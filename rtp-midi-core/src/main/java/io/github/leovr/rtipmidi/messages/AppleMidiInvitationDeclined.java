package io.github.leovr.rtipmidi.messages;

import lombok.EqualsAndHashCode;
import lombok.ToString;

@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class AppleMidiInvitationDeclined extends AppleMidiInvitation {

    public AppleMidiInvitationDeclined(final int protocolVersion, final int initiatorToken, final int ssrc,
                                       final String name) {
        super(CommandWord.NO, protocolVersion, initiatorToken, ssrc, name);
    }

}
