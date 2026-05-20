/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package music.editor;

/**
 *
 * @author desktop
 */
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JScrollBar;
import javax.swing.plaf.basic.BasicScrollBarUI;

public class SteamScrollBarUI extends BasicScrollBarUI {

    // STEAM COLORS
    private final Color steamDark = new Color(27, 40, 56);

    private final Color steamLight = new Color(45, 65, 85);

    private final Color steamBorder = new Color(90, 110, 130);

    @Override
    protected void configureScrollBarColors() {

        thumbColor = steamLight;

        trackColor = steamDark;
    }

    @Override
    protected JButton createDecreaseButton(int orientation) {

        return createInvisibleButton();
    }

    @Override
    protected JButton createIncreaseButton(int orientation) {

        return createInvisibleButton();
    }

    private JButton createInvisibleButton() {

        JButton button = new JButton();

        button.setPreferredSize(new Dimension(0, 0));

        button.setMinimumSize(new Dimension(0, 0));

        button.setMaximumSize(new Dimension(0, 0));

        return button;
    }

    @Override
    protected void paintTrack(
        Graphics g,
        JComponent c,
        java.awt.Rectangle trackBounds
    ) {

        Graphics2D g2 = (Graphics2D) g;

        g2.setColor(trackColor);

        g2.fillRect(
            trackBounds.x,
            trackBounds.y,
            trackBounds.width,
            trackBounds.height
        );
    }

    @Override
    protected void paintThumb(
        Graphics g,
        JComponent c,
        java.awt.Rectangle thumbBounds
    ) {

        if (thumbBounds.isEmpty() || !scrollbar.isEnabled()) {
            return;
        }

        Graphics2D g2 = (Graphics2D) g.create();

        g2.setColor(thumbColor);

        g2.fillRoundRect(
            thumbBounds.x + 2,
            thumbBounds.y + 2,
            thumbBounds.width - 4,
            thumbBounds.height - 4,
            10,
            10
        );

        g2.setColor(steamBorder);

        g2.drawRoundRect(
            thumbBounds.x + 2,
            thumbBounds.y + 2,
            thumbBounds.width - 5,
            thumbBounds.height - 5,
            10,
            10
        );

        g2.dispose();
    }

    @Override
    protected Dimension getMinimumThumbSize() {

        return new Dimension(30, 30);
    }
}
