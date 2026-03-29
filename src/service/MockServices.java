package service;

/**
 * Mock implementations of AI, email, and maps services.
 * Activated when app.mock.ai / app.mock.email / app.mock.maps = true
 * in config.properties. Returns realistic hardcoded responses so all
 * modules can build and test without live API keys.
 *
 * Usage is transparent — AIService, NotificationService, and MapService
 * check ConfigService at call time and delegate here automatically.
 * No changes needed in calling code.
 */
public class MockServices {

    // -------------------------------------------------------------------------
    // Mock AI responses
    // -------------------------------------------------------------------------

    /** Returns a H2G2-style Guide entry for any entity name */
    public static String generateGuideEntry(String entityName) {
        return String.format(
            "DEEP THOUGHT GUIDE ENTRY: %s\n\n" +
            "Widely regarded as one of the more remarkable institutions in the " +
            "known universe, %s has been the subject of considerable debate among " +
            "philosophers, economists, and beings who simply have nothing better to do " +
            "on a Thursday afternoon. Its significance is difficult to overstate, " +
            "though many have tried, usually without success.\n\n" +
            "SEE ALSO: Vogon poetry, the Total Perspective Vortex, and other " +
            "things best avoided at parties.\n\n" +
            "RELIABILITY RATING: Mostly Harmless.",
            entityName.toUpperCase(), entityName
        );
    }

    /** Returns a mock report summary */
    public static String summarizeReport(String reportTitle, String scope) {
        return String.format(
            "Executive Summary — %s\n\n" +
            "Analysis of %s data indicates performance is broadly consistent " +
            "with expectations, which is to say, somewhat better than catastrophic " +
            "and marginally worse than ideal. Key metrics trend positively across " +
            "most dimensions, with isolated anomalies noted in sectors that frankly " +
            "should have known better.\n\n" +
            "Recommendation: Continue operations. Avoid the Vogon sector.",
            reportTitle, scope
        );
    }

    /** Returns a mock work request resolution suggestion */
    public static String suggestResolution(String requestTitle, String requestType) {
        return String.format(
            "Resolution Suggestion for: %s\n\n" +
            "After careful analysis of this %s request, the Guide recommends " +
            "the following course of action: assign to the most competent available " +
            "party, set a reasonable deadline, and under no circumstances panic. " +
            "Cross-enterprise coordination should proceed via the standard work " +
            "request protocol. Expected resolution time: 3-5 business days, " +
            "or approximately 0.0000000042 galactic standard units.",
            requestTitle, requestType
        );
    }

    // -------------------------------------------------------------------------
    // Mock email responses
    // -------------------------------------------------------------------------

    /** Simulates sending an email — logs to console instead */
    public static boolean sendEmail(String to, String subject, String body) {
        System.out.println("\n[MOCK EMAIL]");
        System.out.println("  To:      " + to);
        System.out.println("  Subject: " + subject);
        System.out.println("  Body:    " + body.substring(0, Math.min(80, body.length())) + "...");
        System.out.println("[/MOCK EMAIL]\n");
        return true;
    }

    /** Returns a mock verification token for guest registration */
    public static String generateVerificationToken() {
        return "MOCK-VERIFY-42-" + System.currentTimeMillis();
    }

    /** Simulates sending a verification email */
    public static boolean sendVerificationEmail(String to, String token) {
        return sendEmail(
            to,
            "Verify your Deep Thought Entertainment Group account",
            "Your verification token is: " + token +
            "\n\nClick here to verify: http://localhost/verify?token=" + token
        );
    }

    /** Simulates sending a booking confirmation */
    public static boolean sendBookingConfirmation(String to, String confirmationCode,
                                                    String orgName, String date) {
        return sendEmail(
            to,
            "Booking Confirmed — " + orgName,
            "Your booking at " + orgName + " on " + date +
            " is confirmed.\nConfirmation code: " + confirmationCode
        );
    }

    /** Simulates sending a work request notification */
    public static boolean sendWorkRequestNotification(String to, String requestTitle,
                                                        String status) {
        return sendEmail(
            to,
            "Work Request Update: " + requestTitle,
            "Your work request '" + requestTitle + "' has been updated to: " + status
        );
    }

    // -------------------------------------------------------------------------
    // Mock maps responses
    // -------------------------------------------------------------------------

    /** Returns a mock static map URL for park layouts */
    public static String getParkMapUrl(String orgName) {
        return "https://via.placeholder.com/800x600/0d0d1a/c8b8f8?text=" +
               orgName.replace(" ", "+") + "+Park+Map+(Mock)";
    }

    /** Returns mock attraction coordinates */
    public static double[] getAttractionCoordinates(String attractionName) {
        // Returns lat/lng near Orlando as a realistic placeholder
        return new double[]{28.3772 + (Math.random() * 0.02),
                           -81.5707 + (Math.random() * 0.02)};
    }

    /** Returns a mock guest path description */
    public static String renderGuestPath(String[] attractions) {
        StringBuilder sb = new StringBuilder("Suggested guest path (mock):\n");
        for (int i = 0; i < attractions.length; i++) {
            sb.append("  ").append(i + 1).append(". ").append(attractions[i]);
            if (i < attractions.length - 1) sb.append(" → ");
            sb.append("\n");
        }
        sb.append("Estimated walk time: 42 minutes.");
        return sb.toString();
    }
}