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
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Label;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.Sequence;
import javax.sound.midi.Sequencer;
import javax.sound.midi.Track;
import javax.sound.midi.ShortMessage;
import javax.sound.midi.MidiEvent;
import javax.swing.*;

public class MusicEditor implements SteamColors{
    
    
    // UI components for the main editor window
    JPanel mainContainer; // Holds all instrument panels in a scrollable area
    ArrayList<PartiturePanel> partiturePanels; // Collection of all musical instrument panels
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
        mainContainer.setBackground(steamDark);
        
        
        //The Menu Bar
        JMenuBar menuBar = new JMenuBar();

        menuBar.setBackground(steamDark);
        menuBar.setBorder(BorderFactory.createLineBorder(steamBorder));

        JMenu fileMenu = new JMenu("File");

        fileMenu.setForeground(creamText);
        fileMenu.setBackground(steamDark);

        JMenuItem exportMidi = new JMenuItem("Export Music");
        JMenuItem importMidi = new JMenuItem("Import Music");

        exportMidi.setBackground(steamLight);
        exportMidi.setForeground(creamText);

        importMidi.setBackground(steamLight);
        importMidi.setForeground(creamText);

        fileMenu.add(exportMidi);
        fileMenu.add(importMidi);

        menuBar.add(fileMenu);

        theFrame.setJMenuBar(menuBar);
        
        exportMidi.addActionListener(e -> exportProjectFile());
        
        importMidi.addActionListener(e -> importProjectFile());
          
        // ===== TOP PANEL: Global controls that affect all instruments =====
        JPanel globalTopPanel = new JPanel();
        JLabel globalControlsLabel = new JLabel("Global Controls");
        globalControlsLabel.setForeground(creamText);
        globalTopPanel.add(globalControlsLabel);
        
        globalTopPanel.setBackground(steamDark);
        
        // Tempo control dropdown
        JLabel tempoLabel = new JLabel("  Tempo:");
        tempoLabel.setForeground(creamText);
        globalTopPanel.add(tempoLabel);
        JComboBox<String> tempoCombo = new JComboBox<>(new String[]{"100 BPM", "120 BPM", "140 BPM", "Custom..."});
        
        tempoCombo.setUI(new SteamComboBoxUI());
        
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
        playAllButton.addActionListener(e -> playAllPartituresSequentially());
        
        playAllButton.setBackground(steamLight);
        playAllButton.setForeground(creamText);      
        playAllButton.setFocusPainted(false);
        
        globalTopPanel.add(playAllButton);
        
        JButton stopAllButton = new JButton("Stop All");
        stopAllButton.addActionListener(e -> {for(PartiturePanel pp : partiturePanels) {pp.stopAllInstruments();}});
        
        stopAllButton.setBackground(steamLight);
        stopAllButton.setForeground(creamText);      
        stopAllButton.setFocusPainted(false);
        
        globalTopPanel.add(stopAllButton);
        
        JButton addPartitureButton = new JButton("+ Add Partiture");
        
        addPartitureButton.setBackground(steamLight);
        addPartitureButton.setForeground(creamText);      
        addPartitureButton.setFocusPainted(false);

        globalTopPanel.add(addPartitureButton);
        
        mainContainer.add(BorderLayout.NORTH, globalTopPanel);
        
        // ===== SCROLLABLE AREA: Holds all individual instrument panels =====
        JPanel scrollContent = new JPanel();
        scrollContent.setLayout(new BoxLayout(scrollContent, BoxLayout.Y_AXIS));
        JScrollPane scrollPane = new JScrollPane(scrollContent);
        scrollPane.getVerticalScrollBar().setUI(new SteamScrollBarUI());
        //scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        //scrollPane.getHorizontalScrollBar().setUI(new SteamScrollBarUI());
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        
        scrollPane.setBorder(null);

        scrollPane.getViewport().setBackground(
            new Color(27, 40, 56)
        );
        
        addPartitureButton.addActionListener(e -> {addNewPartiture(scrollContent);});
        
        mainContainer.add(BorderLayout.CENTER, scrollPane);
        
        // Initialize collection and add the first partiture
        partiturePanels = new ArrayList<>();
        addNewPartiture(scrollContent);
        
        theFrame.getContentPane().add(mainContainer);
        theFrame.setBounds(50, 50, 900, 700);
        theFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        theFrame.setVisible(true);
    }
    
    private void addNewPartiture(JPanel scrollContent) {

        PartiturePanel pp =
            new PartiturePanel(partiturePanels.size() + 1);
        
        pp.setBackground(steamDark);
        
        partiturePanels.add(pp);
        
        scrollContent.setBackground(steamDark);

        scrollContent.add(pp);
        
        scrollContent.add(Box.createVerticalStrut(15));

        scrollContent.revalidate();
        scrollContent.repaint();
    }
    
        private void importProjectFile() {

            try {

                JFileChooser chooser =
                    new JFileChooser();

                int result =
                    chooser.showOpenDialog(theFrame);

                if (result != JFileChooser.APPROVE_OPTION) {
                    return;
                }

                java.io.File file =
                    chooser.getSelectedFile();

                ObjectInputStream in =
                    new ObjectInputStream(
                        new FileInputStream(file)
                    );

                // GET SCROLL CONTENT
                JPanel scrollContent =
                    (JPanel)
                    ((JScrollPane) mainContainer.getComponent(1))
                    .getViewport()
                    .getView();

                // CLEAR CURRENT PROJECT
                partiturePanels.clear();

                scrollContent.removeAll();

                int partitureCount =
                    in.readInt();

                for (
                    int p = 0;
                    p < partitureCount;
                    p++
                ) {

                    PartiturePanel pp =
                        new PartiturePanel(p + 1);

                    pp.clearInstruments();

                    int instrumentCount =
                        in.readInt();

                    for (
                        int i = 0;
                        i < instrumentCount;
                        i++
                    ) {

                        int instrument =
                            in.readInt();

                        int beats =
                            in.readInt();

                        int octaveShift =
                            in.readInt();

                        boolean[][] active =
                            new boolean[7][beats];

                        boolean[][] continuation =
                            new boolean[7][beats];

                        // READ GRID DATA
                        for (int row = 0; row < 7; row++) {

                            for (
                                int beat = 0;
                                beat < beats;
                                beat++
                            ) {

                                active[row][beat] =
                                    in.readBoolean();

                                continuation[row][beat] =
                                    in.readBoolean();
                            }
                        }

                        // CREATE INSTRUMENT
                        pp.addLoadedInstrument(
                            instrument,
                            beats,
                            octaveShift,
                            active,
                            continuation
                        );
                    }

                    pp.setBackground(steamDark);
                    
                    partiturePanels.add(pp);

                    scrollContent.add(pp);

                    scrollContent.add(
                        Box.createVerticalStrut(15)
                    );
                }

                in.close();

                scrollContent.revalidate();

                scrollContent.repaint();

                JOptionPane.showMessageDialog(
                    theFrame,
                    "Project imported successfully!"
                );
            }

            catch (Exception ex) {

                ex.printStackTrace();

                JOptionPane.showMessageDialog(
                    theFrame,
                    "Import error: " + ex.getMessage()
                );
            }
        }
    
    private void exportProjectFile() {

        try {

            JFileChooser chooser =
                new JFileChooser();

            int result =
                chooser.showSaveDialog(theFrame);

            if (result != JFileChooser.APPROVE_OPTION) {
                return;
            }

            java.io.File file =
                chooser.getSelectedFile();

            if (!file.getName().toLowerCase().endsWith(".cbb")) {

                file = new java.io.File(
                    file.getAbsolutePath() + ".cbb"
                );
            }

            ObjectOutputStream out =
                new ObjectOutputStream(
                    new FileOutputStream(file)
                );

            // SAVE PARTITURE COUNT
            out.writeInt(partiturePanels.size());

            for (PartiturePanel pp : partiturePanels) {

                // SAVE INSTRUMENT COUNT
                out.writeInt(
                    pp.getInstruments().size()
                );

                for (InstrumentPanel ip : pp.getInstruments()) {

                    out.writeInt(
                        ip.getCurrentInstrument()
                    );

                    out.writeInt(
                        ip.getCurrentBeats()
                    );

                    out.writeInt(
                        ip.getCurrentOctaveShift()
                    );

                    boolean[][] active =
                        ip.getActiveData();

                    boolean[][] continuation =
                        ip.getContinuationData();

                    for (int row = 0; row < 7; row++) {

                        for (
                            int beat = 0;
                            beat < ip.getCurrentBeats();
                            beat++
                        ) {

                            out.writeBoolean(
                                active[row][beat]
                            );

                            out.writeBoolean(
                                continuation[row][beat]
                            );
                        }
                    }
                }
            }

            out.close();

            JOptionPane.showMessageDialog(
                theFrame,
                "Project exported successfully!"
            );
        }

        catch (Exception ex) {

            ex.printStackTrace();

            JOptionPane.showMessageDialog(
                theFrame,
                "Export error: " + ex.getMessage()
            );
        }
    }
    
    private void playAllPartituresSequentially() {

        new Thread(() -> {

            try {

                for (PartiturePanel pp : partiturePanels) {

                    pp.playAllInstruments();

                    Thread.sleep(pp.getDurationMillis());

                    pp.stopAllInstruments();
                }

            } catch (Exception ex) {
                ex.printStackTrace();
            }

        }).start();
    }
    
    private void setTempoForAll(int bpm) {

        for (PartiturePanel pp : partiturePanels) {

            pp.setTempoForAllInstruments(bpm);
        }
    }

}