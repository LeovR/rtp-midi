package io.github.leovr.rtipmidi.messages;

import io.github.leovr.rtipmidi.model.MidiMessage;
import lombok.Value;

@Value
public class MidiTimestampPair {

    private int timestamp;
    private MidiMessage midiMessage;
}
