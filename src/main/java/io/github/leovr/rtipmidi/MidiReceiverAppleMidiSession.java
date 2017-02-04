package io.github.leovr.rtipmidi;

import io.github.leovr.rtipmidi.error.AppleMidiSessionInstantiationException;
import io.github.leovr.rtipmidi.session.AppleMidiSession;

import javax.sound.midi.MidiMessage;
import javax.sound.midi.Receiver;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class MidiReceiverAppleMidiSession extends AppleMidiSession {

    private final List<Receiver> receivers;

    public MidiReceiverAppleMidiSession(final Receiver receiver) throws AppleMidiSessionInstantiationException {
        this(Collections.singletonList(receiver));
        Objects.requireNonNull(receiver, "receiver is null");
    }

    public MidiReceiverAppleMidiSession(final List<Receiver> receivers) {
        Objects.requireNonNull(receivers, "receivers cannot be null");
        this.receivers = receivers;
    }

    @Override
    protected void onMidiMessage(final MidiMessage message, final long timestamp) {
        receivers.forEach(receiver -> receiver.send(message, timestamp));
    }
}
