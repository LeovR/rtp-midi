package io.github.leovr.rtipmidi.handler;

import io.github.leovr.rtipmidi.AppleMidiMessageListener;
import io.github.leovr.rtipmidi.messages.MidiCommandHeader;
import io.github.leovr.rtipmidi.model.MidiMessage;
import lombok.extern.slf4j.Slf4j;

@Slf4j
class AppleMidiMessageLogListener implements AppleMidiMessageListener {

    @Override
    public void onMidiMessage(final MidiCommandHeader midiCommandHeader, final MidiMessage message,
                              final int timestamp) {
        log.trace("MIDI message: midiCommandHeader: {}, message: {}, timestamp: {}", midiCommandHeader, message,
                timestamp);
    }
}
