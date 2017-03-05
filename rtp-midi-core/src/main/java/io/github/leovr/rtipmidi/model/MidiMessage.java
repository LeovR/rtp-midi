package io.github.leovr.rtipmidi.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public abstract class MidiMessage {

    private final byte[] data;
    private final int length;

}
