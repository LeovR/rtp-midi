package io.github.leovr.rtipmidi.messages;

import lombok.EqualsAndHashCode;
import lombok.ToString;

@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class AppleMidiInvitationRequest extends AppleMidiInvitation {

    public AppleMidiInvitationRequest(final int protocolVersion, final int initiatorToken, final int ssrc,
                                      final String name) {
        super(CommandWord.IN, protocolVersion, initiatorToken, ssrc, name);
    }

}
