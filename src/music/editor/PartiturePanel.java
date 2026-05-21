/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package music.editor;

import music.editor.theme.SteamColors;
import java.awt.BorderLayout;
import java.awt.Color;
import java.util.ArrayList;
import javax.sound.midi.Sequence;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

/**
 *
 * @author desktop
 */

class PartiturePanel extends JPanel implements SteamColors{
    
    
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
            (long)((60000.0 / bpm) * (maxBeats / 4.0));

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
    
    public ArrayList<InstrumentPanel> getInstruments() {
        return instruments;
    }

    public int getInstrumentCount() {
        return instruments.size();
    }

    public void addLoadedInstrument(
        int instrument,
        int beats,
        int octaveShift,
        boolean[][] active,
        boolean[][] continuation
    ) {

        InstrumentPanel ip =
            new InstrumentPanel(
                instruments.size() + 1,
                this
            );

        ip.setCurrentInstrument(instrument);

        ip.setCurrentOctaveShift(octaveShift);

        ip.setCurrentBeats(beats);

        ip.loadNoteData(active, continuation);

        instruments.add(ip);

        instrumentsContainer.add(ip);

        instrumentsContainer.revalidate();

        instrumentsContainer.repaint();
    }

    void clearInstruments() {
        instruments.clear();

        instrumentsContainer.removeAll();

        instrumentsContainer.revalidate();

        instrumentsContainer.repaint();
    }

}