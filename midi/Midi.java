package midi;
/**
 * 6.005 Elements of Software Construction
 * Fall 2007
 * (c) 2007-8, MIT and Rob Miller
 */

import java.util.HashMap;
import java.util.Map;

import javax.sound.midi.*;

/**
 * Midi represents a MIDI synthesis device.
 */
public class Midi {
    private Synthesizer synthesizer;

    public final static Instrument DEFAULT_INSTRUMENT = Instrument.PIANO;
    public static Instrument customInstrument = Instrument.PIANO;

    // active MIDI channels, assigned to instruments
    private final Map<Instrument, MidiChannel> channels = new HashMap<Instrument, MidiChannel>();

    // next available channel number (unassigned to an instrument yet)
    private int nextChannel = 0;

    // volume -- a percentage?
    private static final int VELOCITY = 100;

    private void checkRep() {
        assert synthesizer != null;
        assert channels != null;
        assert nextChannel >= 0;
    }

    /**
     * Make a Midi.
     *
     * @throws MidiUnavailableException if MIDI is not available
     */
    public Midi() throws MidiUnavailableException {
        synthesizer = MidiSystem.getSynthesizer();
        synthesizer.open();
        synthesizer.loadAllInstruments(synthesizer.getDefaultSoundbank());
        checkRep();
    }

    /**
     * Play a note on the Midi scale for a duration in milliseconds using a
     * specified instrument.
     *
     * @requires 0 <= note < 256, duration >= 0, instr != null
     */
    public void play(int note, int duration, Instrument instr) {
        MidiChannel channel = getChannel(instr);
        synchronized (channel) {
            channel.noteOn(note, VELOCITY);
        }
        wait(duration);
        synchronized (channel) {
            channel.noteOff(note);
        }
    }

    /**
     * Start playing a note on the Midi scale using a specified instrument.
     *
     * @requires 0 <= note < 256, instr != null
     */
    public void beginNote(int note, Instrument instr) {
        MidiChannel channel = getChannel(instr);
        synchronized (channel) {
            channel.noteOn(note, VELOCITY);
        }
    }

    public void beginNote(int note) {
        beginNote(note, customInstrument);
    }

    /**
     * Stop playing a note on the Midi scale using a specified instrument.
     *
     * @requires 0 <= note < 256, instr != null
     */
    public void endNote(int note, Instrument instr) {
        MidiChannel channel = getChannel(instr);
        synchronized (channel) {
            channel.noteOff(note, VELOCITY);
        }
    }

    public void endNote(int note) {
        endNote(note, customInstrument);
    }

    /**
     * Wait for a duration in milliseconds.
     *
     * @requires duration >= 0
     */
    public static void wait(int duration) {
        long now = System.currentTimeMillis();
        long end = now + duration;
        while (now < end) {
            try {
                Thread.sleep((int) (end - now));
            } catch (InterruptedException e) {
            }
            now = System.currentTimeMillis();
        }
    }

    private MidiChannel getChannel(Instrument instr) {
        synchronized (channels) {
            // check whether this instrument already has a channel
            MidiChannel channel = channels.get(instr);
            if (channel != null)
                return channel;

            channel = allocateChannel();
            patchInstrumentIntoChannel(channel, instr);
            channels.put(instr, channel);
            checkRep();
            return channel;
        }
    }

    private MidiChannel allocateChannel() {
        MidiChannel[] channels = synthesizer.getChannels();
        if (nextChannel >= channels.length)
            throw new RuntimeException("tried to use too many instruments: limited to " + channels.length);
        MidiChannel channel = channels[nextChannel];
        // quick hack by DNJ to allow more instruments to be used
        nextChannel = (nextChannel + 1) % channels.length;
        return channel;
    }

    private void patchInstrumentIntoChannel(MidiChannel channel, Instrument instr) {
        channel.programChange(0, instr.ordinal());
    }

    /**
     * Discover all the instruments in your MIDI synthesizer and print them to
     * standard output, along with their bank and program codes.
     */
    public static void main(String[] args) throws MidiUnavailableException {
        Midi m = new Midi();
        for (javax.sound.midi.Instrument i : m.synthesizer.getLoadedInstruments()) {
            System.out.println(i.getName() + " " + i.getPatch().getBank() + " " + i.getPatch().getProgram());
        }
    }
}