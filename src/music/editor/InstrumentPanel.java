/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package music.editor;

import music.editor.theme.SteamComboBoxUI;
import music.editor.theme.SteamColors;
import music.editor.grid.NoteGrid;
import music.editor.grid.NoteInfo;
import music.editor.midi.MidiPatternPlayer;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import java.awt.Color;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 *
 * @author desktop
 */
    // Inner class representing a single instrument panel (one instrument's pattern)
    class InstrumentPanel extends JPanel implements SteamColors{
        
        // Instrument identification and UI components
        private int instrumentId;
        private JPanel background;
        private JPanel mainPanel;
        
        private NoteGrid noteGrid;
        private JCheckBox[][] checkBoxGrid;     // Grid of checkboxes (rows x beats)
        
        private MidiPatternPlayer player;
        
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
        
        private boolean dragging = false;

        private int dragStartRow = -1;
        private int dragStartBeat = -1;
        
        private String[] soundsNames = {"SI", "LA", "SOL", "FA", "MI", "RE", "DO"};

        private int[] instruments = {

            // Piano
            0, 1, 4,

            // Bass
            33, 38,

            // Strings
            40, 48, 50,

            // Choir / Pads
            52, 54, 89,

            // Synth Leads
            80, 81, 82, 84,

            // Percussion
            35, 42, 46, 38, 49,
            39, 50, 60, 70, 72,
            64, 56, 58, 47, 67, 63
        };
        
        private PartiturePanel parentPartiture;

        private String[] instrumentNames = {

            // Piano
            "Acoustic Grand Piano (0)",
            "Bright Piano (1)",
            "Electric Piano (4)",

            // Bass
            "Finger Bass (33)",
            "Synth Bass (38)",

            // Strings
            "Violin (40)",
            "String Ensemble (48)",
            "Synth Strings (50)",

            // Choir / Pads
            "Choir Aahs (52)",
            "Synth Voice (54)",
            "Warm Pad (89)",

            // Synth Leads
            "Lead Square (80)",
            "Lead Saw Wave (81)",
            "Lead Calliope (82)",
            "Lead Charang (84)",

            // Percussion
            "Bass Drum (35)",
            "Closed Hi-Hat (42)",
            "Open Hi-Hat (46)",
            "Acoustic Snare (38)",
            "Crash Cymbal (49)",
            "Hand Clap (39)",
            "Hi Tom (50)",
            "Low Bongo (60)",
            "Mute Cuica (70)",
            "Open Cuica (72)",
            "Low Timbal (64)",
            "High Agogo (56)",
            "Cabasa (58)",
            "Maracas (47)",
            "High Wood Block (67)",
            "Low Wood Block (63)"
        };
        public InstrumentPanel(int id, PartiturePanel parent) {

            this.instrumentId = id;
            this.parentPartiture = parent;
            this.setBackground(steamDark);
            setupInstrument();
        }
        
        // Builds the complete UI for a single instrument
        private void setupInstrument() {

           setLayout(new BorderLayout());

           setBackground(steamDark);

           setBorder(
               BorderFactory.createTitledBorder(
                   BorderFactory.createLineBorder(steamBorder),
                   "Instrument #" + instrumentId,
                   0,
                   0,
                   null,
                   creamText
               )
           );

           setPreferredSize(new Dimension(850, 400));
           setMaximumSize(new Dimension(850, 400));

           background = new JPanel(new BorderLayout());

           background.setBackground(steamDark);

           background.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

           // ===== TOP PANEL =====

           JPanel topPanel = new JPanel();

           topPanel.setBackground(steamDark);

           JLabel beatsLabel = new JLabel("Beats:");

           beatsLabel.setForeground(creamText);

           topPanel.add(beatsLabel);

           beatsTextField = new JTextField("16", 4);

           beatsTextField.setBackground(steamLight);

           beatsTextField.setForeground(creamText);

           beatsTextField.setCaretColor(creamText);

           beatsTextField.setBorder(BorderFactory.createLineBorder(steamBorder));

           topPanel.add(beatsTextField);

           // GENERATE BUTTON

           JButton generateButton = new JButton("Generate Grid");

           generateButton.addActionListener(e -> generateNewGrid());

           generateButton.setBackground(steamLight);

           generateButton.setForeground(creamText);

           generateButton.setFocusPainted(false);

           topPanel.add(generateButton);

           // CLEAR BUTTON

           JButton clearAllButton = new JButton("Clear All");

           clearAllButton.addActionListener(e -> clearAllCheckboxes());

           clearAllButton.setBackground(steamLight);

           clearAllButton.setForeground(creamText);

           clearAllButton.setFocusPainted(false);

           topPanel.add(clearAllButton);

           // REMOVE BUTTON

           JButton removeButton = new JButton("Remove Instrument");

           removeButton.addActionListener(e -> removeThisInstrument());

           removeButton.setBackground(steamLight);

           removeButton.setForeground(creamText);

           removeButton.setFocusPainted(false);

           topPanel.add(removeButton);

           background.add(BorderLayout.NORTH, topPanel);

           // ===== WEST PANEL =====

           JPanel westPanel = new JPanel(new BorderLayout());

           westPanel.setBackground(steamDark);

           JPanel instrumentsAndShiftPanel = new JPanel(new GridLayout(2, 1, 0, 10));

           instrumentsAndShiftPanel.setBackground(steamDark);

           // INSTRUMENT PANEL

           JPanel instrumentPanel = new JPanel(new BorderLayout());

           instrumentPanel.setBackground(steamDark);

           instrumentPanel.setBorder(
               BorderFactory.createTitledBorder(
                   BorderFactory.createLineBorder(
                       steamBorder
                   ),
                   "Select Instrument",
                   0,
                   0,
                   null,
                   creamText
               )
           );

           instrumentCombo = new JComboBox<>(instrumentNames);
           
           instrumentCombo.setUI(new SteamComboBoxUI());

           instrumentCombo.addActionListener(e -> {

               int index =
                   instrumentCombo.getSelectedIndex();

               currentInstrument =
                   instruments[index];
           });

           instrumentPanel.add(
               instrumentCombo,
               BorderLayout.CENTER
           );

           instrumentsAndShiftPanel.add(
               instrumentPanel
           );

           // OCTAVE PANEL

           JPanel octavePanel =
               new JPanel(new BorderLayout());

           octavePanel.setBackground(steamDark);

           octavePanel.setBorder(
               BorderFactory.createTitledBorder(
                   BorderFactory.createLineBorder(
                       steamBorder
                   ),
                   "Octave Shift",
                   0,
                   0,
                   null,
                   creamText
               )
           );

           String[] octaveOptions = {
               "-5 Octaves", "-4 Octaves", "-3 Octaves", "-2 Octaves", "-1 Octave",
               "0 (Default)", "+1 Octave", "+2 Octaves", "+3 Octaves","+4 Octaves"};

           octaveCombo = new JComboBox<>(octaveOptions);
           
           octaveCombo.setUI(new SteamComboBoxUI());

           octaveCombo.setSelectedIndex(5);

           octaveCombo.addActionListener(e -> {

               int selectedIndex = octaveCombo.getSelectedIndex();

               currentOctaveShift = selectedIndex - 5;

               updateOctave();
           });

           octavePanel.add(octaveCombo);

           instrumentsAndShiftPanel.add(octavePanel);

           westPanel.add(
               instrumentsAndShiftPanel,
               BorderLayout.WEST
           );

           // NOTES PANEL

           JPanel notesPanel = new JPanel(new GridLayout(7, 1, 5, 5));

           notesPanel.setBackground(steamDark);

           notesPanel.setPreferredSize(
               new Dimension(67, 0)
           );

           notesPanel.setBorder(
               BorderFactory.createTitledBorder(
                   BorderFactory.createLineBorder(
                       steamBorder
                   ),
                   "Notes",
                   0,
                   0,
                   null,
                   creamText
               )
           );

           for (int i = 0; i < 7; i++) {

               JLabel noteLabel =
                   new JLabel(
                       soundsNames[i],
                       SwingConstants.CENTER
                   );

               noteLabel.setForeground(creamText);

               noteLabel.setFont(
                   noteLabel.getFont().deriveFont(14f)
               );

               notesPanel.add(noteLabel);
           }

           westPanel.add(notesPanel, BorderLayout.EAST);

           background.add(BorderLayout.WEST, westPanel);

           // ===== PLAYBACK =====

           Box buttonBox = new Box(BoxLayout.Y_AXIS);

           buttonBox.setBackground(steamDark);

           buttonBox.setBorder(
               BorderFactory.createTitledBorder(
                   BorderFactory.createLineBorder(
                       steamBorder
                   ),
                   "Playback",
                   0,
                   0,
                   null,
                   creamText
               )
           );
           
           // SAME SIZE
            Dimension buttonSize = new Dimension(67, 25);

           JButton start = new JButton("Start");

           start.addActionListener(e -> startMusic());

           start.setBackground(steamLight);

           start.setForeground(creamText);

           start.setFocusPainted(false);
           
           
           start.setPreferredSize(buttonSize);
           start.setMaximumSize(buttonSize);

           buttonBox.add(start);

           JButton stop = new JButton("Stop");

           stop.addActionListener(e -> stopMusic());

           stop.setBackground(steamLight);

           stop.setForeground(creamText);

           stop.setFocusPainted(false);
           
           stop.setPreferredSize(buttonSize);
           stop.setMaximumSize(buttonSize);

           buttonBox.add(stop);

           background.add(
               BorderLayout.EAST,
               buttonBox
           );

           // ===== GRID =====

           createGrid(16);
           
           player = new MidiPatternPlayer();
           
           add(background, BorderLayout.CENTER);

       }
        
        // Removes this instrument from the editor
        private void removeThisInstrument() {

            int confirm = JOptionPane.showConfirmDialog(
                this,
                "Remove Instrument #" + instrumentId + "?",
                "Confirm Removal",
                JOptionPane.YES_NO_OPTION
            );

            if (confirm == JOptionPane.YES_OPTION) {

                stopMusic();

                parentPartiture.removeInstrument(this);
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

            noteGrid.clear();

            refreshGrid();
        }
        
        private void refreshGrid() {

            for (int row = 0; row < 7; row++) {

                refreshRowVisuals(row);
            }
        }
        
        // Creates the beat matrix grid with specified number of beats
        private void createGrid(int beats) {

            if (mainPanel != null) {
                background.remove(mainPanel);
            }

            GridLayout grid = new GridLayout(7, beats);
            grid.setVgap(2);
            grid.setHgap(2);

            mainPanel = new JPanel(grid);
            mainPanel.setBackground(steamDark);
            mainPanel.setBorder(
                BorderFactory.createTitledBorder(
                    BorderFactory.createLineBorder(steamBorder),
                    "Beat Matrix (" + beats + " beats)",
                    0,
                    0,
                    null,
                    creamText
                )
            );

            // ✅ ONLY ONE SOURCE OF TRUTH
            if (noteGrid == null) {
                noteGrid = new NoteGrid(beats);
            } else {
                noteGrid.resize(beats);
            }

            checkBoxGrid = new JCheckBox[7][beats];

            for (int row = 0; row < 7; row++) {
                for (int beat = 0; beat < beats; beat++) {

                    JCheckBox box = new JCheckBox() {
                        @Override
                        protected void paintComponent(java.awt.Graphics g) {
                            g.setColor(getBackground());
                            g.fillRect(0, 0, getWidth(), getHeight());
                        }
                    };

                    box.setOpaque(true);
                    box.setBorder(BorderFactory.createLineBorder(new Color(70, 90, 110)));
                    box.setBackground(new Color(45, 65, 85));
                    box.setFocusPainted(false);
                    box.setContentAreaFilled(false);
                    box.setPreferredSize(new Dimension(24, 24));

                    final int currentRow = row;
                    final int currentBeat = beat;

                    box.addMouseListener(new MouseAdapter() {

                        @Override
                        public void mousePressed(MouseEvent e) {

                            if (e.getButton() != MouseEvent.BUTTON1) return;

                            dragging = true;
                            dragStartRow = currentRow;
                            dragStartBeat = currentBeat;

                            // ✅ USE NOTEGRID API ONLY
                            if (noteGrid.isActive(currentRow, currentBeat)) {

                                noteGrid.clearNote(currentRow, currentBeat);
                                refreshRowVisuals(currentRow);

                                dragging = false;
                                return;
                            }

                            //noteGrid.setActive(currentRow, currentBeat, true);
                            //noteGrid.setContinuation(currentRow, currentBeat, false);
                            
                            noteGrid.extendNote(currentRow,currentBeat,currentBeat);

                            refreshRowVisuals(currentRow);
                        }

                        @Override
                        public void mouseEntered(MouseEvent e) {

                            if (!dragging) return;
                            if (currentRow != dragStartRow) return;
                            if (currentBeat < dragStartBeat) return;

                            noteGrid.extendNote(currentRow,dragStartBeat,currentBeat);

                            refreshRowVisuals(currentRow);
                        }

                        @Override
                        public void mouseReleased(MouseEvent e) {
                            dragging = false;
                        }
                    });

                    checkBoxGrid[row][beat] = box;
                    mainPanel.add(box);
                }
            }

            background.add(BorderLayout.CENTER, mainPanel);
            background.revalidate();
            background.repaint();

            currentBeats = beats;
        }
        
        private void updateCellVisual(int row, int beat, int duration) {

            //NoteCell cell = noteGrid[row][beat];

            JCheckBox box = checkBoxGrid[row][beat];

            if (!noteGrid.isActive(row, beat)) {

                box.setSelected(false);

                box.setBackground(new Color(45, 65, 85));

                return;
            }
            
            
            box.setSelected(true);

            box.setOpaque(true);

            // SMOOTH COLOR GRADIENT

            int red;
            int green;

            // LIMIT MAX VISUAL LENGTH
            int cappedDuration = Math.min(duration, 8);

            // MORE LENGTH = MORE RED
            red = 30 * cappedDuration;

            // MORE LENGTH = LESS GREEN
            green = 255 - (25 * cappedDuration);

            // CLAMP VALUES
            red = Math.min(255, Math.max(0, red));
            green = Math.min(255, Math.max(0, green));

            box.setBackground(new Color(red, green, 40));
        }
        
        // Regenerates the grid with new beat count from text field
        private void generateNewGrid() {
            try {
                int beats = Integer.parseInt(beatsTextField.getText().trim());
                if (beats < 1) {
                    JOptionPane.showMessageDialog(this, "Number of beats must be at least 1");
                    return;
                }
                if (beats > 64) {
                    JOptionPane.showMessageDialog(this, "Number of beats cannot exceed 64");
                    return;
                }
                createGrid(beats);
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Please enter a valid number");
            }
        }
        
        private void refreshRowVisuals(int row) {

            // RESET ROW VISUALS
            for (int beat = 0; beat < currentBeats; beat++) {

                updateCellVisual(row, beat, 1);
            }

            // DRAW NOTES
            for (NoteInfo info : noteGrid.getNotesInRow(row)) {

                int start = info.getStartBeat();

                int duration = info.getDuration();

                for (int b = start; b < start + duration; b++) {

                    updateCellVisual(row, b, duration);
                }
            }
        }
   
        public int getCurrentInstrument() {
            return currentInstrument;
        }

        public int getCurrentOctaveShift() {
            return currentOctaveShift;
        }

        public boolean[][] getActiveData() {

            boolean[][] data =
                new boolean[7][currentBeats];

            for (int row = 0; row < 7; row++) {

                for (int beat = 0; beat < currentBeats; beat++) {

                    data[row][beat] = noteGrid.isActive(row, beat);
                }
            }

            return data;
        }

        public boolean[][] getContinuationData() {

            boolean[][] data = new boolean[7][currentBeats];

            for (int row = 0; row < 7; row++) {
                for (int beat = 0; beat < currentBeats; beat++) {
                    data[row][beat] = noteGrid.isContinuation(row, beat);
                }
            }

            return data;
        }

        public void setCurrentInstrument(int instrument) {

            currentInstrument = instrument;
            // UPDATE COMBOBOX VISUALLY
            for (int i = 0; i < instruments.length; i++) {

                if (instruments[i] == instrument) {
                    instrumentCombo.setSelectedIndex(i);
                    break;
                }
            }
        }

        public void setCurrentOctaveShift(int shift) {
            currentOctaveShift = shift;
            octaveCombo.setSelectedIndex(shift + 5);
            updateOctave();
        }

        public void setCurrentBeats(int beats) {
            currentBeats = beats;
            beatsTextField.setText("" + beats);
            createGrid(beats);
        }

        public void loadNoteData(boolean[][] active, boolean[][] continuation) {
            noteGrid.load(active, continuation);
            refreshGrid();
        }
                
        // Public method to start music playback
        public void startMusic() {
            player.play(noteGrid, notes, currentInstrument, currentBeats);
        }
        
        // Public method to stop music playback
        public void stopMusic() {
            player.stop();
        }
        
        // Public method to change tempo
        public void setTempo(int bpm) {
            player.setTempo(bpm);
        }
        
        public int getCurrentBeats() {
            return currentBeats;
        }

        public float getTempo() {
            return player.getTempo();
        }      
    }
