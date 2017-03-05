package io.github.leovr.rtipmidi;

import io.github.leovr.rtipmidi.messages.MidiCommandHeader;
import io.github.leovr.rtipmidi.model.MidiMessage;

public interface AppleMidiMessageListener {

    /**
     * This method is called when a new MIDI message from the origin server is received
     *
     * @param midiCommandHeader The MIDI command meta information
     * @param message           The MIDI message
     * @param timestamp         Timestamp of this MIDI message
     */
    void onMidiMessage(MidiCommandHeader midiCommandHeader, MidiMessage message, final int timestamp);
}
