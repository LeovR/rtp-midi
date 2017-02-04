package io.github.leovr.rtipmidi.messages;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class AppleMidiInvitationAcceptedTest {

    private final byte[] expectedMessage =
            {(byte) 0xff, (byte) 0xff, (byte) 0x4f, (byte) 0x4b, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x02,
                    (byte) 0x00, (byte) 0x16, (byte) 0x3d, (byte) 0x91, (byte) 0x27, (byte) 0x8e, (byte) 0x7d,
                    (byte) 0x1c, (byte) 0x74, (byte) 0x65, (byte) 0x73, (byte) 0x74, (byte) 0x00};

    @Test
    public void testInvitationAccepted() throws Exception {
        final AppleMidiInvitationAccepted accepted = new AppleMidiInvitationAccepted(2, 1457553, 663649564, "test");

        assertThat(accepted.toByteArray()).inHexadecimal().containsExactly(expectedMessage);
    }

}
