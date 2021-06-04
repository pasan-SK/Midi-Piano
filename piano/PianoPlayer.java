package piano;

import music.NoteEvent;

import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import midi.Midi;

public class PianoPlayer {
    private final BlockingQueue<NoteEvent> queue, delayQueue;
    private final PianoMachine machine;
    private Thread processQueueThread, processDelayQueueThread;

    public PianoPlayer()
    {
        queue = new LinkedBlockingQueue<NoteEvent>();
        delayQueue = new LinkedBlockingQueue<NoteEvent>();
        machine = new PianoMachine(this);

        //spawning process queue thread
        processQueueThread = new Thread(new Runnable() {
            public void run() {
                System.out.println("ProcessQueueThread started!");
                processQueue();
            }
        });

        //spawning process delay queue thread
        processDelayQueueThread = new Thread(new Runnable() {
            public void run() {
                System.out.println("ProcessDelayQueueThread started!");
                processDelayQueue();
            }
        });

        //starting threads
        processQueueThread.start();
        processDelayQueueThread.start();
    }

    public void request(NoteEvent e)
    {
        try {
            queue.put(e);
        } catch (InterruptedException e1) {
            e1.printStackTrace();
        }
    }

    public void requestPlayback()
    {
        machine.requestPlayback();
    }

    public void toggleRecording()
    {
        machine.toggleRecording();
    }

    public void playbackRecording(List<NoteEvent> recording)
    {
        //This condition will ensure that pressing 'P' twice does not repeat the playback twice
        if (this.delayQueue.isEmpty()) {
            for (NoteEvent e : recording) {
                try {
                    delayQueue.put(e);
                } catch (InterruptedException e1) {
                    e1.printStackTrace();
                }
            }
        }
        return;
    }

    public void nextInstrument() {
        this.machine.setNextInstrument();

    }

    //method that runs through processQueueThread
    public void processQueue()
    {
        while (true) {
            NoteEvent e;
            try {
                e = queue.take();
                e.execute(machine);
            } catch (InterruptedException e1) {
                e1.printStackTrace();
            }

        }
    }

    //method that runs through processDelayQueueThread
    public void processDelayQueue()
    {
        while (true) {
            try {
                NoteEvent e = delayQueue.take();
                Midi.wait(e.getDelay());
                queue.put(e);
            } catch (InterruptedException e1) {
                e1.printStackTrace();
            }
        }
    }

    public PianoMachine getMachine() {
        return this.machine;
    }
}
