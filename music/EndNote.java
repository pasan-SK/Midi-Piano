package music;

public class EndNote extends NoteEvent {

    public EndNote(Pitch pitch, int delay) {
        super(pitch, delay);
    }

    public void execute(MusicMachine m) {
        m.endNote(this);
    }
}
