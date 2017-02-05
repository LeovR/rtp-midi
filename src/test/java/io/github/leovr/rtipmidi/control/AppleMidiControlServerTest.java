package io.github.leovr.rtipmidi.control;

import io.github.leovr.rtipmidi.handler.AppleMidiCommandHandler;
import io.github.leovr.rtipmidi.messages.AppleMidiEndSession;
import io.github.leovr.rtipmidi.messages.AppleMidiInvitationAccepted;
import io.github.leovr.rtipmidi.messages.AppleMidiInvitationDeclined;
import io.github.leovr.rtipmidi.messages.AppleMidiInvitationRequest;
import io.github.leovr.rtipmidi.model.AppleMidiServer;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class AppleMidiControlServerTest {

    private AppleMidiControlServer server;
    @Mock
    private DatagramSocket socket;
    @Mock
    private InetAddress inetAddress;
    @Mock
    private DatagramPacket datagramPacket;
    @Mock
    private AppleMidiCommandHandler handler;
    private AppleMidiServer appleMidiServer;
    @Captor
    private ArgumentCaptor<DatagramPacket> datagramPacketArgumentCaptor;

    @Before
    public void setUp() throws Exception {
        server = new AppleMidiControlServer(handler, 17, "test", 0) {
            @Override
            DatagramSocket initDatagramSocket() throws SocketException {
                return socket;
            }

            @Override
            DatagramPacket initDatagramPacket(final byte[] receiveData) {
                return datagramPacket;
            }

            @Override
            int getNewInitiatorToken() {
                return 18;
            }
        };
        given(datagramPacket.getAddress()).willReturn(inetAddress);
        given(datagramPacket.getPort()).willReturn(5555);
        appleMidiServer = new AppleMidiServer(inetAddress, 5555);
    }

    @Test
    public void testHandling() throws Exception {
        server.start();
        Thread.sleep(1000);
        server.stopServer();
        server.join();
        verify(socket, atLeastOnce()).receive(datagramPacket);
        verify(handler, atLeastOnce()).handle(any(byte[].class), eq(appleMidiServer));
        verify(socket).close();
    }

    @Test
    public void testAcceptMidiInvitation() throws Exception {
        server.start();
        server.setMaxNumberOfSessions(1);

        server.onMidiInvitation(new AppleMidiInvitationRequest(2, 1, 1, "initiator"), appleMidiServer);
        verify(socket).send(datagramPacketArgumentCaptor.capture());
        assertThat(datagramPacketArgumentCaptor.getValue().getData()).inHexadecimal()
                .isEqualTo(new AppleMidiInvitationAccepted(2, 1, 17, "test").toByteArray());

        server.stopServer();
        verify(socket, times(2)).send(datagramPacketArgumentCaptor.capture());
        server.join();

        assertThat(datagramPacketArgumentCaptor.getValue().getData()).inHexadecimal()
                .isEqualTo(new AppleMidiEndSession(2, 18, 17).toByteArray());
        verify(socket).close();
    }

    @Test
    public void testDeclineMidiInvitation() throws Exception {
        server.start();

        server.onMidiInvitation(new AppleMidiInvitationRequest(2, 1, 1, "initiator"), appleMidiServer);
        verify(socket).send(datagramPacketArgumentCaptor.capture());
        assertThat(datagramPacketArgumentCaptor.getValue().getData()).inHexadecimal()
                .isEqualTo(new AppleMidiInvitationDeclined(2, 1, 17, "test").toByteArray());

        server.stopServer();
        verify(socket).send(any(DatagramPacket.class));
        server.join();
        verify(socket).close();
    }

    @Test
    public void testEndSession() throws Exception {
        server.start();
        server.setMaxNumberOfSessions(1);

        server.onMidiInvitation(new AppleMidiInvitationRequest(2, 1, 1, "initiator"), appleMidiServer);
        verify(socket).send(datagramPacketArgumentCaptor.capture());
        assertThat(datagramPacketArgumentCaptor.getValue().getData()).inHexadecimal()
                .isEqualTo(new AppleMidiInvitationAccepted(2, 1, 17, "test").toByteArray());

        server.onEndSession(new AppleMidiEndSession(2, 1, 17), appleMidiServer);

        server.stopServer();
        verify(socket).send(any(DatagramPacket.class));
        server.join();

        verify(socket).close();
    }

}
