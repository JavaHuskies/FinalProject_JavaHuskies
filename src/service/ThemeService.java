package service;

import ui.components.ImageBackgroundPanel;
import ui.components.ImageBackgroundPanel.Treatment;

import javax.imageio.ImageIO;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Singleton service managing JWST imagery and UI theming.
 *
 * Enterprise-to-image mapping:
 *   MAGRATHEA_STUDIOS          -> pillars_of_creation.png
 *   STARSHIP_TITANIC_LEISURE   -> cartwheel_galaxy.png
 *   GALACTIC_BROADCASTING      -> stephans_quintet.png
 *   SIRIUS_CYBERNETICS         -> southern_ring_nebula.png
 *
 * Public pool (splash, login, guest portal) -> random each session from all 6 images.
 *
 * Usage:
 *   ThemeService ts = ThemeService.getInstance();
 *   BufferedImage img = ts.getEnterpriseImage("MAGRATHEA_STUDIOS");
 *   ImageBackgroundPanel panel = ts.createPanel("sidebar", "MAGRATHEA_STUDIOS");
 */
public class ThemeService {

    private static final Logger LOG = Logger.getLogger(ThemeService.class.getName());
    private static ThemeService instance;

    // Enterprise ID constants — match enterprise_id values in database
    public static final String MAGRATHEA_STUDIOS         = "MAGRATHEA_STUDIOS";
    public static final String STARSHIP_TITANIC_LEISURE  = "STARSHIP_TITANIC_LEISURE";
    public static final String GALACTIC_BROADCASTING     = "GALACTIC_BROADCASTING";
    public static final String SIRIUS_CYBERNETICS        = "SIRIUS_CYBERNETICS";

    // Image resource paths — must match files in src/resources/images/
    private static final String RES_PATH = "/resources/images/";
    private static final String IMG_PILLARS    = "pillars_of_creation.png";
    private static final String IMG_CARTWHEEL  = "cartwheel_galaxy.png";
    private static final String IMG_QUINTET    = "stephans_quintet.png";
    private static final String IMG_RING       = "southern_ring_nebula.png";
    private static final String IMG_DEEP_FIELD = "deep_field.png";
    private static final String IMG_CARINA     = "carina_nebula.png";

    // Cached image map — loaded once at startup
    private final Map<String, BufferedImage> imageCache = new HashMap<>();

    // Fixed enterprise -> image filename mapping
    private final Map<String, String> enterpriseImageMap = Map.of(
        MAGRATHEA_STUDIOS,        IMG_PILLARS,
        STARSHIP_TITANIC_LEISURE, IMG_CARTWHEEL,
        GALACTIC_BROADCASTING,    IMG_QUINTET,
        SIRIUS_CYBERNETICS,       IMG_RING
    );

    // Full pool for public/guest/splash randomization
    private final List<String> publicPool = Arrays.asList(
        IMG_PILLARS, IMG_CARTWHEEL, IMG_QUINTET,
        IMG_RING, IMG_DEEP_FIELD, IMG_CARINA
    );

    // Session image for public-facing panels — selected once at startup
    private BufferedImage sessionPublicImage;

    // -------------------------------------------------------------------------
    // Singleton
    // -------------------------------------------------------------------------

    private ThemeService() {
        loadAllImages();
        randomizePublicImage();
    }

    public static synchronized ThemeService getInstance() {
        if (instance == null) {
            instance = new ThemeService();
        }
        return instance;
    }

    // -------------------------------------------------------------------------
    // Image access
    // -------------------------------------------------------------------------

    /**
     * Returns the designated JWST image for the given enterprise ID.
     * Falls back to session public image if enterprise ID is unrecognised.
     */
    public BufferedImage getEnterpriseImage(String enterpriseId) {
        String filename = enterpriseImageMap.get(enterpriseId);
        if (filename == null) {
            LOG.warning("Unknown enterpriseId: " + enterpriseId + " — using public image");
            return sessionPublicImage;
        }
        BufferedImage img = imageCache.get(filename);
        return img != null ? img : sessionPublicImage;
    }

    /**
     * Returns the session-random image for splash, login, and guest portal.
     * Consistent for the lifetime of the application session.
     */
    public BufferedImage getPublicImage() {
        return sessionPublicImage;
    }

    /**
     * Re-randomizes the public session image.
     * Call only at startup — not during a session.
     */
    public void randomizePublicImage() {
        List<String> shuffled = new ArrayList<>(publicPool);
        Collections.shuffle(shuffled);
        BufferedImage img = imageCache.get(shuffled.get(0));
        sessionPublicImage = img != null ? img : null;
    }

    // -------------------------------------------------------------------------
    // Panel factory methods
    // -------------------------------------------------------------------------

    /**
     * Creates a themed ImageBackgroundPanel for the given context and enterprise.
     *
     * context values:
     *   "splash"    -> public image, FULL_OVERLAY, 68% opacity
     *   "login"     -> public image, FULL_OVERLAY, 72% opacity
     *   "sidebar"   -> enterprise image, FADE_RIGHT, 0% (gradient handles it)
     *   "header"    -> enterprise image, STRIP
     *   "guest"     -> public image, FULL_OVERLAY, 65% opacity
     *   "dashboard" -> enterprise image, DIMMED, 88% opacity
     *   default     -> enterprise image, FULL_OVERLAY, 70% opacity
     */
    public ImageBackgroundPanel createPanel(String context, String enterpriseId) {
        boolean usePublic = isPublicContext(context);
        BufferedImage img = usePublic
            ? getPublicImage()
            : getEnterpriseImage(enterpriseId);

        return switch (context) {
            case "splash"    -> new ImageBackgroundPanel(img, Treatment.FULL_OVERLAY,
                                    0.68f, Color.BLACK);
            case "login"     -> new ImageBackgroundPanel(img, Treatment.FULL_OVERLAY,
                                    0.72f, Color.BLACK);
            case "sidebar"   -> new ImageBackgroundPanel(img, Treatment.FADE_RIGHT,
                                    0.0f,  Color.BLACK);
            case "header"    -> new ImageBackgroundPanel(img, Treatment.STRIP,
                                    0.0f,  Color.BLACK);
            case "guest"     -> new ImageBackgroundPanel(img, Treatment.FULL_OVERLAY,
                                    0.65f, Color.BLACK);
            case "dashboard" -> new ImageBackgroundPanel(img, Treatment.DIMMED,
                                    0.88f, new Color(8, 8, 26));
            default          -> new ImageBackgroundPanel(img, Treatment.FULL_OVERLAY,
                                    0.70f, Color.BLACK);
        };
    }

    /** Convenience — creates a panel without enterprise context (public contexts) */
    public ImageBackgroundPanel createPanel(String context) {
        return createPanel(context, null);
    }

    // -------------------------------------------------------------------------
    // Color palette — Dark H2G2 UI theme
    // -------------------------------------------------------------------------

    public static final Color COLOR_BG_PRIMARY    = new Color(10,  10,  26);
    public static final Color COLOR_BG_SECONDARY  = new Color(18,  18,  42);
    public static final Color COLOR_BG_TERTIARY   = new Color(26,  26,  58);
    public static final Color COLOR_ACCENT_PURPLE  = new Color(123, 111, 196);
    public static final Color COLOR_ACCENT_TEAL    = new Color(93,  202, 165);
    public static final Color COLOR_TEXT_PRIMARY   = new Color(200, 184, 248);
    public static final Color COLOR_TEXT_SECONDARY = new Color(144, 144, 192);
    public static final Color COLOR_TEXT_MUTED     = new Color(85,  85,  100);
    public static final Color COLOR_BORDER         = new Color(42,  42,  90);
    public static final Color COLOR_SIDEBAR_ACTIVE = new Color(26,  26,  58);

    /** Returns the accent color for the given enterprise */
    public static Color getEnterpriseAccent(String enterpriseId) {
        return switch (enterpriseId) {
            case MAGRATHEA_STUDIOS        -> new Color(160, 100, 220); // Purple
            case STARSHIP_TITANIC_LEISURE -> new Color(220, 120, 80);  // Coral
            case GALACTIC_BROADCASTING    -> new Color(80,  160, 220); // Blue
            case SIRIUS_CYBERNETICS       -> new Color(80,  200, 150); // Teal
            default                       -> COLOR_ACCENT_PURPLE;
        };
    }

    // -------------------------------------------------------------------------
    // Internal helpers
    // -------------------------------------------------------------------------

    private void loadAllImages() {
        String[] allImages = {
            IMG_PILLARS, IMG_CARTWHEEL, IMG_QUINTET,
            IMG_RING, IMG_DEEP_FIELD, IMG_CARINA
        };
        for (String filename : allImages) {
            try (InputStream is = getClass().getResourceAsStream(RES_PATH + filename)) {
                if (is == null) {
                    LOG.warning("Image not found: " + RES_PATH + filename
                        + " — place images in src/resources/images/");
                    continue;
                }
                BufferedImage img = ImageIO.read(is);
                if (img != null) {
                    imageCache.put(filename, img);
                    LOG.info("Loaded: " + filename
                        + " (" + img.getWidth() + "x" + img.getHeight() + ")");
                }
            } catch (IOException e) {
                LOG.log(Level.WARNING, "Failed to load image: " + filename, e);
            }
        }
        LOG.info("ThemeService: loaded " + imageCache.size() + "/" + allImages.length + " images");
    }

    private boolean isPublicContext(String context) {
        return context != null && (
            context.equals("splash") ||
            context.equals("login")  ||
            context.equals("guest")
        );
    }
}