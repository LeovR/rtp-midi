package io.github.leovr.rtipmidi.session;

import io.github.leovr.rtipmidi.model.MidiMessage;

import javax.annotation.Nonnull;

/**
 * The implementation of this interface is responsible for sending {@link MidiMessage} over the network.
 */
public interface AppleMidiSessionSender {

    /**
     * Sends a {@link MidiMessage} over the network
     *
     * @param message   The message to deliver
     * @param timestamp The timestamp of this message
     */
    void sendMidiMessage(@Nonnull final MidiMessage message, final long timestamp);
}
