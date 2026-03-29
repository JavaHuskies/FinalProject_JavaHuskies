package service;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Singleton service providing access to config.properties.
 * Loaded once at startup — all modules use this instead of
 * reading the file directly.
 *
 * Usage:
 *   String secret = ConfigService.getInstance().get("jwt.secret");
 *   int width = ConfigService.getInstance().getInt("app.window.width", 1280);
 *   boolean seedAlways = ConfigService.getInstance().getBool("db.seed.always", false);
 */
public class ConfigService {

    private static final Logger LOG = Logger.getLogger(ConfigService.class.getName());
    private static final String CONFIG_FILE = "/config.properties";
    private static ConfigService instance;
    private final Properties props = new Properties();
    private boolean loaded = false;

    // -------------------------------------------------------------------------
    // Singleton
    // -------------------------------------------------------------------------

    private ConfigService() {
        load();
    }

    public static synchronized ConfigService getInstance() {
        if (instance == null) {
            instance = new ConfigService();
        }
        return instance;
    }

    // -------------------------------------------------------------------------
    // Property accessors
    // -------------------------------------------------------------------------

    /** Returns the property value or null if not found */
    public String get(String key) {
        return props.getProperty(key);
    }

    /** Returns the property value or the specified default */
    public String get(String key, String defaultValue) {
        return props.getProperty(key, defaultValue);
    }

    /** Returns the property as an int or the specified default */
    public int getInt(String key, int defaultValue) {
        String val = props.getProperty(key);
        if (val == null) return defaultValue;
        try {
            return Integer.parseInt(val.trim());
        } catch (NumberFormatException e) {
            LOG.warning("Invalid int for key '" + key + "': " + val);
            return defaultValue;
        }
    }

    /** Returns the property as a long or the specified default */
    public long getLong(String key, long defaultValue) {
        String val = props.getProperty(key);
        if (val == null) return defaultValue;
        try {
            return Long.parseLong(val.trim());
        } catch (NumberFormatException e) {
            LOG.warning("Invalid long for key '" + key + "': " + val);
            return defaultValue;
        }
    }

    /** Returns the property as a boolean or the specified default */
    public boolean getBool(String key, boolean defaultValue) {
        String val = props.getProperty(key);
        if (val == null) return defaultValue;
        return Boolean.parseBoolean(val.trim());
    }

    /** Returns true if the config file loaded successfully */
    public boolean isLoaded() {
        return loaded;
    }

    /**
     * Validates that all required credentials are present.
     * Call at startup and warn the user if anything is missing.
     */
    public boolean validateRequiredKeys() {
        String[] required = {
            "jwt.secret",
            "smtp.user",
            "smtp.password",
            "claude.api.key",
            "maps.api.key"
        };
        boolean valid = true;
        for (String key : required) {
            String val = props.getProperty(key);
            if (val == null || val.isBlank() || val.startsWith("REPLACE_")) {
                LOG.warning("Missing or unconfigured property: " + key);
                valid = false;
            }
        }
        return valid;
    }

    /** Returns true if ALL external services should use mock responses */
    public boolean isMockServices() {
        return getBool("app.mock.services", false);
    }

    /** Returns true if AI service should use mock responses */
    public boolean isMockAI() {
        return isMockServices() || getBool("app.mock.ai", false);
    }

    /** Returns true if email service should use mock responses */
    public boolean isMockEmail() {
        return isMockServices() || getBool("app.mock.email", false);
    }

    /** Returns true if maps service should use mock responses */
    public boolean isMockMaps() {
        return isMockServices() || getBool("app.mock.maps", false);
    }

    // -------------------------------------------------------------------------
    // Internal
    // -------------------------------------------------------------------------

    private void load() {
        try (InputStream is = getClass().getResourceAsStream(CONFIG_FILE)) {
            if (is == null) {
                LOG.severe("config.properties not found at " + CONFIG_FILE
                    + " — copy config.properties.template and fill in your values");
                return;
            }
            props.load(is);
            loaded = true;
            LOG.info("ConfigService: loaded " + props.size() + " properties");
        } catch (IOException e) {
            LOG.log(Level.SEVERE, "Failed to load config.properties", e);
        }
    }
}