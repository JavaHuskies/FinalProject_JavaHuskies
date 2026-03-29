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
 *   magratheaStudios        -> pillars_of_creation.png
 *   starshipTitanicLeisure  -> cartwheel_galaxy.png
 *   galacticBroadcasting    -> stephans_quintet.png
 *   siriusCybernetics       -> southern_ring_nebula.png
 *
 * Public pool (splash, login, guest portal) -> random each session from all 6 images.
 *
 * Usage:
 *   ThemeService ts = ThemeService.getInstance();
 *   BufferedImage img = ts.getEnterpriseImage("magratheaStudios");
 *   ImageBackgroundPanel panel = ts.createPanel("sidebar", "magratheaStudios");
 */
public class ThemeService {

    private static final Logger log = Logger.getLogger(ThemeService.class.getName());
    private static ThemeService instance;

    // Enterprise ID constants — match enterprise_id values in database
    public static final String magratheaStudios        = "magratheaStudios";
    public static final String starshipTitanicLeisure  = "starshipTitanicLeisure";
    public static final String galacticBroadcasting    = "galacticBroadcasting";
    public static final String siriusCybernetics       = "siriusCybernetics";

    // Image resource paths — must match files in src/resources/images/
    private static final String resPath       = "/resources/images/";
    private static final String imgPillars    = "pillars_of_creation.png";
    private static final String imgCartwheel  = "cartwheel_galaxy.png";
    private static final String imgQuintet    = "stephans_quintet.png";
    private static final String imgRing       = "southern_ring_nebula.png";
    private static final String imgDeepField  = "deep_field.png";
    private static final String imgCarina     = "carina_nebula.png";

    // Cached image map — loaded once at startup
    private final Map<String, BufferedImage> imageCache = new HashMap<>();

    // Fixed enterprise -> image filename mapping
    private final Map<String, String> enterpriseImageMap = Map.of(
        magratheaStudios,       imgPillars,
        starshipTitanicLeisure, imgCartwheel,
        galacticBroadcasting,   imgQuintet,
        siriusCybernetics,      imgRing
    );

    // Full pool for public/guest/splash randomization
    private final List<String> publicPool = Arrays.asList(
        imgPillars, imgCartwheel, imgQuintet,
        imgRing, imgDeepField, imgCarina
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
     * Falls back to the session public image if the enterprise ID is unrecognised.
     *
     * @param enterpriseId one of the ThemeService enterprise ID constants
     * @return the mapped BufferedImage, or the session public image as fallback
     */
    public BufferedImage getEnterpriseImage(String enterpriseId) {
        String filename = enterpriseImageMap.get(enterpriseId);
        if (filename == null) {
            log.warning("Unknown enterpriseId: " + enterpriseId + " — using public image");
            return sessionPublicImage;
        }
        BufferedImage img = imageCache.get(filename);
        return img != null ? img : sessionPublicImage;
    }

    /**
     * Returns the session-random image for splash, login, and guest portal.
     * Consistent for the lifetime of the application session.
     *
     * @return the session public BufferedImage selected at startup
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
     * Public contexts (splash, login, guest) use the session public image.
     * Enterprise contexts use the mapped JWST image for the given enterprise.
     *
     * context values:
     *   "splash"    -> public image, FULL_OVERLAY, 68% opacity
     *   "login"     -> public image, FULL_OVERLAY, 72% opacity
     *   "sidebar"   -> enterprise image, FADE_RIGHT, 0% (gradient handles it)
     *   "header"    -> enterprise image, STRIP
     *   "guest"     -> public image, FULL_OVERLAY, 65% opacity
     *   "dashboard" -> enterprise image, DIMMED, 88% opacity
     *   default     -> enterprise image, FULL_OVERLAY, 70% opacity
     * 
     * @param enterpriseId one of the ThemeService enterprise ID constants,
     *                     or null for public contexts
     * @return configured ImageBackgroundPanel ready to add to a layout
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

    /**
    * Convenience overload — creates a panel without enterprise context.
    * Intended for public contexts (splash, login, guest) where no enterprise is active.
    *
    * @param context one of: "splash", "login", or "guest"
    * @return configured ImageBackgroundPanel using the session public image
    */
    public ImageBackgroundPanel createPanel(String context) {
        return createPanel(context, null);
    }

    // -------------------------------------------------------------------------
    // Color palette — Dark H2G2 UI theme
    // -------------------------------------------------------------------------

    public static final Color colorBgPrimary     = new Color(10,  10,  26);
    public static final Color colorBgSecondary   = new Color(18,  18,  42);
    public static final Color colorBgTertiary    = new Color(26,  26,  58);
    public static final Color colorAccentPurple  = new Color(123, 111, 196);
    public static final Color colorAccentTeal    = new Color(93,  202, 165);
    public static final Color colorTextPrimary   = new Color(200, 184, 248);
    public static final Color colorTextSecondary = new Color(144, 144, 192);
    public static final Color colorTextMuted     = new Color(85,  85,  100);
    public static final Color colorBorder        = new Color(42,  42,  90);
    public static final Color colorSidebarActive = new Color(26,  26,  58);

    /**
     * Returns the UI accent color mapped to the given enterprise.
     * Falls back to colorAccentPurple for unrecognised IDs.
     *
     * @param enterpriseId one of the ThemeService enterprise ID constants
     * @return the enterprise accent Color
     */
    public static Color getEnterpriseAccent(String enterpriseId) {
        return switch (enterpriseId) {
            case magratheaStudios       -> new Color(160, 100, 220); // purple
            case starshipTitanicLeisure -> new Color(220, 120, 80);  // coral
            case galacticBroadcasting   -> new Color(80,  160, 220); // blue
            case siriusCybernetics      -> new Color(80,  200, 150); // teal
            default                     -> colorAccentPurple;
        };
    }

    // -------------------------------------------------------------------------
    // Internal helpers
    // -------------------------------------------------------------------------

    private void loadAllImages() {
        String[] allImages = {
            imgPillars, imgCartwheel, imgQuintet,
            imgRing, imgDeepField, imgCarina
        };
        for (String filename : allImages) {
            try (InputStream is = getClass().getResourceAsStream(resPath + filename)) {
                if (is == null) {
                    log.warning("Image not found: " + resPath + filename
                        + " — place images in src/resources/images/");
                    continue;
                }
                BufferedImage img = ImageIO.read(is);
                if (img != null) {
                    imageCache.put(filename, img);
                    log.info("Loaded: " + filename
                        + " (" + img.getWidth() + "x" + img.getHeight() + ")");
                }
            } catch (IOException e) {
                log.log(Level.WARNING, "Failed to load image: " + filename, e);
            }
        }
        log.info("ThemeService: loaded " + imageCache.size() + "/" + allImages.length + " images");
    }

    private boolean isPublicContext(String context) {
        return context != null && (
            context.equals("splash") ||
            context.equals("login")  ||
            context.equals("guest")
        );
    }
}