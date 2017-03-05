package io.github.leovr.rtipmidi;

import io.github.leovr.rtipmidi.messages.AppleMidiClockSynchronization;
import io.github.leovr.rtipmidi.messages.AppleMidiInvitationRequest;
import io.github.leovr.rtipmidi.model.AppleMidiServer;

import javax.annotation.Nonnull;

public interface AppleMidiCommandListener extends EndSessionListener {

    /**
     * This method is called for every invitation request.
     *
     * @param invitation      The invitation request
     * @param appleMidiServer The origin server of this message
     */
    void onMidiInvitation(@Nonnull final AppleMidiInvitationRequest invitation,
                          @Nonnull final AppleMidiServer appleMidiServer);

    /**
     * This method is called for every clock synchronization request.
     *
     * @param clockSynchronization The clock synchronization request
     * @param appleMidiServer      The origin server of this message
     */
    void onClockSynchronization(@Nonnull final AppleMidiClockSynchronization clockSynchronization,
                                @Nonnull final AppleMidiServer appleMidiServer);

}
