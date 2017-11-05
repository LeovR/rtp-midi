package io.github.leovr.rtipmidi;


import io.github.leovr.rtipmidi.adapter.ShortMessageAdapter;
import io.github.leovr.rtipmidi.adapter.SysexMessageAdapter;
import io.github.leovr.rtipmidi.model.ShortMessage;
import io.github.leovr.rtipmidi.model.SysexMessage;

import javax.sound.midi.MidiMessage;

/**
 * Converter class to convert from {@link io.github.leovr.rtipmidi.model.MidiMessage} to {@link MidiMessage}
 */
class JavaxMidiMessageConverter {

    MidiMessage convert(final io.github.leovr.rtipmidi.model.MidiMessage message) {
        if (message instanceof ShortMessage) {
            return handleShortMessage((ShortMessage) message);
        } else if (message instanceof SysexMessage) {
            return handleSysexMessage((SysexMessage) message);
        }
        throw new IllegalArgumentException("Message could not be converted");
    }

    io.github.leovr.rtipmidi.model.MidiMessage convert(final MidiMessage message) {
        if (message instanceof javax.sound.midi.ShortMessage) {
            final javax.sound.midi.ShortMessage shortMessage = (javax.sound.midi.ShortMessage) message;
            return new ShortMessage((byte) shortMessage.getCommand(), (byte) shortMessage.getData1(),
                    (byte) shortMessage.getData2());
        } else if (message instanceof javax.sound.midi.SysexMessage) {
            final javax.sound.midi.SysexMessage sysexMessage = (javax.sound.midi.SysexMessage) message;
            return new SysexMessage(sysexMessage.getMessage(), sysexMessage.getLength());
        }
        throw new IllegalArgumentException("Message could not be converted");
    }

    private MidiMessage handleSysexMessage(final SysexMessage message) {
        return new SysexMessageAdapter(message);
    }

    private MidiMessage handleShortMessage(final ShortMessage message) {
        return new ShortMessageAdapter(message);
    }

}
