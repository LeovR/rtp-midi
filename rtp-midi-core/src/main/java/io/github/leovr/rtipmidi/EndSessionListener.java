package io.github.leovr.rtipmidi;

import io.github.leovr.rtipmidi.messages.AppleMidiEndSession;

import javax.annotation.Nonnull;

public interface EndSessionListener {

    /**
     * This method is called when the origin server ends this session
     *
     * @param appleMidiEndSession The end session request
     * @param appleMidiServer     The origin server of this message
     */
    void onEndSession(@Nonnull AppleMidiEndSession appleMidiEndSession,
                      @Nonnull io.github.leovr.rtipmidi.model.AppleMidiServer appleMidiServer);
}
