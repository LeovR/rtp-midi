package io.github.leovr.rtipmidi.messages;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;


public class RtpHeaderTest {

    @Test
    public void testToByteArray() throws Exception {
        final RtpHeader rtpHeader =
                new RtpHeader((byte) 2, false, false, (byte) 1, false, (byte) 97, (short) 15, 2004447, 0xc203859c);

        assertThat(rtpHeader.toByteArray()).inHexadecimal()
                .containsExactly(0x81, 0x61, 0x00, 0x0F, 0x00, 0x1e, 0x95, 0xdf, 0xc2, 0x03, 0x85, 0x9c);
    }
}
