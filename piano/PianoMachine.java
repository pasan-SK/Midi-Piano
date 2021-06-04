package piano;

import midi.Instrument;
import music.MusicMachine;
import music.NoteEvent;
import music.Pitch;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.sound.midi.MidiUnavailableException;

import midi.Midi;

public class PianoMachine implements MusicMachine {

    private boolean isRecording = false;
    private List<NoteEvent> recording, lastRecording;
    private Set<Pitch> pitchesPlaying;  //to save pitches generated after key press but not yet released
    private final PianoPlayer player;
    private Midi midi;
    private int time;

    public PianoMachine(PianoPlayer player) {
        time = (int) System.currentTimeMillis();	//Can be used to get time delays between notes
        this.player = player;
        try {
            this.midi = new Midi();
        } catch (MidiUnavailableException e) {
            // TODO Auto-generated catch block
            this.midi = null;
            e.printStackTrace();
        }
        lastRecording = new ArrayList<NoteEvent>();
        pitchesPlaying = new HashSet<Pitch>();
    }

    @Override
    public void beginNote(NoteEvent event) {
        Instrument prevInstrument = Midi.customInstrument;  //Instrument that was set before playing the note
        Pitch pitch = event.getPitch();
        Midi.customInstrument = event.getInstrument();

        if (pitchesPlaying.contains(pitch))
            return;     //This will make sure key sustains and does not generate repeated sounds

        pitchesPlaying.add(pitch);
        time = (int) System.currentTimeMillis();    //Time variable gets updated
        midi.beginNote(pitch.toMidiFrequency());    //generates sound

        if (isRecording)
            recording.add(event);

        Midi.customInstrument = prevInstrument; //Restore the previous instrument after playing the note
    }

    public void toggleRecording() {
        if (isRecording) {
            lastRecording = recording;
        } else {
            recording = new ArrayList<NoteEvent>();
        }
        isRecording = !isRecording;
    }

    public void requestPlayback() {
        player.playbackRecording(lastRecording);
    }

    @Override
    public void endNote(NoteEvent event) {
        Pitch pitch = event.getPitch();

        pitchesPlaying.remove(pitch);
        time = (int) System.currentTimeMillis();    //time variable gets updated
        midi.endNote(pitch.toMidiFrequency());
        if (isRecording)
            recording.add(event);
    }

    public int getTime() { return time;}

    public void setNextInstrument() {
        Midi.customInstrument = Midi.customInstrument.next();
    }

    public void setTime(int time) { this.time = time;}

    public Instrument getinstrument() {return Midi.customInstrument; }
}