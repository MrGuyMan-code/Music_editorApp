/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package music.editor.io;

import music.editor.InstrumentPanel;
import music.editor.PartiturePanel;

import javax.swing.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

public class ProjectFileManager {

    private final JFrame parentFrame;

    public ProjectFileManager(JFrame parentFrame) {
        this.parentFrame = parentFrame;
    }

    public void saveProject(
        ArrayList<PartiturePanel> partiturePanels
    ) {

        try {

            JFileChooser chooser =
                new JFileChooser();

            int result =
                chooser.showSaveDialog(parentFrame);

            if (
                result != JFileChooser.APPROVE_OPTION
            ) {
                return;
            }

            File file =
                chooser.getSelectedFile();

            if (
                !file.getName()
                    .toLowerCase()
                    .endsWith(".cbb")
            ) {

                file =
                    new File(
                        file.getAbsolutePath()
                        + ".cbb"
                    );
            }

            ObjectOutputStream out =
                new ObjectOutputStream(
                    new FileOutputStream(file)
                );

            out.writeInt(
                partiturePanels.size()
            );

            for (
                PartiturePanel pp :
                partiturePanels
            ) {

                out.writeInt(
                    pp.getInstruments().size()
                );

                for (
                    InstrumentPanel ip :
                    pp.getInstruments()
                ) {

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

                    for (
                        int row = 0;
                        row < 7;
                        row++
                    ) {

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
                parentFrame,
                "Project exported successfully!"
            );
        }

        catch (Exception ex) {

            ex.printStackTrace();

            JOptionPane.showMessageDialog(
                parentFrame,
                "Export error: "
                + ex.getMessage()
            );
        }
    }

    public void loadProject(ArrayList<PartiturePanel> partiturePanels) {

        try {

            JFileChooser chooser =
                new JFileChooser();

            int result =
                chooser.showOpenDialog(
                    parentFrame
                );

            if (
                result != JFileChooser.APPROVE_OPTION
            ) {
                return;
            }

            File file =
                chooser.getSelectedFile();

            ObjectInputStream in =
                new ObjectInputStream(
                    new FileInputStream(file)
                );

            partiturePanels.clear();

            int partitureCount =
                in.readInt();

            for (
                int p = 0;
                p < partitureCount;
                p++
            ) {

                PartiturePanel pp =
                    new PartiturePanel(
                        p + 1
                    );

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

                    for (
                        int row = 0;
                        row < 7;
                        row++
                    ) {

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

                    pp.addLoadedInstrument(
                        instrument,
                        beats,
                        octaveShift,
                        active,
                        continuation
                    );
                }

                partiturePanels.add(pp);
            }

            in.close();

            JOptionPane.showMessageDialog(
                parentFrame,
                "Project imported successfully!"
            );
        }

        catch (Exception ex) {

            ex.printStackTrace();

            JOptionPane.showMessageDialog(
                parentFrame,
                "Import error: "
                + ex.getMessage()
            );
        }
    }
}