# rtpmidi
A Java implementation of the RTP-MIDI protocol. 

## Usage

Currently this library can only be used as a RTP-MIDI session listener.

1. Create a `AppleMidiServer`
2. Add a `AppleMidiSession` to the server
  * Either a `MidiDeviceAppleMidiSession` if a `MidiDevice` should be used
  * Or a `MidiReceiverAppleMidiSession` if only a `Receiver` interface should be used. E.g. for direct processing of messages without a `MidiDevice`.
3. `start()` the server

To announce the server via Apple's bonjour the [jMDNS](https://github.com/jmdns/jmdns) library can be used.

###Demo

    public class Application {
    
        public static void main(final String[] args) throws InterruptedException {
            try {
                JmDNS jmdns = JmDNS.create(InetAddress.getLocalHost());
    
                ServiceInfo serviceInfo =
                        ServiceInfo.create("_apple-midi._udp.local.", "rtpMidiJava", 50004, "apple-midi");
                jmdns.registerService(serviceInfo);

                MidiDevice midiDevice = ;//get MIDI device somehow

                AppleMidiServer server = new AppleMidiServer();
                server.addAppleMidiSession(new MidiDeviceAppleMidiSession(midiDevice));

                server.start();

                System.in.read();

                server.stop();
            } catch (final IOException e) {
                e.printStackTrace();
            }
        }
    }


## ToDo
* Implement journaling
* Implement missing session protocol commands
* Implement session initiation
