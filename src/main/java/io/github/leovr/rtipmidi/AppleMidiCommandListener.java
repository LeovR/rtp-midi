package io.github.leovr.rtipmidi;

import io.github.leovr.rtipmidi.messages.AppleMidiClockSynchronization;
import io.github.leovr.rtipmidi.messages.AppleMidiEndSession;
import io.github.leovr.rtipmidi.messages.AppleMidiInvitationRequest;
import io.github.leovr.rtipmidi.model.AppleMidiServer;

public interface AppleMidiCommandListener {

    void onMidiInvitation(final AppleMidiInvitationRequest invitation, final AppleMidiServer appleMidiServer);

    void onClockSynchronization(final AppleMidiClockSynchronization clockSynchronization,
                                final AppleMidiServer appleMidiServer);

    void onEndSession(AppleMidiEndSession appleMidiEndSession, AppleMidiServer appleMidiServer);
}
