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
    
    // UI components for the main editor window
    JPanel mainContainer; // Holds all instrument panels in a scrollable area
    ArrayList<InstrumentPanel> instrumentPanels; // Collection of all musical instrument panels
    JFrame theFrame;
    
    // Note names for the 7 rows (descending pitch order)
    String[] soundsNames = {"SI", "LA", "SOL", "FA", "MI", "RE", "DO"};
    
    // MIDI instrument numbers (mostly percussion sounds)
    int[] instruments = {35, 42, 46, 38, 49, 39, 50, 60, 70, 72, 64, 56, 58, 47, 67, 63};
    
    // Display names for the instrument selector dropdown
    String[] instrumentNames = {
        "Bass Drum (35)", "Closed Hi-Hat (42)", "Open Hi-Hat (46)", "Acoustic Snare (38)",
        "Crash Cymbal (49)", "Hand Clap (39)", "Hi Tom (50)", "Low Bongo (60)",
        "Mute Cuica (70)", "Open Cuica (72)", "Low Timbal (64)", "High Agogo (56)",
        "Cabasa (58)", "Maracas (47)", "High Wood Block (67)", "Low Wood Block (63)"
    };

    public static void main(String[] args) {
        new MusicEditor().buildGUI();
    }
    
    // Creates the main application window and all UI components
    public void buildGUI(){
        theFrame = new JFrame("Cyber BeatBox");
        theFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        BorderLayout mainLayout = new BorderLayout();
        mainContainer = new JPanel(mainLayout);
        mainContainer.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // ===== TOP PANEL: Global controls that affect all instruments =====
        JPanel globalTopPanel = new JPanel();
        globalTopPanel.add(new JLabel("Global Controls"));
        
        // Tempo control dropdown
        globalTopPanel.add(new JLabel("  Tempo:"));
        JComboBox<String> tempoCombo = new JComboBox<>(new String[]{"100 BPM", "120 BPM", "140 BPM", "Custom..."});
        tempoCombo.addActionListener(e -> {
            String selected = (String) tempoCombo.getSelectedItem();
            if (selected.equals("Custom...")) {
                String input = JOptionPane.showInputDialog(theFrame, "Enter tempo (BPM):", "120");
                if (input != null) {
                    try {
                        int tempo = Integer.parseInt(input);
                        if (tempo >= 40 && tempo <= 240) {
                            setTempoForAll(tempo);
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
                setTempoForAll(tempo);
            }
        });
        globalTopPanel.add(tempoCombo);
        
        // Global play/stop buttons
        JButton playAllButton = new JButton("Play All");
        playAllButton.addActionListener(e -> playAllInstruments());
        globalTopPanel.add(playAllButton);
        
        JButton stopAllButton = new JButton("Stop All");
        stopAllButton.addActionListener(e -> stopAllInstruments());
        globalTopPanel.add(stopAllButton);
        
        mainContainer.add(BorderLayout.NORTH, globalTopPanel);
        
        // ===== SCROLLABLE AREA: Holds all individual instrument panels =====
        JPanel scrollContent = new JPanel();
        scrollContent.setLayout(new BoxLayout(scrollContent, BoxLayout.Y_AXIS));
        JScrollPane scrollPane = new JScrollPane(scrollContent);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        
        mainContainer.add(BorderLayout.CENTER, scrollPane);
        
        // Initialize collection and add the first instrument
        instrumentPanels = new ArrayList<>();
        addNewInstrument(scrollContent);
        
        theFrame.getContentPane().add(mainContainer);
        theFrame.setBounds(50, 50, 900, 700);
        theFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        theFrame.setVisible(true);
    }
    
    // Creates and adds a new instrument panel to the scrollable area
    private void addNewInstrument(JPanel scrollContent) {
        InstrumentPanel newInstrument = new InstrumentPanel(instrumentPanels.size() + 1);
        instrumentPanels.add(newInstrument);
        scrollContent.add(newInstrument);
        scrollContent.add(Box.createVerticalStrut(10)); // Spacing between panels
        scrollContent.revalidate();
        scrollContent.repaint();
    }
    
    // Applies the same tempo to every instrument
    private void setTempoForAll(int bpm) {
        for (InstrumentPanel ip : instrumentPanels) {
            ip.setTempo(bpm);
        }
        JOptionPane.showMessageDialog(theFrame, "Tempo changed to " + bpm + " BPM for all instruments");
    }
    
    // Starts playback for all instruments simultaneously
    private void playAllInstruments() {
        for (InstrumentPanel ip : instrumentPanels) {
            ip.startMusic();
        }
    }
    
    // Stops playback for all instruments
    private void stopAllInstruments() {
        for (InstrumentPanel ip : instrumentPanels) {
            ip.stopMusic();
        }
    }
    
    // Inner class representing a single instrument panel (one instrument's pattern)
    class InstrumentPanel extends JPanel {
        // Instrument identification and UI components
        private int instrumentId;
        private JPanel background;
        private JPanel mainPanel;
        private ArrayList<JCheckBox> checkboxList; // Grid of checkboxes (rows x beats)
        
        // MIDI components for playback
        private Sequencer sequencer;
        private Sequence sequence;
        private Track track;
        
        // Musical parameters
        private int currentInstrument = 38; // Default instrument (Acoustic Snare)
        private int[] baseNotes = {71, 69, 67, 65, 64, 62, 60}; // MIDI notes for 7 rows
        private int[] notes = {71, 69, 67, 65, 64, 62, 60}; // Notes with octave shift applied
        private int currentOctaveShift = 0;
        private int currentBeats = 16; // Number of beats in the pattern
        
        // UI controls
        private JTextField beatsTextField;
        private JComboBox<String> instrumentCombo;
        private JComboBox<String> octaveCombo;
        
        public InstrumentPanel(int id) {
            this.instrumentId = id;
            setupInstrument();
        }
        
        // Builds the complete UI for a single instrument
        private void setupInstrument() {
            setLayout(new BorderLayout());
            setBorder(BorderFactory.createTitledBorder("Instrument #" + instrumentId));
            setPreferredSize(new Dimension(850, 400));
            setMaximumSize(new Dimension(850, 400));
            
            background = new JPanel(new BorderLayout());
            background.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
            
            // ===== TOP PANEL: Instrument-specific controls =====
            JPanel topPanel = new JPanel();
            topPanel.add(new JLabel("Beats:"));
            beatsTextField = new JTextField("16", 4);
            topPanel.add(beatsTextField);
            JButton generateButton = new JButton("Generate Grid");
            generateButton.addActionListener(e -> generateNewGrid());
            topPanel.add(generateButton);
            
            JButton clearAllButton = new JButton("Clear All");
            clearAllButton.addActionListener(e -> clearAllCheckboxes());
            topPanel.add(clearAllButton);
            
            JButton removeButton = new JButton("Remove Instrument");
            removeButton.addActionListener(e -> removeThisInstrument());
            topPanel.add(removeButton);
            
            background.add(BorderLayout.NORTH, topPanel);
            
            // ===== WEST PANEL: Instrument selection and note labels =====
            JPanel westPanel = new JPanel(new BorderLayout());
            JPanel instrumentsAndShiftPanel = new JPanel(new GridLayout(2,1, 0, 10));
            
            // Instrument selector
            JPanel instrumentPanel = new JPanel(new BorderLayout());
            instrumentPanel.setBorder(BorderFactory.createTitledBorder("Select Instrument"));
            instrumentCombo = new JComboBox<>(instrumentNames);
            instrumentCombo.addActionListener(e -> {
                int index = instrumentCombo.getSelectedIndex();
                currentInstrument = instruments[index];
            });
            instrumentPanel.add(instrumentCombo, BorderLayout.CENTER);
            instrumentsAndShiftPanel.add(instrumentPanel);
            
            // Octave shift control
            JPanel octavePanel = new JPanel(new BorderLayout());
            octavePanel.setBorder(BorderFactory.createTitledBorder("Octave Shift"));
            String[] octaveOptions = {
                "-5 Octaves", "-4 Octaves", "-3 Octaves", "-2 Octaves", "-1 Octave", 
                "0 (Default)", "+1 Octave", "+2 Octaves", "+3 Octaves", "+4 Octaves"
            };
            octaveCombo = new JComboBox<>(octaveOptions);
            octaveCombo.setSelectedIndex(5);
            octaveCombo.addActionListener(e -> {
                int selectedIndex = octaveCombo.getSelectedIndex();
                currentOctaveShift = selectedIndex - 5;
                updateOctave(); // Recalculate note pitches
            });
            octavePanel.add(octaveCombo);
            instrumentsAndShiftPanel.add(octavePanel);
            
            westPanel.add(instrumentsAndShiftPanel ,BorderLayout.WEST);
            // Note names panel (7 rows)
            JPanel notesPanel = new JPanel(new GridLayout(7, 1, 5, 5));
            notesPanel.setBorder(BorderFactory.createTitledBorder("Notes"));
            notesPanel.setPreferredSize(new Dimension(70, 0));
            for (int i = 0; i < 7; i++) {
                JLabel noteLabel = new JLabel(soundsNames[i], SwingConstants.CENTER);
                noteLabel.setFont(noteLabel.getFont().deriveFont(14f));
                notesPanel.add(noteLabel);
            }
            
            westPanel.add(notesPanel, BorderLayout.EAST);
            background.add(BorderLayout.WEST, westPanel);
            
            // ===== EAST PANEL: Playback controls =====
            Box buttonBox = new Box(BoxLayout.Y_AXIS);
            buttonBox.setBorder(BorderFactory.createTitledBorder("Playback"));
            JButton start = new JButton("Start");
            start.addActionListener(e -> startMusic());
            buttonBox.add(start);
            JButton stop = new JButton("Stop");
            stop.addActionListener(e -> stopMusic());
            buttonBox.add(stop);
            background.add(BorderLayout.EAST, buttonBox);
            
            // Initialize the beat matrix grid
            checkboxList = new ArrayList<JCheckBox>();
            createGrid(16);
            
            // ===== SOUTH PANEL: Button to add more instruments =====
            JPanel southPanel = new JPanel();
            JButton addInstrumentButton = new JButton("+ Add New Instrument Below");
            addInstrumentButton.addActionListener(e -> {
                JPanel scrollContent = (JPanel) ((JScrollPane) mainContainer.getComponent(1)).getViewport().getView();
                addNewInstrument(scrollContent);
            });
            southPanel.add(addInstrumentButton);
            background.add(BorderLayout.SOUTH, southPanel);
            
            add(background, BorderLayout.CENTER);
            setUpMidi();
        }
        
        // Removes this instrument from the editor
        private void removeThisInstrument() {
            int confirm = JOptionPane.showConfirmDialog(theFrame, 
                "Remove Instrument #" + instrumentId + "?", 
                "Confirm Removal", 
                JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                stopMusic(); // Stop playback before removal
                JPanel scrollContent = (JPanel) ((JScrollPane) mainContainer.getComponent(1)).getViewport().getView();
                scrollContent.remove(this);
                instrumentPanels.remove(this);
                scrollContent.revalidate();
                scrollContent.repaint();
            }
        }
        
        // Applies octave shift to all notes (12 semitones per octave)
        private void updateOctave() {
            for (int i = 0; i < baseNotes.length; i++) {
                notes[i] = baseNotes[i] + (currentOctaveShift * 12);
            }
        }
        
        // Unchecks all checkboxes in the beat matrix
        private void clearAllCheckboxes() {
            if (checkboxList != null) {
                for (JCheckBox checkBox : checkboxList) {
                    checkBox.setSelected(false);
                }
            }
        }
        
        // Creates the beat matrix grid with specified number of beats
        private void createGrid(int beats) {
            if (mainPanel != null) {
                background.remove(mainPanel);
            }
            
            GridLayout grid = new GridLayout(7, beats); // 7 rows (notes) x beats columns
            grid.setVgap(2);
            grid.setHgap(2);
            mainPanel = new JPanel(grid);
            mainPanel.setBorder(BorderFactory.createTitledBorder("Beat Matrix (" + beats + " beats)"));
            
            if (checkboxList == null) {
                checkboxList = new ArrayList<JCheckBox>();
            } else {
                checkboxList.clear();
            }
            
            // Create checkboxes: row-major order (row 0 all beats, then row 1, etc.)
            for (int i = 0; i < beats * 7; i++) {
                JCheckBox c = new JCheckBox();
                c.setSelected(false);
                checkboxList.add(c);
                mainPanel.add(c);
            }
            
            background.add(BorderLayout.CENTER, mainPanel);
            background.revalidate();
            background.repaint();
            currentBeats = beats;
        }
        
        // Regenerates the grid with new beat count from text field
        private void generateNewGrid() {
            try {
                int beats = Integer.parseInt(beatsTextField.getText().trim());
                if (beats < 1) {
                    JOptionPane.showMessageDialog(theFrame, "Number of beats must be at least 1");
                    return;
                }
                if (beats > 64) {
                    JOptionPane.showMessageDialog(theFrame, "Number of beats cannot exceed 64");
                    return;
                }
                createGrid(beats);
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(theFrame, "Please enter a valid number");
            }
        }
        
        // Initializes the MIDI sequencer for playback
        private void setUpMidi() {
            try {
                sequencer = MidiSystem.getSequencer();
                sequencer.open();
                sequencer.setTempoInBPM(120);
            } catch(Exception e) {e.printStackTrace();}
        }
        
        // Builds the MIDI track from checkbox selections and starts playback
        private void buildTrackAndStart() {
            try {
                if (sequencer.isRunning()) {
                    sequencer.stop();
                }
                
                sequence = new Sequence(Sequence.PPQ, 4); // PPQ = Pulses Per Quarter note
                track = sequence.createTrack();
                
                // Set the instrument (program change)
                ShortMessage instrumentMsg = new ShortMessage();
                instrumentMsg.setMessage(ShortMessage.PROGRAM_CHANGE, 0, currentInstrument, 0);
                track.add(new MidiEvent(instrumentMsg, 0));
                
                // Add silent note at end to ensure all notes stop
                for (int row = 0; row < 7; row++) {
                    ShortMessage silentNote = new ShortMessage();
                    silentNote.setMessage(ShortMessage.NOTE_ON, 0, notes[row], 0);
                    track.add(new MidiEvent(silentNote, currentBeats));
                }
                
                // Iterate through each beat and row to add notes for checked checkboxes
                for (int beat = 0; beat < currentBeats; beat++) {
                    for (int row = 0; row < 7; row++) {
                        int checkboxIndex = (row * currentBeats) + beat;
                        if (checkboxIndex < checkboxList.size()) {
                            JCheckBox check = checkboxList.get(checkboxIndex);
                            if (check.isSelected()) {
                                // Note ON event
                                ShortMessage noteOn = new ShortMessage();
                                noteOn.setMessage(ShortMessage.NOTE_ON, 0, notes[row], 100);
                                track.add(new MidiEvent(noteOn, beat));
                                
                                // Note OFF event (1 beat later)
                                ShortMessage noteOff = new ShortMessage();
                                noteOff.setMessage(ShortMessage.NOTE_OFF, 0, notes[row], 100);
                                track.add(new MidiEvent(noteOff, beat + 1));
                            }
                        }
                    }
                }
                
                // Configure and start the sequencer with looping
                sequencer.setSequence(sequence);
                sequencer.setLoopStartPoint(0);
                sequencer.setLoopEndPoint(currentBeats);
                sequencer.setLoopCount(Sequencer.LOOP_CONTINUOUSLY);
                sequencer.start();
            
            } catch (Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(theFrame, "Error playing music: " + e.getMessage());
            }
        }
        
        // Public method to start music playback
        public void startMusic() {
            buildTrackAndStart();
        }
        
        // Public method to stop music playback
        public void stopMusic() {
            if (sequencer != null && sequencer.isRunning()) {
                sequencer.stop();
            }
        }
        
        // Public method to change tempo
        public void setTempo(int bpm) {
            if (sequencer != null && sequencer.isOpen()) {
                sequencer.setTempoInBPM(bpm);
            }
        }
    }
}