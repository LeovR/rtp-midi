package io.github.leovr.rtipmidi.handler;

import io.github.leovr.rtipmidi.AppleMidiCommandListener;
import io.github.leovr.rtipmidi.messages.AppleMidiClockSynchronization;
import io.github.leovr.rtipmidi.messages.AppleMidiEndSession;
import io.github.leovr.rtipmidi.messages.AppleMidiInvitationRequest;
import io.github.leovr.rtipmidi.model.AppleMidiServer;
import lombok.extern.slf4j.Slf4j;

@Slf4j
class AppleMidiCommandLogListener implements AppleMidiCommandListener {

    @Override
    public void onMidiInvitation(final AppleMidiInvitationRequest invitation, final AppleMidiServer appleMidiServer) {
        log.trace("MIDI invitation: invitation: {}, appleMidiServer: {}", invitation, appleMidiServer);
    }

    @Override
    public void onClockSynchronization(final AppleMidiClockSynchronization clockSynchronization,
                                       final AppleMidiServer appleMidiServer) {
        log.trace("MIDI clock synchronization: clockSynchronization: {}, appleMidiServer: {}", clockSynchronization,
                appleMidiServer);
    }

    @Override
    public void onEndSession(final AppleMidiEndSession appleMidiEndSession, final AppleMidiServer appleMidiServer) {
        log.trace("MIDI end session: appleMidiEndSession: {}, appleMidiServer: {}", appleMidiEndSession,
                appleMidiServer);
    }
}
