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
import java.util.ArrayList;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.Sequence;
import javax.sound.midi.Sequencer;
import javax.sound.midi.Track;
import javax.sound.midi.ShortMessage;
import javax.sound.midi.MidiEvent;
import javax.swing.*;

public class MusicEditor {
    
        // STEAM THEME COLORS
    private final Color steamDark = new Color(27, 40, 56);

    private final Color steamLight = new Color(45, 65, 85);

    private final Color steamBorder = new Color(90, 110, 130);

    private final Color creamText = new Color(245, 235, 210);
    
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
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        
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