package io.github.leovr.rtipmidi;

import io.github.leovr.rtipmidi.messages.AppleMidiClockSynchronization;
import io.github.leovr.rtipmidi.messages.AppleMidiEndSession;
import io.github.leovr.rtipmidi.messages.AppleMidiInvitationRequest;
import io.github.leovr.rtipmidi.model.AppleMidiServer;

import javax.annotation.Nonnull;

public interface AppleMidiCommandListener {

    void onMidiInvitation(@Nonnull final AppleMidiInvitationRequest invitation,
                          @Nonnull final AppleMidiServer appleMidiServer);

    void onClockSynchronization(@Nonnull final AppleMidiClockSynchronization clockSynchronization,
                                @Nonnull final AppleMidiServer appleMidiServer);

    void onEndSession(@Nonnull AppleMidiEndSession appleMidiEndSession, @Nonnull AppleMidiServer appleMidiServer);
}
