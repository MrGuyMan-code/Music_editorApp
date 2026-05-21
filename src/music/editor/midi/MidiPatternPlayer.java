/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package music.editor.midi;

import music.editor.grid.NoteGrid;
import music.editor.grid.NoteInfo;

import javax.sound.midi.*;

public class MidiPatternPlayer {

    private Sequencer sequencer;
    
    private int tempo = 120;

    public MidiPatternPlayer() {

        try {

            sequencer = MidiSystem.getSequencer();

            sequencer.open();

            sequencer.setTempoInBPM(120);
        }

        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void play(NoteGrid noteGrid, int[] notes, int currentInstrument, int currentBeats) {

        try {

            if (sequencer.isRunning()) {
                sequencer.stop();
            }

            Sequence sequence = new Sequence(Sequence.PPQ, 4);

            Track track = sequence.createTrack();

            // INSTRUMENT
            ShortMessage instrumentMsg = new ShortMessage();

            instrumentMsg.setMessage(
                ShortMessage.PROGRAM_CHANGE,
                0,
                currentInstrument,
                0
            );

            track.add(new MidiEvent(instrumentMsg, 0));

            // BUILD NOTES
            for (int row = 0; row < 7; row++) {

                for (
                    int beat = 0;
                    beat < currentBeats;
                    beat++
                ) {

                    // SKIP EMPTY
                    if (!noteGrid.isActive(row, beat)) {
                        continue;
                    }

                    // SKIP CONTINUATIONS
                    if (noteGrid.isContinuation(row, beat)) {
                        continue;
                    }

                    NoteInfo info = noteGrid.getNoteInfo(row,beat);

                    int duration = info.getDuration();

                    // NOTE ON
                    ShortMessage noteOn = new ShortMessage();

                    noteOn.setMessage(
                        ShortMessage.NOTE_ON,
                        0,
                        notes[row],
                        100
                    );

                    track.add(new MidiEvent(noteOn, beat));

                    // NOTE OFF
                    ShortMessage noteOff = new ShortMessage();

                    noteOff.setMessage(
                        ShortMessage.NOTE_OFF,
                        0,
                        notes[row],
                        100
                    );

                    track.add(
                        new MidiEvent(
                            noteOff,
                            beat + duration
                        )
                    );
                }
            }

            sequencer.setSequence(sequence);
            sequencer.setTempoInBPM(tempo);

            sequencer.start();
        }

        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void stop() {

        if (
            sequencer != null &&
            sequencer.isRunning()
        ) {

            sequencer.stop();
        }
    }

    public void setTempo(int bpm) {
        tempo = bpm;

        if (sequencer != null && sequencer.isOpen()) {

            sequencer.setTempoInBPM(bpm);
        }
    }

    public float getTempo() {

        return sequencer.getTempoInBPM();
    }
}
