package ui.panels;

import service.ThemeService;
import ui.ApplicationFrame;
import ui.components.ImageBackgroundPanel;
import ui.components.ImageBackgroundPanel.Treatment;

import javax.swing.*;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class SplashPanel extends ImageBackgroundPanel {

    private final ApplicationFrame frame;

    public SplashPanel(ApplicationFrame frame) {
        super(ThemeService.getInstance().getPublicImage(),
              Treatment.FULL_OVERLAY, 0.68f, Color.BLACK);
        this.frame = frame;
        setLayout(new GridBagLayout());
        buildUI();
    }

    private void buildUI() {
        JPanel content = new JPanel(new GridBagLayout());
        content.setOpaque(false);
        content.setPreferredSize(new Dimension(1100, 420));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx  = 0;
        gbc.fill   = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.CENTER;

        JLabel title = new JLabel("Deep Thought Entertainment Group");
        title.setFont(new Font("SansSerif", Font.PLAIN, 48));
        title.setForeground(ThemeService.colorTextPrimary);
        title.setHorizontalAlignment(SwingConstants.CENTER);

        JLabel tagline = new JLabel(
            "\u201cThe answer is 42. The system is everything else.\u201d");
        tagline.setFont(new Font("SansSerif", Font.ITALIC, 20));
        tagline.setForeground(ThemeService.colorTextMuted);
        tagline.setHorizontalAlignment(SwingConstants.CENTER);

        JPanel cards = new JPanel(new FlowLayout(FlowLayout.CENTER, 24, 0));
        cards.setOpaque(false);
        cards.add(buildEntryCard("\uD83C\uDFE2",
            "Staff Login",  "Internal portal",
            ApplicationFrame.panelStaffLogin));
        cards.add(buildEntryCard("\uD83C\uDF0C",
            "Guest Portal", "Book \u00B7 Play \u00B7 Explore",
            ApplicationFrame.panelGuestLogin));
        cards.add(buildEntryCard("\u2699",
            "System Admin", "Network access",
            ApplicationFrame.panelNetworkAdmin));

        JLabel version = new JLabel(
            "v1.0.0  \u00B7  INFO 5100  \u00B7  Northeastern University");
        version.setFont(new Font("SansSerif", Font.PLAIN, 14));
        version.setForeground(ThemeService.colorTextMuted);
        version.setHorizontalAlignment(SwingConstants.CENTER);

        gbc.gridy = 0; gbc.insets = new Insets(0, 0, 8, 0);
        content.add(title, gbc);

        gbc.gridy = 1; gbc.insets = new Insets(0, 0, 48, 0);
        content.add(tagline, gbc);

        gbc.gridy = 2; gbc.insets = new Insets(0, 0, 48, 0);
        content.add(cards, gbc);

        gbc.gridy = 3; gbc.insets = new Insets(0, 0, 0, 0);
        content.add(version, gbc);

        add(content);
    }

    private JPanel buildEntryCard(String icon, String label,
                                   String subtitle, String targetPanel) {
        JPanel card = new JPanel(new GridBagLayout());
        card.setPreferredSize(new Dimension(220, 200));
        card.setBackground(ThemeService.colorBgTertiary);
        card.setBorder(BorderFactory.createLineBorder(
            ThemeService.colorBorder, 1));
        card.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx  = 0;
        gbc.anchor = GridBagConstraints.CENTER;

        JLabel iconLabel = new JLabel(icon);
        iconLabel.setFont(new Font("SansSerif", Font.PLAIN, 42));
        iconLabel.setForeground(ThemeService.colorTextPrimary);

        JLabel nameLabel = new JLabel(label);
        nameLabel.setFont(new Font("SansSerif", Font.PLAIN, 18));
        nameLabel.setForeground(ThemeService.colorTextPrimary);

        JLabel subLabel = new JLabel(subtitle);
        subLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
        subLabel.setForeground(ThemeService.colorTextMuted);

        gbc.gridy = 0; gbc.insets = new Insets(16, 16, 8, 16);
        card.add(iconLabel, gbc);
        gbc.gridy = 1; gbc.insets = new Insets(0, 16, 4, 16);
        card.add(nameLabel, gbc);
        gbc.gridy = 2; gbc.insets = new Insets(0, 16, 16, 16);
        card.add(subLabel, gbc);

        card.addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) {
                card.setBackground(ThemeService.colorBgSecondary);
                card.setBorder(BorderFactory.createLineBorder(
                    ThemeService.colorAccentPurple, 1));
            }
            @Override public void mouseExited(MouseEvent e) {
                card.setBackground(ThemeService.colorBgTertiary);
                card.setBorder(BorderFactory.createLineBorder(
                    ThemeService.colorBorder, 1));
            }
            @Override public void mouseClicked(MouseEvent e) {
                frame.showPanel(targetPanel);
            }
        });

        return card;
    }
}
