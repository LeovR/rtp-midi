package io.github.leovr.rtipmidi;

import io.github.leovr.rtipmidi.error.AppleMidiSessionInstantiationException;
import io.github.leovr.rtipmidi.session.AppleMidiSession;

import javax.annotation.Nonnull;
import javax.sound.midi.MidiMessage;
import javax.sound.midi.Receiver;
import java.util.Collections;
import java.util.List;

public class MidiReceiverAppleMidiSession extends AppleMidiSession {

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
        receivers.forEach(receiver -> receiver.send(message, timestamp));
    }
}
