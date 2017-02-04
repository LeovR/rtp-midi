package io.github.leovr.rtipmidi;

import io.github.leovr.rtipmidi.messages.AppleMidiClockSynchronization;
import io.github.leovr.rtipmidi.messages.AppleMidiEndSession;
import io.github.leovr.rtipmidi.messages.AppleMidiInvitationRequest;
import io.github.leovr.rtipmidi.messages.MidiCommandHeader;
import io.github.leovr.rtipmidi.model.AppleMidiServer;
import lombok.Getter;
import lombok.Setter;

import javax.sound.midi.MidiMessage;
import java.lang.management.ManagementFactory;

@Getter
@Setter
public abstract class AppleMidiSession implements AppleMidiMessageListener, AppleMidiCommandListener {

    private long offsetEstimate;

    public long getCurrentTimestampIn100Microseconds() {
        return ManagementFactory.getRuntimeMXBean().getUptime() * 10;
    }

    @Override
    public final void onMidiMessage(final MidiCommandHeader midiCommandHeader, final MidiMessage message,
                                    final int timestamp) {
        onMidiMessage(message, timestamp + offsetEstimate);
    }

    protected abstract void onMidiMessage(final MidiMessage message, final long timestamp);

    @Override
    public final void onMidiInvitation(final AppleMidiInvitationRequest invitation,
                                       final AppleMidiServer appleMidiServer) {
    }

    @Override
    public final void onClockSynchronization(final AppleMidiClockSynchronization clockSynchronization,
                                             final AppleMidiServer appleMidiServer) {
    }

    @Override
    public final void onEndSession(final AppleMidiEndSession appleMidiEndSession,
                                   final AppleMidiServer appleMidiServer) {
        onEndSession();
    }

    protected void onEndSession() {
    }
}
