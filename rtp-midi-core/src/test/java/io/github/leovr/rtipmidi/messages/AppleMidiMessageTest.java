package io.github.leovr.rtipmidi.messages;

import io.github.leovr.rtipmidi.model.ShortMessage;
import org.junit.Test;

import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;

public class AppleMidiMessageTest {

    @Test
    public void testToByteArray() throws Exception {
        final RtpHeader rtpHeader =
                new RtpHeader((byte) 2, false, false, (byte) 0, false, (byte) 97, (short) 15, 2004447, 0xc203859c);
        final MidiCommandHeader midiCommandHeader =
                new MidiCommandHeader(false, false, false, false, (short) 3, rtpHeader);
        final AppleMidiMessage message = new AppleMidiMessage(midiCommandHeader, Collections
                .singletonList(new MidiTimestampPair(0, new ShortMessage((byte) 0x90, (byte) 0x47, (byte) 0x64))));

        assertThat(message.toByteArray()).inHexadecimal().containsSequence(0x9c, 0x03, 0x90, 0x47, 0x64);
    }

    @Test
    public void testToByteArrayWithTimestamp() throws Exception {
        final RtpHeader rtpHeader =
                new RtpHeader((byte) 2, false, false, (byte) 0, false, (byte) 97, (short) 15, 2004447, 0xc203859c);
        final MidiCommandHeader midiCommandHeader =
                new MidiCommandHeader(false, false, true, false, (short) 3, rtpHeader);
        final AppleMidiMessage message = new AppleMidiMessage(midiCommandHeader, Collections.singletonList(
                new MidiTimestampPair(1000001, new ShortMessage((byte) 0x90, (byte) 0x47, (byte) 0x64))));

        assertThat(message.toByteArray()).inHexadecimal()
                .containsSequence(0x9c, 0x23, 0xbd, 0x84, 0x41, 0x90, 0x47, 0x64);
    }

}
