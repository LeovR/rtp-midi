package io.github.leovr.rtipmidi.handler;

import io.github.leovr.rtipmidi.AppleMidiCommandListener;
import io.github.leovr.rtipmidi.messages.AppleMidiClockSynchronization;
import io.github.leovr.rtipmidi.messages.AppleMidiEndSession;
import io.github.leovr.rtipmidi.messages.AppleMidiInvitationRequest;
import io.github.leovr.rtipmidi.messages.CommandWord;
import io.github.leovr.rtipmidi.model.AppleMidiServer;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.Nonnull;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import static io.github.leovr.rtipmidi.messages.AppleMidiCommand.MIDI_COMMAND_HEADER1;
import static io.github.leovr.rtipmidi.messages.AppleMidiCommand.MIDI_COMMAND_HEADER2;
import static java.nio.charset.StandardCharsets.UTF_8;

@Slf4j
public class AppleMidiCommandHandler {

    private static final int NUMBER_OF_PADDING_BYTES = 3;
    private static final int PROTOCOL_VERSION = 2;
    private static final int COMMAND_BUFFER_LENGTH = 2;
    private static final String NUL_TERMINATOR = "\u0000";
    private final List<AppleMidiCommandListener> listeners = new ArrayList<>();

    public AppleMidiCommandHandler() {
        listeners.add(new AppleMidiCommandLogListener());
    }

    public void handle(@Nonnull final byte[] data, @Nonnull final AppleMidiServer appleMidiServer) {
        final DataInputStream dataInputStream = new DataInputStream(new ByteArrayInputStream(data));
        try {
            final byte header1 = dataInputStream.readByte();
            if (header1 != MIDI_COMMAND_HEADER1) {
                log.info("Header did not contain first MIDI command header");
                return;
            }
            final byte header2 = dataInputStream.readByte();
            if (header2 != MIDI_COMMAND_HEADER2) {
                log.info("Header did not contain second MIDI command header");
                return;
            }
            final byte[] commandBuffer = new byte[COMMAND_BUFFER_LENGTH];
            final int commandBytesRead = dataInputStream.read(commandBuffer);
            if (commandBytesRead != COMMAND_BUFFER_LENGTH) {
                log.info("The number of command bytes: {} did not match: {}", commandBytesRead, COMMAND_BUFFER_LENGTH);
                return;
            }
            final String command = new String(commandBuffer, UTF_8);
            final CommandWord commandWord;
            try {
                commandWord = CommandWord.valueOf(command);
            } catch (final IllegalArgumentException e) {
                log.info("Could not parse command word from: {}", command);
                return;
            }
            switch (commandWord) {
                case IN:
                    handleInvitation(dataInputStream, appleMidiServer);
                    break;
                case CK:
                    handleSynchronization(dataInputStream, appleMidiServer);
                    break;
                case BY:
                    handleEndSession(dataInputStream, appleMidiServer);
                    break;
            }
        } catch (final IOException e) {
            log.error("IOException while parsing message", e);
        }
    }

    private void handleEndSession(final DataInputStream dataInputStream, final AppleMidiServer appleMidiServer) throws
            IOException {
        final int protocolVersion = dataInputStream.readInt();
        if (protocolVersion != PROTOCOL_VERSION) {
            log.info("Protocol version: {} did not match version {}", protocolVersion, PROTOCOL_VERSION);
            return;
        }
        final int initiatorToken = dataInputStream.readInt();
        final int ssrc = dataInputStream.readInt();
        for (final AppleMidiCommandListener listener : listeners) {
            listener.onEndSession(new AppleMidiEndSession(protocolVersion, initiatorToken, ssrc), appleMidiServer);
        }
    }

    private void handleSynchronization(final DataInputStream dataInputStream,
                                       final AppleMidiServer appleMidiServer) throws IOException {
        final int ssrc = dataInputStream.readInt();
        final byte count = dataInputStream.readByte();
        final int paddingBytes = dataInputStream.read(new byte[NUMBER_OF_PADDING_BYTES]);
        if (paddingBytes != NUMBER_OF_PADDING_BYTES) {
            log.info("The number of padding bytes: {} did not match: {}", paddingBytes, NUMBER_OF_PADDING_BYTES);
            return;
        }
        final long timestamp1 = dataInputStream.readLong();
        final long timestamp2 = dataInputStream.readLong();
        final long timestamp3 = dataInputStream.readLong();
        for (final AppleMidiCommandListener listener : listeners) {
            listener.onClockSynchronization(
                    new AppleMidiClockSynchronization(ssrc, count, timestamp1, timestamp2, timestamp3),
                    appleMidiServer);
        }
    }

    private void handleInvitation(final DataInputStream dataInputStream, final AppleMidiServer appleMidiServer) throws
            IOException {
        final int protocolVersion = dataInputStream.readInt();
        if (protocolVersion != PROTOCOL_VERSION) {
            log.info("Protocol version: {} did not match version {}", protocolVersion, PROTOCOL_VERSION);
            return;
        }
        final int initiatorToken = dataInputStream.readInt();
        final int ssrc = dataInputStream.readInt();
        final Scanner scanner = new Scanner(dataInputStream, UTF_8.name()).useDelimiter(NUL_TERMINATOR);
        if (!scanner.hasNext()) {
            log.info("Could not find \\0 terminating string");
            return;
        }
        final String name = scanner.next();
        for (final AppleMidiCommandListener listener : listeners) {
            listener.onMidiInvitation(new AppleMidiInvitationRequest(protocolVersion, initiatorToken, ssrc, name),
                    appleMidiServer);
        }
    }

    public void registerListener(@Nonnull final AppleMidiCommandListener appleMidiCommandListener) {
        listeners.add(appleMidiCommandListener);
    }

    public void unregisterListener(@Nonnull final AppleMidiCommandListener appleMidiCommandListener) {
        listeners.remove(appleMidiCommandListener);
    }

}
