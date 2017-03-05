package io.github.leovr.rtipmidi.session;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import javax.annotation.Nonnull;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.concurrent.Executors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;


@RunWith(MockitoJUnitRunner.class)
public class AppleMidiSessionServerTest {

    private AppleMidiSessionServer server;
    @Mock
    private DatagramSocket socket;
    @Mock
    private Thread thread;

    @Before
    public void setUp() throws Exception {
        server = new AppleMidiSessionServer("test", 0, Executors.newSingleThreadExecutor());
    }

    @Test
    public void testAppleMidiSessionServerStopWithoutStart() throws Exception {
        server.stopServer();
    }

    @Test
    public void testCreateThread() throws Exception {
        final Thread thread = server.createThread("test");
        assertThat(thread).isNotNull();
        assertThat(thread.getName()).isEqualTo("testSessionThread");
    }

    @Test
    public void testStart() throws Exception {
        final AppleMidiSessionServer server = new AppleMidiSessionServer("test", 5) {
            @Override
            Thread createThread(@Nonnull final String name) {
                return thread;
            }

            @Override
            DatagramSocket createSocket() throws SocketException {
                return socket;
            }
        };
        server.start();

        verify(socket).setSoTimeout(1000);
        verify(thread).start();
    }

}
