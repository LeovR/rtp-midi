package io.github.leovr.rtipmidi;


import javax.sound.midi.MidiDevice;
import javax.sound.midi.MidiSystem;

public class Application {

    public static void main(final String[] args) throws Exception {
        final AppleMidiServer server = new AppleMidiServer();

        final MidiDevice.Info[] devices = MidiSystem.getMidiDeviceInfo();

        final MidiDevice midiDevice = MidiSystem.getMidiDevice(devices[2]);
        server.addAppleMidiSession(
                new MidiDeviceAppleMidiSession(new MidiDeviceModePair(midiDevice, MidiDeviceMode.WRITE_ONLY)));

        server.start();

        System.in.read();
        server.stop();
    }

}
