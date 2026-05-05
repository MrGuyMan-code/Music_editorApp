/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package music.editor;




/**
 *
 * @author desktop
 */

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Label;
import java.util.ArrayList;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.Sequence;
import javax.sound.midi.Sequencer;
import javax.sound.midi.Track;
import javax.sound.midi.ShortMessage;
import javax.sound.midi.MidiEvent;
import javax.swing.*;

public class MusicEditor {
    
    JPanel mainPanel;
    //Stocam casetele de validare in array list
    ArrayList<JCheckBox> checkboxList;
    Sequencer sequencer;
    Sequence sequence;
    Track track;
    JFrame theFrame;
    
    String[] soundsNames = {"SI", "LA", "SOL", "FA", "MI", "RE", "DO"};
    
    int[] instruments = {35, 42, 46, 38, 49, 39, 50, 60, 70, 72, 64, 56, 58, 47, 67, 63};
    
    // Currently selected instrument (default: Acoustic Snare - 38)
    int currentInstrument = 38;
    
    // Notes for each row (MIDI note numbers)
    int[] notes = {71, 69, 67, 65, 64, 62, 60}; // SI, LA, SOL, FA, MI, RE, DO
    
    // Current number of beats
    int currentBeats = 16;
    
    JTextField beatsTextField;
    JButton generateButton;
    JPanel background;
    
    // Instrument names for display
    String[] instrumentNames = {
        "Bass Drum (35)", "Closed Hi-Hat (42)", "Open Hi-Hat (46)", "Acoustic Snare (38)",
        "Crash Cymbal (49)", "Hand Clap (39)", "Hi Tom (50)", "Low Bongo (60)",
        "Mute Cuica (70)", "Open Cuica (72)", "Low Timbal (64)", "High Agogo (56)",
        "Cabasa (58)", "Maracas (47)", "High Wood Block (67)", "Low Wood Block (63)"
    };

    public static void main(String[] args) {
        new MusicEditor().buildGUI();
    }
    
    public void buildGUI(){
        theFrame = new JFrame("Cyber BeatBox");
        theFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        BorderLayout layout = new BorderLayout();
        background = new JPanel(layout);
        background.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Create top panel for beat control and tempo
        JPanel topPanel = new JPanel();
        topPanel.add(new JLabel("Number of Beats:"));
        beatsTextField = new JTextField("16", 5);
        topPanel.add(beatsTextField);
        generateButton = new JButton("Generate Grid");
        generateButton.addActionListener(e -> generateNewGrid());
        topPanel.add(generateButton);
        
        // Add Clear All button
        JButton clearAllButton = new JButton("Clear All");
        clearAllButton.addActionListener(e -> clearAllCheckboxes());
        topPanel.add(clearAllButton);
        
        // Add tempo control to top panel
        topPanel.add(new JLabel("  Tempo:"));
        JComboBox<String> tempoCombo = new JComboBox<>(new String[]{"100 BPM", "120 BPM", "140 BPM", "Custom..."});
        tempoCombo.addActionListener(e -> {
            String selected = (String) tempoCombo.getSelectedItem();
            if (selected.equals("Custom...")) {
                String input = JOptionPane.showInputDialog(theFrame, "Enter tempo (BPM):", "120");
                if (input != null) {
                    try {
                        int tempo = Integer.parseInt(input);
                        if (tempo >= 40 && tempo <= 240) {
                            setTempo(tempo);
                        } else {
                            JOptionPane.showMessageDialog(theFrame, "Tempo must be between 40 and 240 BPM");
                        }
                    } catch (NumberFormatException ex) {
                        JOptionPane.showMessageDialog(theFrame, "Please enter a valid number");
                    }
                }
                tempoCombo.setSelectedItem("120 BPM");
            } else {
                int tempo = Integer.parseInt(selected.split(" ")[0]);
                setTempo(tempo);
            }
        });
        topPanel.add(tempoCombo);
        
        background.add(BorderLayout.NORTH, topPanel);
        
        // Create WEST panel using GridLayout to put components side by side (left and right)
        JPanel westPanel = new JPanel(new BorderLayout());
        
        // LEFT side of west panel - Instrument selector
        JPanel instrumentPanel = new JPanel(new BorderLayout());
        instrumentPanel.setBorder(BorderFactory.createTitledBorder("Select Instrument"));
        JComboBox<String> instrumentCombo = new JComboBox<>(instrumentNames);
        instrumentCombo.addActionListener(e -> {
            int index = instrumentCombo.getSelectedIndex();
            currentInstrument = instruments[index];
            System.out.println("Instrument changed to: " + instrumentNames[index] + " (ID: " + currentInstrument + ")");
        });
        instrumentPanel.add(instrumentCombo, BorderLayout.CENTER);
        westPanel.add(BorderLayout.WEST, instrumentPanel);
        
        // RIGHT side of west panel - Note names (with increased width)
        JPanel notesPanel = new JPanel(new GridLayout(7, 1, 5, 5));
        notesPanel.setBorder(BorderFactory.createTitledBorder("Notes"));
        
        // Set preferred width for notes panel
        notesPanel.setPreferredSize(new Dimension(67, 0));
        
        for (int i = 0; i < 7; i++) {
            JLabel noteLabel = new JLabel(soundsNames[i], SwingConstants.CENTER);
            noteLabel.setFont(noteLabel.getFont().deriveFont(16f));
            // Add some padding around the text
            noteLabel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
            notesPanel.add(noteLabel);
        }
        
        westPanel.add(notesPanel, BorderLayout.CENTER);
        
        background.add(BorderLayout.WEST, westPanel);
        
        // Create right panel for buttons
        Box buttonBox = new Box(BoxLayout.Y_AXIS);
        buttonBox.setBorder(BorderFactory.createTitledBorder("Controls"));
        
        JButton start = new JButton("Start");
        start.addActionListener(e -> startMusic());
        buttonBox.add(start);
        buttonBox.add(Box.createVerticalStrut(5));
        
        JButton stop = new JButton("Stop");
        stop.addActionListener(e -> stopMusic());
        buttonBox.add(stop);
        buttonBox.add(Box.createVerticalStrut(5));
        
        JButton serialisation = new JButton("Serialisation");
        buttonBox.add(serialisation);
        buttonBox.add(Box.createVerticalStrut(5));
        
        JButton restore = new JButton("Restore");
        buttonBox.add(restore);
        
        background.add(BorderLayout.EAST, buttonBox);
        
        theFrame.getContentPane().add(background);
        
        // Initialize checkboxList BEFORE creating the grid
        checkboxList = new ArrayList<JCheckBox>();
        
        // Create the grid panel in the center
        createGrid(16);
        
        setUpMidi();
        
        theFrame.setBounds(50, 50, 800, 500);
        theFrame.pack();
        theFrame.setVisible(true);
    }
    
    private void clearAllCheckboxes() {
        if (checkboxList != null) {
            for (JCheckBox checkBox : checkboxList) {
                checkBox.setSelected(false);
            }
            System.out.println("Cleared all checkboxes");
        }
    }
    
    private void createGrid(int beats) {
        // Remove old grid if it exists
        if (mainPanel != null) {
            background.remove(mainPanel);
        }
        
        // Create new grid layout
        GridLayout grid = new GridLayout(7, beats);
        grid.setVgap(2);
        grid.setHgap(2);
        mainPanel = new JPanel(grid);
        mainPanel.setBorder(BorderFactory.createTitledBorder("Beat Matrix (" + beats + " beats)"));
        
        // Clear and recreate checkbox list
        if (checkboxList == null) {
            checkboxList = new ArrayList<JCheckBox>();
        } else {
            checkboxList.clear();
        }
        
        // Create new checkboxes
        for (int i = 0; i < beats * 7; i++) {
            JCheckBox c = new JCheckBox();
            c.setSelected(false);
            checkboxList.add(c);
            mainPanel.add(c);
        }
        
        // Add the grid to the center of background
        background.add(BorderLayout.CENTER, mainPanel);
        
        // Force repaint and revalidate
        background.revalidate();
        background.repaint();
        theFrame.revalidate();
        theFrame.repaint();
        
        currentBeats = beats;
        System.out.println("Created new grid with " + beats + " beats");
        
        // Pack the frame to adjust size
        theFrame.pack();
    }
    
    private void generateNewGrid() {
        try {
            int beats = Integer.parseInt(beatsTextField.getText().trim());
            if (beats < 1) {
                JOptionPane.showMessageDialog(theFrame, "Number of beats must be at least 1");
                return;
            }
            if (beats > 64) {
                JOptionPane.showMessageDialog(theFrame, "Number of beats cannot exceed 64 (performance reasons)");
                return;
            }
            createGrid(beats);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(theFrame, "Please enter a valid number");
        }
    }
    
    private String getInstrumentName(int instrument) {
        for (int i = 0; i < instruments.length; i++) {
            if (instruments[i] == instrument) {
                return instrumentNames[i];
            }
        }
        return "Unknown Instrument";
    }
    
    private void setTempo(int bpm) {
        if (sequencer != null && sequencer.isOpen()) {
            sequencer.setTempoInBPM(bpm);
            JOptionPane.showMessageDialog(theFrame, "Tempo changed to " + bpm + " BPM");
        }
    }
    
    public void setUpMidi(){
        try{
            sequencer = MidiSystem.getSequencer();
            sequencer.open();
            sequencer.setTempoInBPM(120);
        } catch(Exception e) {e.printStackTrace();}
    }
    
    public void buildTrackAndStart() {
        try {
            // Stop any current playback
            if (sequencer.isRunning()) {
                sequencer.stop();
            }
            
            // Create new sequence and track - using 4 ticks per quarter note
            sequence = new Sequence(Sequence.PPQ, 4);
            track = sequence.createTrack();
            
            System.out.println("Building track with " + currentBeats + " beats and instrument ID: " + currentInstrument);
            
            // Set the instrument (program change)
            ShortMessage instrumentMsg = new ShortMessage();
            instrumentMsg.setMessage(ShortMessage.PROGRAM_CHANGE, 0, currentInstrument, 0);
            track.add(new MidiEvent(instrumentMsg, 0));
            
            // Add silent notes at the end of each row to ensure full duration
            for (int row = 0; row < 7; row++) {
                // Add a silent note (volume 0) at the last beat to mark the end
                ShortMessage silentNote = new ShortMessage();
                silentNote.setMessage(ShortMessage.NOTE_ON, 0, notes[row], 0);
                track.add(new MidiEvent(silentNote, currentBeats));
            }
            
            // Loop through all beats (0 to currentBeats-1)
            for (int beat = 0; beat < currentBeats; beat++) {
                // Loop through all 7 rows
                for (int row = 0; row < 7; row++) {
                    // Get the checkbox for this beat and row
                    int checkboxIndex = (row * currentBeats) + beat;
                    if (checkboxIndex < checkboxList.size()) {
                        JCheckBox check = checkboxList.get(checkboxIndex);
                        
                        if (check.isSelected()) {
                            // Add note on at this beat
                            ShortMessage noteOn = new ShortMessage();
                            noteOn.setMessage(ShortMessage.NOTE_ON, 0, notes[row], 100);
                            track.add(new MidiEvent(noteOn, beat));
                            
                            // Add note off at beat + 1 (lasts for 1 beat)
                            ShortMessage noteOff = new ShortMessage();
                            noteOff.setMessage(ShortMessage.NOTE_OFF, 0, notes[row], 100);
                            track.add(new MidiEvent(noteOff, beat + 1));
                        }
                    }
                }
            }
            
            // Load sequence into sequencer
            sequencer.setSequence(sequence);
            
            // Set loop from beginning to the end of the sequence (currentBeats)
            sequencer.setLoopStartPoint(0);
            sequencer.setLoopEndPoint(currentBeats);
            sequencer.setLoopCount(Sequencer.LOOP_CONTINUOUSLY);
            
            // Start playing
            sequencer.start();
            
            System.out.println("Now playing " + currentBeats + " beats with instrument: " + getInstrumentName(currentInstrument));
            System.out.println("Loop from 0 to " + currentBeats);
            
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(theFrame, "Error playing music: " + e.getMessage());
        }
    }
    
    public void startMusic() {
        buildTrackAndStart();
    }
    
    public void stopMusic() {
        if (sequencer != null && sequencer.isRunning()) {
            sequencer.stop();
            System.out.println("Music stopped");
        }
    }
}