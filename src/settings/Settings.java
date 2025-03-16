package settings;

import exceptions.SettingsException;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class Settings {
    private Properties properties;

    public Settings(String filename) {
        if (filename == null) {
            throw new SettingsException("Filename cannot be null");
        }
        properties = new Properties();
        try (FileInputStream fileInputStream = new FileInputStream(filename)) {
            properties.load(fileInputStream);
        } catch (IOException e) {
            throw new SettingsException("Could not load properties file: " + e.getMessage());
        }
    }

    public String getProperty(String key) {
        return properties.getProperty(key);
    }

    public void setProperty(String key, String value) {
        if (key == null) {
            throw new SettingsException("Key cannot be null");
        }
        if (value == null) {
            throw new SettingsException("Value cannot be null");
        }
        properties.setProperty(key, value);
    }
}
