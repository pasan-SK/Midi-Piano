package music;

import midi.Instrument;

public abstract class NoteEvent {

    protected final Pitch pitch;
    protected final int delay;
    protected Instrument instrument;

    public NoteEvent(Pitch pitch, int delay, Instrument instrument) {
        this.pitch = pitch;
        this.delay = delay;
        this.instrument = instrument;
    }

    public NoteEvent(Pitch pitch, int delay) {
        this.pitch = pitch;
        this.delay = delay;
    }

    public Pitch getPitch() {
        return this.pitch;
    }

    public int getDelay() {
        return this.delay;
    }

    public Instrument getInstrument() {
        return this.instrument;
    }
    abstract public void execute(MusicMachine m);
}

