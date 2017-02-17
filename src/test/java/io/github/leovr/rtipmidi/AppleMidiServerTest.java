package io.github.leovr.rtipmidi;

import org.junit.Test;

public class AppleMidiServerTest {

    @Test
    public void testAppleMidiServerStopWithoutStart() throws Exception {
        final AppleMidiServer server = new AppleMidiServer();
        server.stop();
    }
}
