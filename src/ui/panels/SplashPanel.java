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

/**
 * Hand-coded splash screen — entry point for all user types.
 * Full JWST background with dark overlay.
 * Three entry cards route to staff login, guest portal, system admin.
 *
 * Hand-coded (not GUI Builder) to support ImageBackgroundPanel painting.
 */
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
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.anchor = GridBagConstraints.CENTER;

        // Title
        JLabel title = new JLabel("Deep Thought Entertainment Group");
        title.setFont(new Font("SansSerif", Font.PLAIN, 32));
        title.setForeground(ThemeService.COLOR_TEXT_PRIMARY);
        title.setHorizontalAlignment(SwingConstants.CENTER);

        // Tagline
        JLabel tagline = new JLabel(
            "\u201cThe answer is 42. The system is everything else.\u201d");
        tagline.setFont(new Font("SansSerif", Font.ITALIC, 14));
        tagline.setForeground(ThemeService.COLOR_TEXT_MUTED);
        tagline.setHorizontalAlignment(SwingConstants.CENTER);

        // Entry cards
        JPanel cards = new JPanel(new FlowLayout(FlowLayout.CENTER, 24, 0));
        cards.setOpaque(false);
        cards.add(buildEntryCard("\uD83C\uDFE2",
            "Staff Login",    "Internal portal",
            ApplicationFrame.PANEL_STAFF_LOGIN));
        cards.add(buildEntryCard("\uD83C\uDF0C",
            "Guest Portal",   "Book \u00B7 Play \u00B7 Explore",
            ApplicationFrame.PANEL_GUEST_LOGIN));
        cards.add(buildEntryCard("\u2699\uFE0F",
            "System Admin",   "Network access",
            ApplicationFrame.PANEL_NETWORK_ADMIN));

        // Version
        JLabel version = new JLabel(
            "v1.0.0  \u00B7  INFO 5100  \u00B7  Northeastern University");
        version.setFont(new Font("SansSerif", Font.PLAIN, 11));
        version.setForeground(ThemeService.COLOR_TEXT_MUTED);
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
        card.setPreferredSize(new Dimension(160, 140));
        card.setBackground(ThemeService.COLOR_BG_TERTIARY);
        card.setBorder(BorderFactory.createLineBorder(
            ThemeService.COLOR_BORDER, 1));
        card.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.anchor = GridBagConstraints.CENTER;

        JLabel iconLabel = new JLabel(icon);
        iconLabel.setFont(new Font("SansSerif", Font.PLAIN, 28));

        JLabel nameLabel = new JLabel(label);
        nameLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
        nameLabel.setForeground(ThemeService.COLOR_TEXT_PRIMARY);

        JLabel subLabel = new JLabel(subtitle);
        subLabel.setFont(new Font("SansSerif", Font.PLAIN, 11));
        subLabel.setForeground(ThemeService.COLOR_TEXT_MUTED);

        gbc.gridy = 0; gbc.insets = new Insets(16, 16, 8, 16);
        card.add(iconLabel, gbc);
        gbc.gridy = 1; gbc.insets = new Insets(0, 16, 4, 16);
        card.add(nameLabel, gbc);
        gbc.gridy = 2; gbc.insets = new Insets(0, 16, 16, 16);
        card.add(subLabel, gbc);

        // Hover + click
        card.addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) {
                card.setBackground(ThemeService.COLOR_BG_SECONDARY);
                card.setBorder(BorderFactory.createLineBorder(
                    ThemeService.COLOR_ACCENT_PURPLE, 1));
            }
            @Override public void mouseExited(MouseEvent e) {
                card.setBackground(ThemeService.COLOR_BG_TERTIARY);
                card.setBorder(BorderFactory.createLineBorder(
                    ThemeService.COLOR_BORDER, 1));
            }
            @Override public void mouseClicked(MouseEvent e) {
                frame.showPanel(targetPanel);
            }
        });

        return card;
    }
}