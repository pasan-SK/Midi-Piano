/**
 * Author: dnj
 * Date: August 29, 2008
 * 6.005 Elements of Software Construction
 * (c) 2008, MIT and Daniel Jackson
 */
package piano;
import java.awt.*;
import java.awt.event.*;
import java.applet.*;
import java.util.HashMap;

import music.*;

/**
 * A skeletal applet that shows how to bind methods to key events
 */
public class PianoApplet extends Applet {

    public void init() {
        final PianoPlayer player = new PianoPlayer();
        setBackground(Color.green);

        HashMap<Character, Integer> mapping = new HashMap<>();
        mapping.put('1',0);
        mapping.put('2',1);
        mapping.put('3',2);
        mapping.put('4',3);
        mapping.put('5',4);
        mapping.put('6',5);
        mapping.put('7',6);
        mapping.put('8',7);
        mapping.put('9',8);
        mapping.put('0',9);
        mapping.put('-',10);
        mapping.put('=',11);

        // this is a standard pattern for associating method calls with GUI events
        // the call to the constructor of KeyAdapter creates an object of an
        // anonymous subclass of KeyAdapter, whose keyPressed method is called
        // when a key is pressed in the GUI
        addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent e) {
                char key = (char) e.getKeyCode();
                if (key == 'I'){ player.nextInstrument();}

                else if (key == 'P'){ player.requestPlayback();}

                else if (key == 'R')
                {
                    if (getBackground() == Color.green)
                        setBackground(Color.red);
                    else
                        setBackground(Color.green);

                    player.toggleRecording();
                }

                else if (mapping.containsKey(key))
                {
                    NoteEvent ne = new BeginNote(new Pitch(mapping.get(key)), (int) System.currentTimeMillis() - player.getMachine().getTime(), player.getMachine().getinstrument());
                    player.request(ne);
                }
                else
                {
                    System.out.println("INVALID KEY PRESS!!");
                }
            }

            public void keyReleased(KeyEvent e) {
                char key = (char) e.getKeyCode();

                if (key == 'I' || key == 'P' || key == 'R') {
                    player.getMachine().setTime((int) System.currentTimeMillis());  //time variable on machine gets updated
                    return;
                }

                if (mapping.containsKey(key)) {
                    NoteEvent ne = new EndNote(new Pitch(mapping.get(key)), (int) System.currentTimeMillis() - player.getMachine().getTime());
                    player.request(ne);
                    return;
                }
                return;
            }
        });
    }

    }

