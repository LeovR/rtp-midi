package io.github.leovr.rtipmidi;

import io.github.leovr.rtipmidi.error.AppleMidiSessionInstantiationException;
import io.github.leovr.rtipmidi.session.AppleMidiSession;

import javax.annotation.Nonnull;
import javax.sound.midi.MidiMessage;
import javax.sound.midi.Receiver;
import java.util.Collections;
import java.util.List;

/**
 * {@link AppleMidiSession} with one or more {@link Receiver} as the receiver(s) of the MIDI messages
 */
public class MidiReceiverAppleMidiSession extends JavaxAppleMidiSession {

    private final List<Receiver> receivers;

    public MidiReceiverAppleMidiSession(@Nonnull final Receiver receiver) throws
            AppleMidiSessionInstantiationException {
        this(Collections.singletonList(receiver));
    }

    public MidiReceiverAppleMidiSession(@Nonnull final List<Receiver> receivers) {
        this.receivers = receivers;
    }

    @Override
    protected void onMidiMessage(final MidiMessage message, final long timestamp) {
        for (final Receiver receiver : receivers) {
            receiver.send(message, timestamp);
        }
    }
}
