package io.github.leovr.rtipmidi.session;

import io.github.leovr.rtipmidi.AppleMidiCommandListener;
import io.github.leovr.rtipmidi.messages.AppleMidiClockSynchronization;
import io.github.leovr.rtipmidi.messages.AppleMidiEndSession;
import io.github.leovr.rtipmidi.model.AppleMidiServer;
import io.github.leovr.rtipmidi.AppleMidiMessageListener;
import io.github.leovr.rtipmidi.messages.AppleMidiInvitationRequest;
import io.github.leovr.rtipmidi.messages.MidiCommandHeader;
import lombok.Getter;
import lombok.Setter;

import javax.annotation.Nonnull;
import javax.sound.midi.MidiMessage;
import java.lang.management.ManagementFactory;

/**
 * This class represents a single MIDI session with a remote server. It simplifies the methods of {@link
 * AppleMidiMessageListener} and {@link AppleMidiCommandListener} for the subclasses.
 */
@Getter
@Setter
public abstract class AppleMidiSession implements AppleMidiMessageListener, AppleMidiCommandListener {

    private long offsetEstimate;

    /**
     * Returns the current timestamp in 100 microseconds. The default implementation uses the JVM startup time as
     * reference.
     *
     * @return The timestamp in 100 microseconds or -1 if the session does not care about the timestamp
     */
    public long getCurrentTimestamp() {
        return ManagementFactory.getRuntimeMXBean().getUptime() * 10;
    }

    @Override
    public final void onMidiMessage(final MidiCommandHeader midiCommandHeader, final MidiMessage message,
                                    final int timestamp) {
        onMidiMessage(message, timestamp + offsetEstimate);
    }

    /**
     * Called for every received MIDI messages
     *
     * @param message   The MIDI message
     * @param timestamp The timestamp of the message
     */
    protected abstract void onMidiMessage(final MidiMessage message, final long timestamp);

    @Override
    public final void onMidiInvitation(@Nonnull final AppleMidiInvitationRequest invitation,
                                       @Nonnull final AppleMidiServer appleMidiServer) {
    }

    @Override
    public final void onClockSynchronization(@Nonnull final AppleMidiClockSynchronization clockSynchronization,
                                             @Nonnull final AppleMidiServer appleMidiServer) {
    }

    @Override
    public final void onEndSession(@Nonnull final AppleMidiEndSession appleMidiEndSession,
                                   @Nonnull final AppleMidiServer appleMidiServer) {
        onEndSession();
    }

    /**
     * Called on end session. Any clean-up can happen here.
     */
    protected void onEndSession() {
    }
}
