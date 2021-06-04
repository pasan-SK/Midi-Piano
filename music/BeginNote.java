package music;

import midi.Instrument;

public class BeginNote extends NoteEvent {

    public BeginNote(Pitch pitch, int delay, Instrument instrument) {
        super(pitch, delay, instrument);
    }

    public void execute(MusicMachine m) {
        m.beginNote(this);
    }
}
