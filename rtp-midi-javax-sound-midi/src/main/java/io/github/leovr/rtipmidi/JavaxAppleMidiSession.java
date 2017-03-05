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

    protected abstract void onMidiMessage(final javax.sound.midi.MidiMessage message, final long timestamp);
}
