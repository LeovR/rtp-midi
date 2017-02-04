package io.github.leovr.rtipmidi.messages;


import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class AppleMidiClockSynchronizationTest {

    private final byte[] expectedMessage =
            {(byte) 0xff, (byte) 0xff, (byte) 0x43, (byte) 0x4b, (byte) 0x27, (byte) 0x8e, (byte) 0x7d, (byte) 0x1c,
                    (byte) 0x01, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                    (byte) 0x00, (byte) 0x16, (byte) 0xa8, (byte) 0x3f, (byte) 0xe5, (byte) 0x00, (byte) 0x00,
                    (byte) 0x00, (byte) 0x00, (byte) 0x9f, (byte) 0x19, (byte) 0x7b, (byte) 0x7e, (byte) 0x00,
                    (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00};

    @Test
    public void testMidiClockSynchronization() throws Exception {
        final AppleMidiClockSynchronization synchronization =
                new AppleMidiClockSynchronization(663649564, (byte) 1, 380125157, 2669247358L, 0);

        assertThat(synchronization.toByteArray()).inHexadecimal().containsExactly(expectedMessage);
    }
}
