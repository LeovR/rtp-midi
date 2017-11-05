package io.github.leovr.rtipmidi;

import io.github.leovr.rtipmidi.error.AppleMidiSessionInstantiationException;
import io.github.leovr.rtipmidi.session.AppleMidiSession;

import javax.annotation.Nonnull;
import javax.sound.midi.MidiMessage;
import javax.sound.midi.Receiver;
import javax.sound.midi.Transmitter;
import java.util.Collections;
import java.util.List;

/**
 * {@link AppleMidiSession} with one or more {@link Transmitter} as the sender(s) of the MIDI messages
 */
public class MidiTransmitterAppleMidiSession extends JavaxAppleMidiSession {

    private final List<Transmitter> transmitters;

    public MidiTransmitterAppleMidiSession(@Nonnull final Transmitter transmitter) throws
            AppleMidiSessionInstantiationException {
        this(Collections.singletonList(transmitter));
    }

    public MidiTransmitterAppleMidiSession(@Nonnull final List<Transmitter> transmitters) {
        this.transmitters = transmitters;

        for (final Transmitter transmitter : transmitters) {
            transmitter.setReceiver(new SimpleReceiver());
        }
    }

    private class SimpleReceiver implements Receiver {

        @Override
        public void send(final MidiMessage message, final long timeStamp) {
            sendMidiMessage(message, timeStamp);
        }

        @Override
        public void close() {
        }
    }
}
