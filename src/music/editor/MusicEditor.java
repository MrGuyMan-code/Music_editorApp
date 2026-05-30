/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package music.editor;

/**
 *
 * @author desktop
 */

import music.editor.theme.SteamComboBoxUI;
import music.editor.theme.SteamScrollBarUI;
import music.editor.theme.SteamColors;
import music.editor.io.ProjectFileManager;
import java.awt.BorderLayout;
import java.util.ArrayList;
import javax.swing.*;

public class MusicEditor implements SteamColors{
    
    
    // UI components for the main editor window
    private JPanel mainContainer; // Holds all instrument panels in a scrollable area
    private ArrayList<PartiturePanel> partiturePanels; // Collection of all musical instrument panels
    private JFrame theFrame;
    private volatile boolean stopPlayback = false;

    private ProjectFileManager fileManager;
    
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
        
        fileManager = new ProjectFileManager(theFrame);
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
        
        exportMidi.addActionListener(e -> fileManager.saveProject(partiturePanels));
        
        importMidi.addActionListener(e -> {
            fileManager.loadProject(partiturePanels);
            refreshPartitureView();
        });
          
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

            String selected =
                (String) tempoCombo.getSelectedItem();

            if (selected.equals("Custom...")) {

                String input =
                    JOptionPane.showInputDialog(
                        theFrame,
                        "Enter tempo (BPM):",
                        "120"
                    );

                if (input != null) {

                    try {

                        int tempo =
                            Integer.parseInt(input);

                        if (tempo >= 40 && tempo <= 240) {

                            setTempoForAll(tempo);

                            System.out.println(
                                "Noul tempo este: " + tempo
                            );

                        } else {

                            JOptionPane.showMessageDialog(
                                theFrame,
                                "Tempo must be between 40 and 240 BPM"
                            );
                        }

                    } catch (NumberFormatException ex) {

                        JOptionPane.showMessageDialog(
                            theFrame,
                            "Please enter a valid number"
                        );
                    }
                }

            } else {

                int tempo =
                    Integer.parseInt(
                        selected.split(" ")[0]
                    );

                setTempoForAll(tempo);
            }
        });
        globalTopPanel.add(tempoCombo);
        
        // Global play/stop buttons
        JButton playAllButton = new JButton("Play All");
        playAllButton.addActionListener(e -> playAllPartituresSequentially());
        
        styleButton(playAllButton);
        
        globalTopPanel.add(playAllButton);
        
        JButton stopAllButton = new JButton("Stop All");
        stopAllButton.addActionListener(e -> {stopPlayback = true;  for(PartiturePanel pp : partiturePanels) {pp.stopAllInstruments();}});
        
        styleButton(stopAllButton);
        
        globalTopPanel.add(stopAllButton);
        
        JButton addPartitureButton = new JButton("+ Add Partiture");
        
        styleButton(addPartitureButton);

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

        scrollPane.getViewport().setBackground(steamDark);
        
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

        stopPlayback = false;
        
        new Thread(() -> {

            try {

                for (PartiturePanel pp : partiturePanels) {

                    // STOP REQUESTED
                    if (stopPlayback) {
                        break;
                    }
                    
                    pp.playAllInstruments();

                    Thread.sleep(pp.getDurationMillis());

                    pp.stopAllInstruments();
                }

            } catch (Exception ex) {
                ex.printStackTrace();
            }

        }).start();
    }
    
    private void refreshPartitureView() {

        JPanel scrollContent =
            (JPanel)
            ((JScrollPane)
                mainContainer.getComponent(1))
            .getViewport()
            .getView();

        scrollContent.removeAll();

        for (PartiturePanel pp : partiturePanels) {

            scrollContent.add(pp);

            scrollContent.add(
                Box.createVerticalStrut(15)
            );
        }

        scrollContent.revalidate();

        scrollContent.repaint();
    }
    
    private void setTempoForAll(int bpm) {

        for (PartiturePanel pp : partiturePanels) {

            pp.setTempoForAllInstruments(bpm);
        }
    }
    
    private void styleButton(JButton button) {

        button.setBackground(steamLight);
        button.setForeground(creamText);
        button.setFocusPainted(false);
    }

}