package io.github.leovr.rtipmidi.messages;

import lombok.ToString;

@ToString(callSuper = true)
public class AppleMidiInvitationAccepted extends AppleMidiInvitation {

    public AppleMidiInvitationAccepted(final int protocolVersion, final int initiatorToken, final int ssrc,
                                       final String name) {
        super(CommandWord.OK, protocolVersion, initiatorToken, ssrc, name);
    }

}
