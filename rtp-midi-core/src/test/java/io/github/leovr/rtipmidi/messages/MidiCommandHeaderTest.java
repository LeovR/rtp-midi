package io.github.leovr.rtipmidi.messages;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class MidiCommandHeaderTest {

    @Test
    public void testToByteArray() throws Exception {
        final RtpHeader rtpHeader =
                new RtpHeader((byte) 2, false, false, (byte) 0, false, (byte) 97, (short) 15, 2004447, 0xc203859c);
        final MidiCommandHeader midiCommandHeader =
                new MidiCommandHeader(false, false, false, false, (short) 3, rtpHeader);

        assertThat(midiCommandHeader.toByteArray()).inHexadecimal().containsSequence(0x9c, 0x03);
    }

    @Test
    public void testToByteArrayLongHeader() throws Exception {
        final RtpHeader rtpHeader =
                new RtpHeader((byte) 2, false, false, (byte) 0, false, (byte) 97, (short) 15, 2004447, 0xc203859c);
        final MidiCommandHeader midiCommandHeader =
                new MidiCommandHeader(true, false, false, false, (short) 300, rtpHeader);

        assertThat(midiCommandHeader.toByteArray()).inHexadecimal().containsSequence(0x9c, 0x81, 0x2c);

    }
}
