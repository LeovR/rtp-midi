package io.github.leovr.rtipmidi;

import io.github.leovr.rtipmidi.messages.MidiCommandHeader;

import javax.sound.midi.MidiMessage;

public interface AppleMidiMessageListener {

    void onMidiMessage(MidiCommandHeader midiCommandHeader, MidiMessage message, final int timestamp);
}
