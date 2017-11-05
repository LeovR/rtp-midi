package io.github.leovr.rtipmidi;

import io.github.leovr.rtipmidi.adapter.ShortMessageAdapter;
import io.github.leovr.rtipmidi.adapter.SysexMessageAdapter;
import io.github.leovr.rtipmidi.model.ShortMessage;
import io.github.leovr.rtipmidi.model.SysexMessage;
import org.junit.Test;

import javax.sound.midi.MidiMessage;

import static org.assertj.core.api.Assertions.assertThat;

public class JavaxMidiMessageConverterTest {

    private final JavaxMidiMessageConverter converter = new JavaxMidiMessageConverter();

    @Test
    public void testShortMessage() throws Exception {
        final ShortMessage message = new ShortMessage((byte) ShortMessage.START);
        final MidiMessage result = converter.convert(message);

        assertThat(result).isInstanceOf(ShortMessageAdapter.class);
        assertThat(result.getMessage()).isEqualTo(message.getData());
    }

    @Test
    public void testSysexMessage() throws Exception {
        final SysexMessage message = new SysexMessage(new byte[]{(byte) SysexMessage.SYSTEM_EXCLUSIVE}, 1);
        final MidiMessage result = converter.convert(message);

        assertThat(result).isInstanceOf(SysexMessageAdapter.class);
        assertThat(result.getMessage()).isEqualTo(message.getData());
    }

    @Test
    public void testShortMessageReverse() throws Exception {
        final javax.sound.midi.ShortMessage message =
                new javax.sound.midi.ShortMessage((byte) javax.sound.midi.ShortMessage.NOTE_ON, 71, 100);
        final io.github.leovr.rtipmidi.model.MidiMessage result = converter.convert(message);

        assertThat(result.getData()).isEqualTo(message.getMessage());
    }

    @Test
    public void testSysexMessageReverse() throws Exception {
        final javax.sound.midi.SysexMessage message =
                new javax.sound.midi.SysexMessage(new byte[]{(byte) SysexMessage.SYSTEM_EXCLUSIVE, 0x00}, 2);
        final io.github.leovr.rtipmidi.model.MidiMessage result = converter.convert(message);

        assertThat(result.getData()).isEqualTo(message.getMessage());
    }
}
