package io.github.leovr.rtipmidi;

import io.github.leovr.rtipmidi.model.MidiMessage;
import io.github.leovr.rtipmidi.session.AppleMidiSession;
import lombok.AccessLevel;
import lombok.Setter;

/**
 * {@link AppleMidiSession} which converts {@link MidiMessage} to {@link javax.sound.midi.MidiMessage}
 */
public abstract class JavaxAppleMidiSession extends AppleMidiSession {

    @Setter(AccessLevel.PACKAGE)
    private JavaxMidiMessageConverter midiMessageConverter = new JavaxMidiMessageConverter();

    @Override
    protected void onMidiMessage(final MidiMessage message, final long timestamp) {
        onMidiMessage(midiMessageConverter.convert(message), timestamp);
    }

    /**
     * This method is called when a new {@link javax.sound.midi.MidiMessage} is received
     *
     * @param message   The MIDI-message
     * @param timestamp The timestamp of this message
     */
    protected void onMidiMessage(final javax.sound.midi.MidiMessage message, final long timestamp) {
    }

    /**
     * This method sends a {@link javax.sound.midi.MidiMessage}
     *
     * @param message   The message to deliver
     * @param timestamp The timestamp of this message
     */
    protected void sendMidiMessage(final javax.sound.midi.MidiMessage message, final long timestamp) {
        final MidiMessage midiMessage = midiMessageConverter.convert(message);
        sendMidiMessage(midiMessage, timestamp);
    }
}
