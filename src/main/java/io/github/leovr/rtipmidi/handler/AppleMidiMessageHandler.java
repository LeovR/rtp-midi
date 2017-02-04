package io.github.leovr.rtipmidi.handler;

import io.github.leovr.rtipmidi.AppleMidiMessageListener;
import io.github.leovr.rtipmidi.messages.MidiCommandHeader;
import io.github.leovr.rtipmidi.messages.RtpHeader;
import io.github.leovr.rtipmidi.model.AppleMidiServer;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiMessage;
import javax.sound.midi.ShortMessage;
import javax.sound.midi.SysexMessage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class AppleMidiMessageHandler {

    private static final int RTP_MIDI = 97;
    private final List<AppleMidiMessageListener> listeners = new ArrayList<>();

    public AppleMidiMessageHandler() {
        listeners.add(new AppleMidiMessageLogListener());
    }

    public void handle(final byte[] data, final AppleMidiServer appleMidiServer) {
        try (final DataInputStream dataInputStream = new DataInputStream(new ByteArrayInputStream(data))) {
            final byte header1 = dataInputStream.readByte();
            final byte version = (byte) ((header1 >> 6) & 0x03);
            final boolean paddingFlag = ((header1 >> 5) & 0x01) == 1;
            final boolean extensionFlag = ((header1 >> 4) & 0x01) == 1;
            final byte contributingSourceIdentifiersCount = (byte) (header1 & 0x0F);

            final byte header2 = dataInputStream.readByte();
            final boolean markerFlag = ((header2 >> 7) & 0x01) == 1;
            final byte payloadType = (byte) (header2 & 0x7F);
            if (payloadType != RTP_MIDI) {
                return;
            }

            final short sequenceNumber = dataInputStream.readShort();
            final int timestamp = dataInputStream.readInt();
            final int ssrc = dataInputStream.readInt();

            final RtpHeader rtpHeader =
                    new RtpHeader(version, paddingFlag, extensionFlag, contributingSourceIdentifiersCount, markerFlag,
                            payloadType, sequenceNumber, timestamp, ssrc);

            final byte midiCommandHeader1 = dataInputStream.readByte();
            final boolean b = ((midiCommandHeader1 >> 7) & 0x01) == 1;
            final boolean j = ((midiCommandHeader1 >> 6) & 0x01) == 1;
            final boolean z = ((midiCommandHeader1 >> 5) & 0x01) == 1;
            final boolean p = ((midiCommandHeader1 >> 4) & 0x01) == 1;
            // Header 2 octets
            final short length;
            if (b) {
                final byte midiCommandHeader2 = dataInputStream.readByte();
                length = (short) ((midiCommandHeader1 << 8 | midiCommandHeader2) & 0x0FFF);
            } else {
                length = (short) (midiCommandHeader1 & 0x0F);
            }

            final MidiCommandHeader midiCommandHeader = new MidiCommandHeader(b, j, z, p, length, rtpHeader);

            final byte[] midiCommandBuffer = new byte[length];
            final int midiCommandBytesRead = dataInputStream.read(midiCommandBuffer);
            if (((short) midiCommandBytesRead) != length) {
                return;
            }

            try (final DataInputStream midiInputStream = new DataInputStream(
                    new ByteArrayInputStream(midiCommandBuffer))) {
                readMidiMessages(midiCommandHeader, midiInputStream);
            }

        } catch (final IOException e) {
            log.error("IOException while processing MIDI message", e);
        }
    }

    @Value
    private static class MidiTimestampPair {

        private int timestamp;
        private MidiMessage midiMessage;
    }

    private void readMidiMessages(final MidiCommandHeader midiCommandHeader,
                                  final DataInputStream midiInputStream) throws IOException {
        final List<MidiTimestampPair> result = new ArrayList<>();

        int status = -1;
        ByteArrayOutputStream sysexDataStream = null;
        int deltaTimeSum = midiCommandHeader.getRtpHeader().getTimestamp();
        while (midiInputStream.available() > 0) {

            final int deltaTime;
            if (midiCommandHeader.isZ()) {
                deltaTime = readDeltaTime(midiInputStream);
            } else {
                deltaTime = 0;
            }
            deltaTimeSum += deltaTime;

            try {
                final byte midiOctet1 = midiInputStream.readByte();
                final boolean systemCommonMessage = (midiOctet1 & 0xF0) == 0xF0;
                final int possibleStatus = midiOctet1 & 0xFF;
                if (systemCommonMessage) {
                    if (midiOctet1 == (byte) SysexMessage.SYSTEM_EXCLUSIVE) {
                        sysexDataStream = new ByteArrayOutputStream();
                        sysexDataStream.write(midiOctet1);
                        final boolean partial = readSysexData(midiInputStream, sysexDataStream);
                        if (!partial) {
                            result.add(new MidiTimestampPair(deltaTimeSum,
                                    new SysexMessage(sysexDataStream.toByteArray(), sysexDataStream.size())));
                        }
                    } else if (midiOctet1 == (byte) SysexMessage.SPECIAL_SYSTEM_EXCLUSIVE) {
                        final boolean partial = readSysexData(midiInputStream, sysexDataStream);
                        if (!partial) {
                            result.add(new MidiTimestampPair(deltaTimeSum,
                                    new SysexMessage(sysexDataStream.toByteArray(), sysexDataStream.size())));
                        }
                    } else if (midiOctet1 == (byte) 0xF4) {
                        sysexDataStream.reset();
                    } else {
                        ShortMessage shortMessage = null;
                        switch (possibleStatus) {
                            case ShortMessage.TUNE_REQUEST:
                            case ShortMessage.TIMING_CLOCK:
                            case ShortMessage.START:
                            case ShortMessage.STOP:
                            case ShortMessage.ACTIVE_SENSING:
                            case ShortMessage.SYSTEM_RESET:
                                shortMessage = new ShortMessage(status);
                                break;
                            case ShortMessage.MIDI_TIME_CODE:
                            case ShortMessage.SONG_SELECT:
                                status = possibleStatus;
                                shortMessage = new ShortMessage(status, midiInputStream.readByte() & 0xFF, 0);
                                break;
                            case ShortMessage.SONG_POSITION_POINTER:
                                status = possibleStatus;
                                shortMessage = new ShortMessage(status, midiInputStream.readByte() & 0xFF,
                                        midiInputStream.readByte() & 0xFF);
                                break;
                        }
                        result.add(new MidiTimestampPair(deltaTimeSum, shortMessage));
                    }
                } else {
                    ShortMessage shortMessage = null;
                    switch (midiOctet1 & 0xF0) {
                        case ShortMessage.NOTE_OFF:
                        case ShortMessage.NOTE_ON:
                        case ShortMessage.POLY_PRESSURE:
                        case ShortMessage.CONTROL_CHANGE:
                        case ShortMessage.PITCH_BEND:
                            status = possibleStatus;
                            shortMessage = new ShortMessage(status, midiInputStream.readByte() & 0xFF,
                                    midiInputStream.readByte() & 0xFF);
                            break;
                        case ShortMessage.PROGRAM_CHANGE:
                        case ShortMessage.CHANNEL_PRESSURE:
                            status = possibleStatus;
                            shortMessage = new ShortMessage(status, midiInputStream.readByte() & 0xFF, 0);
                            break;
                    }
                    result.add(new MidiTimestampPair(deltaTimeSum, shortMessage));
                }
            } catch (final InvalidMidiDataException e) {
                log.error("Invalid midi data", e);
            }
        }
        if (!result.isEmpty()) {
            result.forEach(midiPair -> listeners.forEach(listener -> listener
                    .onMidiMessage(midiCommandHeader, midiPair.getMidiMessage(), midiPair.getTimestamp())));
        }
    }

    /**
     * @return Is partial?
     */
    private boolean readSysexData(final DataInputStream midiInputStream, final ByteArrayOutputStream sysexData) throws
            IOException {
        byte tmpByte;
        boolean sysexEnd;
        boolean sysexStart;
        do {
            tmpByte = midiInputStream.readByte();
            sysexEnd = tmpByte == (byte) SysexMessage.SPECIAL_SYSTEM_EXCLUSIVE;
            sysexStart = tmpByte == (byte) SysexMessage.SYSTEM_EXCLUSIVE;
            if (!sysexStart) {
                sysexData.write(tmpByte);
            }
        } while (!(sysexEnd || sysexStart));
        return sysexStart;
    }

    private int readDeltaTime(final DataInputStream midiInputStream) throws IOException {
        byte deltaTimeOctet = midiInputStream.readByte();
        int deltaTime = deltaTimeOctet & 0x7F;
        int numberOfOctets = 1;
        while (((deltaTimeOctet >> 7) & 0x01) == 1 || numberOfOctets < 4) {
            numberOfOctets++;
            deltaTimeOctet = midiInputStream.readByte();
            deltaTime = ((deltaTimeOctet << 8) & 0x7F) | deltaTimeOctet;
        }
        return deltaTime;
    }

    public void registerListener(final AppleMidiMessageListener appleMidiMessageListener) {
        listeners.add(appleMidiMessageListener);
    }

    public void unregisterListener(final AppleMidiMessageListener appleMidiMessageListener) {
        listeners.remove(appleMidiMessageListener);
    }

}
