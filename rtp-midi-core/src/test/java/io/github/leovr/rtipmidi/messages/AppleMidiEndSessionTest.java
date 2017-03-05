package io.github.leovr.rtipmidi.messages;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class AppleMidiEndSessionTest {

    private final byte[] expectedMessage =
            {(byte) 0xff, (byte) 0xff, (byte) 0x42, (byte) 0x59, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x02,
                    (byte) 0xc3, (byte) 0x7a, (byte) 0xa8, (byte) 0xa0, (byte) 0x75, (byte) 0xb1, (byte) 0xc5,
                    (byte) 0x1d};

    @Test
    public void testEndSession() throws Exception {
        final AppleMidiEndSession endSession = new AppleMidiEndSession(2, 0xc37aa8a0, 0x75b1c51d);

        assertThat(endSession.toByteArray()).inHexadecimal().containsExactly(expectedMessage);
    }
}
