package io.github.leovr.rtipmidi;

import io.github.leovr.rtipmidi.error.AppleMidiSessionInstantiationException;
import io.github.leovr.rtipmidi.session.AppleMidiSession;

import javax.annotation.Nonnull;
import javax.sound.midi.MidiDevice;
import javax.sound.midi.MidiMessage;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Receiver;
import java.util.Objects;

public class MidiDeviceAppleMidiSession extends AppleMidiSession {

    private final Receiver receiver;
    private final MidiDevice midiDevice;

    public MidiDeviceAppleMidiSession(@Nonnull final MidiDevice midiDevice) {
        if (midiDevice.isOpen()) {
            throw new AppleMidiSessionInstantiationException("MIDI-Device is already open");
        }
        if (midiDevice.getMaxReceivers() == 0) {
            throw new AppleMidiSessionInstantiationException(
                    "MIDI-Device cannot have any more receivers or does not support receivers");
        }
        try {
            midiDevice.open();
        } catch (final MidiUnavailableException e) {
            throw new AppleMidiSessionInstantiationException("Error opening MIDI device", e);
        }
        this.midiDevice = midiDevice;
        try {
            this.receiver = midiDevice.getReceiver();
        } catch (final MidiUnavailableException e) {
            throw new AppleMidiSessionInstantiationException("Error getting receiver", e);
        }
    }

    @Override
    protected void onMidiMessage(final MidiMessage message, final long timestamp) {
        receiver.send(message, timestamp);
    }

    @Override
    public long getCurrentTimestamp() {
        final long microsecondPosition = midiDevice.getMicrosecondPosition();
        if (microsecondPosition == -1) {
            return -1;
        }
        return microsecondPosition * 100;
    }

    @Override
    protected void onEndSession() {
        midiDevice.close();
    }
}
