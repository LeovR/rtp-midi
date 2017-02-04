package io.github.leovr.rtipmidi.handler;

import io.github.leovr.rtipmidi.AppleMidiCommandListener;
import io.github.leovr.rtipmidi.messages.AppleMidiClockSynchronization;
import io.github.leovr.rtipmidi.messages.AppleMidiEndSession;
import io.github.leovr.rtipmidi.messages.AppleMidiInvitationRequest;
import io.github.leovr.rtipmidi.model.AppleMidiServer;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.net.InetAddress;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class AppleMidiCommandHandlerTest {

    private AppleMidiCommandHandler handler;

    private final byte[] midiInvitationRequest =
            {(byte) 0xff, (byte) 0xff, (byte) 0x49, (byte) 0x4e, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x02,
                    (byte) 0x00, (byte) 0x16, (byte) 0x3d, (byte) 0x91, (byte) 0x75, (byte) 0xb1, (byte) 0xc5,
                    (byte) 0x1d, (byte) 0x74, (byte) 0x65, (byte) 0x73, (byte) 0x74, (byte) 0x00};
    private final byte[] endSession =
            {(byte) 0xff, (byte) 0xff, (byte) 0x42, (byte) 0x59, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x02,
                    (byte) 0xc3, (byte) 0x7a, (byte) 0xa8, (byte) 0xa0, (byte) 0x75, (byte) 0xb1, (byte) 0xc5,
                    (byte) 0x1d};

    private final byte[] clockSynchronization =
            {(byte) 0xff, (byte) 0xff, (byte) 0x43, (byte) 0x4b, (byte) 0x75, (byte) 0xb1, (byte) 0xc5, (byte) 0x1d,
                    (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                    (byte) 0x00, (byte) 0x16, (byte) 0xa8, (byte) 0x3f, (byte) 0xe5, (byte) 0x00, (byte) 0x00,
                    (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                    (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00};

    @Mock
    private AppleMidiCommandListener listener;
    private AppleMidiServer server;
    @Captor
    private ArgumentCaptor<AppleMidiInvitationRequest> invitationRequestCaptor;
    @Captor
    private ArgumentCaptor<AppleMidiEndSession> endSessionArgumentCaptor;
    @Captor
    private ArgumentCaptor<AppleMidiClockSynchronization> clockSynchronizationArgumentCaptor;
    @Mock
    private InetAddress inetAddress;

    @Before
    public void setUp() throws Exception {
        handler = new AppleMidiCommandHandler();
        handler.registerListener(listener);
        server = new AppleMidiServer(inetAddress, 0);
    }

    @Test
    public void testHandleMidiInvitationRequest() throws Exception {
        handler.handle(midiInvitationRequest, server);

        verify(listener).onMidiInvitation(invitationRequestCaptor.capture(), eq(server));

        final AppleMidiInvitationRequest invitationRequest = invitationRequestCaptor.getValue();
        assertThat(invitationRequest).isEqualTo(new AppleMidiInvitationRequest(2, 1457553, 1974584605, "test"));
    }

    @Test
    public void testHandleEndSession() throws Exception {
        handler.handle(endSession, server);

        verify(listener).onEndSession(endSessionArgumentCaptor.capture(), eq(server));

        final AppleMidiEndSession endSession = endSessionArgumentCaptor.getValue();
        assertThat(endSession).isEqualTo(new AppleMidiEndSession(2, 0xc37aa8a0, 0x75b1c51d));
    }

    @Test
    public void testHandleClockSynchronization() throws Exception {
        handler.handle(clockSynchronization, server);

        verify(listener).onClockSynchronization(clockSynchronizationArgumentCaptor.capture(), eq(server));

        final AppleMidiClockSynchronization clockSynchronization = clockSynchronizationArgumentCaptor.getValue();
        assertThat(clockSynchronization)
                .isEqualTo(new AppleMidiClockSynchronization(0x75b1c51d, (byte) 0, 0x16a83fe5, 0, 0));
    }

    @Test
    public void testInvalidHeader1() throws Exception {
        handler.handle(new byte[]{(byte) 0x00}, server);

        verify(listener, never())
                .onClockSynchronization(any(AppleMidiClockSynchronization.class), any(AppleMidiServer.class));
        verify(listener, never()).onEndSession(any(AppleMidiEndSession.class), any(AppleMidiServer.class));
        verify(listener, never()).onMidiInvitation(any(AppleMidiInvitationRequest.class), any(AppleMidiServer.class));
    }

    @Test
    public void testInvalidHeader2() throws Exception {
        handler.handle(new byte[]{(byte) 0xFF, (byte) 0x00}, server);

        verify(listener, never())
                .onClockSynchronization(any(AppleMidiClockSynchronization.class), any(AppleMidiServer.class));
        verify(listener, never()).onEndSession(any(AppleMidiEndSession.class), any(AppleMidiServer.class));
        verify(listener, never()).onMidiInvitation(any(AppleMidiInvitationRequest.class), any(AppleMidiServer.class));
    }

    @Test
    public void testInvalidCommand() throws Exception {
        handler.handle(new byte[]{(byte) 0xFF, (byte) 0xFF, (byte) 0x00}, server);

        verify(listener, never())
                .onClockSynchronization(any(AppleMidiClockSynchronization.class), any(AppleMidiServer.class));
        verify(listener, never()).onEndSession(any(AppleMidiEndSession.class), any(AppleMidiServer.class));
        verify(listener, never()).onMidiInvitation(any(AppleMidiInvitationRequest.class), any(AppleMidiServer.class));
    }

    @Test
    public void testInvalidCommandWord() throws Exception {
        handler.handle(new byte[]{(byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF}, server);

        verify(listener, never())
                .onClockSynchronization(any(AppleMidiClockSynchronization.class), any(AppleMidiServer.class));
        verify(listener, never()).onEndSession(any(AppleMidiEndSession.class), any(AppleMidiServer.class));
        verify(listener, never()).onMidiInvitation(any(AppleMidiInvitationRequest.class), any(AppleMidiServer.class));
    }
}
