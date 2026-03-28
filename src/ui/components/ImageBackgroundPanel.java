package ui.components;


import javax.swing.*;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;

/**
 * Reusable panel that renders a JWST background image with a configurable
 * overlay treatment. All panels in the application should extend this class
 * rather than JPanel directly to ensure consistent theming.
 *
 * Usage:
 *   ImageBackgroundPanel panel = new ImageBackgroundPanel(
 *       ThemeService.getInstance().getOverlayStyle("splash")
 *   );
 *   panel.setLayout(new BorderLayout());
 *   panel.add(myComponent, BorderLayout.CENTER);
 */
public class ImageBackgroundPanel extends JPanel {

    public enum Treatment {
        FULL_OVERLAY,   // Full image, dark overlay — splash, login
        DIMMED,         // Heavily dimmed subtle texture
        FADE_RIGHT,     // Edge fade left-to-right — sidebar
        STRIP           // Accent strip only — dashboard headers
    }

    private BufferedImage bgImage;
    private Treatment treatment;
    private float overlayOpacity;
    private Color overlayColor;

    // Default: full overlay, 70% opacity black
    public ImageBackgroundPanel() {
        this(null, Treatment.FULL_OVERLAY, 0.70f, new Color(0, 0, 0));
    }

    public ImageBackgroundPanel(BufferedImage image, Treatment treatment,
                                 float overlayOpacity, Color overlayColor) {
        this.bgImage = image;
        this.treatment = treatment;
        this.overlayOpacity = Math.min(1.0f, Math.max(0.0f, overlayOpacity));
        this.overlayColor = overlayColor;
        setOpaque(false);
    }

    /** Swap image at runtime (e.g. enterprise navigation changes sidebar image) */
    public void setBackgroundImage(BufferedImage image) {
        this.bgImage = image;
        repaint();
    }

    public void setTreatment(Treatment treatment) {
        this.treatment = treatment;
        repaint();
    }

    public void setOverlayOpacity(float opacity) {
        this.overlayOpacity = Math.min(1.0f, Math.max(0.0f, opacity));
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (bgImage == null) {
            paintFallback(g);
            return;
        }

        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                             RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING,
                             RenderingHints.VALUE_RENDER_QUALITY);

        int w = getWidth();
        int h = getHeight();

        switch (treatment) {
            case FULL_OVERLAY -> paintFullOverlay(g2d, w, h);
            case DIMMED       -> paintDimmed(g2d, w, h);
            case FADE_RIGHT   -> paintFadeRight(g2d, w, h);
            case STRIP        -> paintStrip(g2d, w, h);
        }

        g2d.dispose();
    }

    /** Full image scaled to fill panel, dark color overlay on top */
    private void paintFullOverlay(Graphics2D g2d, int w, int h) {
        drawScaledImage(g2d, w, h);
        g2d.setColor(new Color(
            overlayColor.getRed(),
            overlayColor.getGreen(),
            overlayColor.getBlue(),
            (int)(overlayOpacity * 255)
        ));
        g2d.fillRect(0, 0, w, h);
    }

    /** Full image but very heavily dimmed — subtle texture effect */
    private void paintDimmed(Graphics2D g2d, int w, int h) {
        drawScaledImage(g2d, w, h);
        g2d.setColor(new Color(8, 8, 26, (int)(overlayOpacity * 255)));
        g2d.fillRect(0, 0, w, h);
    }

    /** Image fades left-to-right into solid dark — for sidebars */
    private void paintFadeRight(Graphics2D g2d, int w, int h) {
        drawScaledImage(g2d, w, h);
        GradientPaint fade = new GradientPaint(
            0, 0, new Color(8, 8, 26, 40),
            w, 0, new Color(8, 8, 26, 245)
        );
        g2d.setPaint(fade);
        g2d.fillRect(0, 0, w, h);
    }

    /** Image rendered only as a top accent strip, rest is solid dark */
    private void paintStrip(Graphics2D g2d, int w, int h) {
        int stripH = Math.min(56, h / 4);

        // Draw image cropped to strip height
        if (bgImage != null) {
            int srcH = (int)((float) bgImage.getHeight() * w / bgImage.getWidth());
            g2d.drawImage(bgImage, 0, 0, w, stripH,
                          0, 0, bgImage.getWidth(),
                          (int)((float) bgImage.getHeight() * stripH / srcH),
                          null);
            // Dim the strip
            g2d.setColor(new Color(0, 0, 0, 120));
            g2d.fillRect(0, 0, w, stripH);
            // Fade strip bottom edge into solid background
            GradientPaint edgeFade = new GradientPaint(
                0, stripH - 12, new Color(0, 0, 0, 0),
                0, stripH,      new Color(10, 10, 26, 255)
            );
            g2d.setPaint(edgeFade);
            g2d.fillRect(0, stripH - 12, w, 12);
        }

        // Solid background below strip
        g2d.setColor(new Color(10, 10, 26));
        g2d.fillRect(0, stripH, w, h - stripH);
    }

    /** Scales image to cover panel maintaining aspect ratio (cover behavior) */
    private void drawScaledImage(Graphics2D g2d, int w, int h) {
        if (bgImage == null) return;
        float imgAspect  = (float) bgImage.getWidth()  / bgImage.getHeight();
        float panelAspect = (float) w / h;

        int drawW, drawH, drawX, drawY;
        if (imgAspect > panelAspect) {
            drawH = h;
            drawW = (int)(h * imgAspect);
        } else {
            drawW = w;
            drawH = (int)(w / imgAspect);
        }
        drawX = (w - drawW) / 2;
        drawY = (h - drawH) / 2;

        g2d.drawImage(bgImage, drawX, drawY, drawW, drawH, null);
    }

    /** Fallback when no image is loaded — solid dark background */
    private void paintFallback(Graphics g) {
        g.setColor(new Color(10, 10, 26));
        g.fillRect(0, 0, getWidth(), getHeight());
    }
}