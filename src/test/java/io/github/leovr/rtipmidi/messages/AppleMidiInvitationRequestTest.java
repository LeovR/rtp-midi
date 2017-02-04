package io.github.leovr.rtipmidi.messages;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class AppleMidiInvitationRequestTest {

    private final byte[] expectedMessage =
            {(byte) 0xff, (byte) 0xff, (byte) 0x49, (byte) 0x4e, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x02,
                    (byte) 0x00, (byte) 0x16, (byte) 0x3d, (byte) 0x91, (byte) 0x75, (byte) 0xb1, (byte) 0xc5,
                    (byte) 0x1d, (byte) 0x74, (byte) 0x65, (byte) 0x73, (byte) 0x74, (byte) 0x00};

    @Test
    public void testInvitation() throws Exception {
        final AppleMidiInvitationRequest invitationRequest =
                new AppleMidiInvitationRequest(2, 1457553, 1974584605, "test");

        assertThat(invitationRequest.toByteArray()).inHexadecimal().containsExactly(expectedMessage);
    }

}