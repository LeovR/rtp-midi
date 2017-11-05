package io.github.leovr.rtipmidi;

import lombok.Value;

import javax.sound.midi.MidiDevice;

@Value
public class MidiDeviceModePair {

    private MidiDevice device;
    private MidiDeviceMode mode;

}
