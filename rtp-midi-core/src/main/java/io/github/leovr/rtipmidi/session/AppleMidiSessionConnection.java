package io.github.leovr.rtipmidi.session;

import io.github.leovr.rtipmidi.messages.AppleMidiMessage;
import io.github.leovr.rtipmidi.messages.MidiCommandHeader;
import io.github.leovr.rtipmidi.messages.MidiTimestampPair;
import io.github.leovr.rtipmidi.messages.RtpHeader;
import io.github.leovr.rtipmidi.model.AppleMidiServer;
import io.github.leovr.rtipmidi.model.MidiMessage;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.util.Collections;
import java.util.Random;

/**
 * This class represents a connection between the local session and the remote {@link AppleMidiServer}. The connections
 * is able to send {@link MidiMessage}s to the {@link AppleMidiMessageSender}.
 */
@Getter
@Slf4j
class AppleMidiSessionConnection implements AppleMidiSessionSender {

    @Nonnull
    private final AppleMidiSession appleMidiSession;
    @Nonnull
    private final AppleMidiServer appleMidiServer;
    private final int ssrc;
    @Nonnull
    private final AppleMidiMessageSender appleMidiMessageSender;

    @Getter(AccessLevel.NONE)
    private short sequenceNumber = (short) new Random().nextInt(Short.MAX_VALUE + 1);

    public AppleMidiSessionConnection(@Nonnull final AppleMidiSession appleMidiSession,
                                      @Nonnull final AppleMidiServer appleMidiServer, final int ssrc,
                                      @Nonnull final AppleMidiMessageSender appleMidiMessageSender) {
        this.appleMidiSession = appleMidiSession;
        this.appleMidiServer = appleMidiServer;
        this.ssrc = ssrc;
        this.appleMidiMessageSender = appleMidiMessageSender;
    }

    @Override
    public void sendMidiMessage(@Nonnull final MidiMessage message, final long timestamp) {
        sequenceNumber++;
        final long currentTimeIn100Microseconds = appleMidiSession.getCurrentTimestamp();
        final int rtpTimestamp = ((int) currentTimeIn100Microseconds);
        final RtpHeader rtpHeader =
                new RtpHeader((byte) 2, false, false, (byte) 0, false, (byte) 97, sequenceNumber, rtpTimestamp, ssrc);
        log.trace("Sending RTP-Header: {}", rtpHeader);
        final boolean b = message.getLength() > 15;
        final MidiCommandHeader midiCommandHeader =
                new MidiCommandHeader(b, false, false, false, ((short) message.getLength()), rtpHeader);
        final AppleMidiMessage appleMidiMessage =
                new AppleMidiMessage(midiCommandHeader, Collections.singletonList(new MidiTimestampPair(0, message)));

        try {
            appleMidiMessageSender.send(appleMidiMessage, appleMidiServer);
        } catch (final IOException e) {
            log.error("Error sending MidiMessage to {}", appleMidiServer, e);
        }
    }
}
