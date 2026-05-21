/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package music.editor;

import java.awt.BorderLayout;
import java.awt.Color;
import java.util.ArrayList;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

/**
 *
 * @author desktop
 */

class PartiturePanel extends JPanel {
    
    // STEAM THEME COLORS
    private final Color steamDark = new Color(27, 40, 56);
    private final Color steamLight = new Color(45, 65, 85);
    private final Color steamBorder = new Color(90, 110, 130);
    private final Color creamText = new Color(245, 235, 210);
    
    private ArrayList<InstrumentPanel> instruments;
    private JPanel instrumentsContainer;

    private int partitureId;

    public PartiturePanel(int id) {

        this.partitureId = id;
        instruments = new ArrayList<>();

        setLayout(new BorderLayout());
        
        setBorder(
            BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(steamBorder),
                "Partiture #" + partitureId,
                0,
                0,
                null,
                creamText
            )
        );

        instrumentsContainer = new JPanel();
        instrumentsContainer.setLayout(new BoxLayout(instrumentsContainer, BoxLayout.Y_AXIS));

        instrumentsContainer.setBackground(steamDark);
        
       // JScrollPane scroll = new JScrollPane(instrumentsContainer);
        
        add(instrumentsContainer, BorderLayout.CENTER);
        
        //add(scroll, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel();
        
        bottomPanel.setBackground(steamDark);

        JButton addInstrument =
            new JButton("+ Add Instrument");
        
        addInstrument.setBackground(steamLight);
        addInstrument.setForeground(creamText);      
        addInstrument.setFocusPainted(false);

        addInstrument.addActionListener(e -> {
            addNewInstrument();
        });

        bottomPanel.add(addInstrument);

        add(bottomPanel, BorderLayout.SOUTH);

        addNewInstrument();
    }
    private void addNewInstrument() {

        InstrumentPanel ip = new InstrumentPanel(instruments.size() + 1, this);

        instruments.add(ip);

        instrumentsContainer.add(ip);

        instrumentsContainer.revalidate();
        instrumentsContainer.repaint();
    }
    
    public void playAllInstruments() {

        for(InstrumentPanel ip : instruments) {
            ip.startMusic();
        }
    }

    public void stopAllInstruments() {

        for(InstrumentPanel ip : instruments) {
            ip.stopMusic();
        }
    }
    
    public long getDurationMillis() {

        int maxBeats = 0;

        for(InstrumentPanel ip : instruments) {

            if(ip.getCurrentBeats() > maxBeats) {
                maxBeats = ip.getCurrentBeats();
            }
        }

        float bpm = instruments.get(0).getTempo();

        long millis =
            (long)((60000.0 / bpm) * maxBeats);

        return millis;
    }
    
    public void setTempoForAllInstruments(int bpm) {
        for (InstrumentPanel ip : instruments) {
            ip.setTempo(bpm);
        }
    }

    public void removeInstrument(InstrumentPanel ip) {

        instruments.remove(ip);

        instrumentsContainer.remove(ip);

        instrumentsContainer.revalidate();

        instrumentsContainer.repaint();
    }

}