/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package music.editor;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.LayoutManager;
import java.awt.Rectangle;
import java.awt.RenderingHints;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JList;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.ListCellRenderer;
import javax.swing.plaf.basic.BasicComboBoxUI;
import javax.swing.plaf.basic.BasicComboPopup;

public class SteamComboBoxUI extends BasicComboBoxUI {

    // STEAM COLORS
    private final Color steamDark = new Color(27, 40, 56);

    private final Color steamLight = new Color(45, 65, 85);

    private final Color steamBorder = new Color(90, 110, 130);

    private final Color creamText = new Color(245, 235, 210);

    @Override
    protected JButton createArrowButton() {

        JButton button = new JButton() {

            @Override
            protected void paintComponent(Graphics g) {

                Graphics2D g2 = (Graphics2D) g;

                g2.setRenderingHint(
                    RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON
                );

                // BACKGROUND
                g2.setColor(steamLight);

                g2.fillRect(
                    0,
                    0,
                    getWidth(),
                    getHeight()
                );

                // BORDER
                g2.setColor(steamBorder);

                g2.drawRect(
                    0,
                    0,
                    getWidth() - 1,
                    getHeight() - 1
                );

                // ARROW
                g2.setColor(creamText);

                int centerX = getWidth() / 2;

                int centerY = getHeight() / 2;

                int[] xPoints = {
                    centerX - 4,
                    centerX + 4,
                    centerX
                };

                int[] yPoints = {
                    centerY - 2,
                    centerY - 2,
                    centerY + 3
                };

                g2.fillPolygon(
                    xPoints,
                    yPoints,
                    3
                );
            }
        };

        button.setBorder(
            BorderFactory.createEmptyBorder()
        );

        button.setFocusPainted(false);

        button.setContentAreaFilled(false);

        return button;
    }

    @Override
    protected LayoutManager createLayoutManager() {

        return new ComboBoxLayoutManager() {

            @Override
            public void layoutContainer(Container parent) {

                JComboBox combo =
                    (JComboBox) parent;

                int width =
                    combo.getWidth();

                int height =
                    combo.getHeight();

                // FIXED ARROW WIDTH
                int buttonWidth = 22;

                // POSITION ARROW BUTTON
                arrowButton.setBounds(
                    width - buttonWidth,
                    0,
                    buttonWidth,
                    height
                );

                // POSITION TEXT AREA
                if (editor != null) {

                    Rectangle r =
                        rectangleForCurrentValue();

                    editor.setBounds(
                        r.x,
                        r.y,
                        r.width,
                        r.height
                    );
                }
            }
        };
    }

    @Override
    public void paintCurrentValueBackground(
        Graphics g,
        Rectangle bounds,
        boolean hasFocus
    ) {

        g.setColor(steamLight);

        g.fillRect(
            bounds.x,
            bounds.y,
            bounds.width,
            bounds.height
        );
    }

@Override
public void installUI(javax.swing.JComponent c) {

    super.installUI(c);

    JComboBox combo =
        (JComboBox) c;

    combo.setBackground(steamLight);

    combo.setForeground(creamText);

    combo.setBorder(
        BorderFactory.createLineBorder(steamBorder)
    );

    combo.setFocusable(false);

    combo.setRenderer(createRenderer());

    // CUSTOM STEAM SCROLLBAR
    Object child =
        combo.getAccessibleContext()
             .getAccessibleChild(0);

    if (child instanceof BasicComboPopup popup) {

        JScrollPane scrollPane =
            (JScrollPane) popup.getComponent(0);

        scrollPane.getVerticalScrollBar()
                  .setUI(new SteamScrollBarUI());

        scrollPane.setBorder(
            BorderFactory.createLineBorder(steamBorder)
        );

        scrollPane.getViewport()
                  .setBackground(steamDark);
    }
}

    @Override
    protected ListCellRenderer createRenderer() {

        return new javax.swing.DefaultListCellRenderer() {

            @Override
            public Component getListCellRendererComponent(
                JList list,
                Object value,
                int index,
                boolean isSelected,
                boolean cellHasFocus
            ) {

                super.getListCellRendererComponent(
                    list,
                    value,
                    index,
                    isSelected,
                    cellHasFocus
                );

                setOpaque(true);

                if (isSelected) {

                    setBackground(steamBorder);

                    setForeground(creamText);
                }

                else {

                    setBackground(steamLight);

                    setForeground(creamText);
                }

                return this;
            }
        };
    }
}
