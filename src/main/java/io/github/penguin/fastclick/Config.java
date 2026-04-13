package io.github.penguin.fastclick;

import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

public class Config {
    public static final Path configFile = FabricLoader.getInstance().getConfigDir().resolve("fastclick.json");
    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private static final int CONFIG_VERSION = 1;

    public static boolean enabled;
    public static int delay;

    static {
        setDefault();
    }

    public static void setDefault() {
        enabled = false;
        delay = 8;
    }

    public static void save() {
        try {
            Files.deleteIfExists(configFile);

            JsonObject json = new JsonObject();
            json.addProperty("enabled", enabled);
            json.addProperty("delay", delay);
            json.addProperty("configVersion", CONFIG_VERSION);

            Files.writeString(configFile, gson.toJson(json));
        } catch (IOException e) {
            FastClick.LOGGER.error("Failed to save config file", e);
        }
    }

    public static void load() {
        try {
            if(!Files.exists(configFile)) {
                Files.createFile(configFile);
                Files.writeString(configFile, "{}");
            }
            JsonObject json = gson.fromJson(Files.readString(configFile), JsonObject.class);

            if (json != null && json.has("configVersion") && json.get("configVersion").getAsInt() == CONFIG_VERSION) {
                if (json.has("enabled")) {
                    enabled = json.getAsJsonPrimitive("enabled").getAsBoolean();
                }
                if (json.has("delay")) {
                    delay = json.getAsJsonPrimitive("delay").getAsInt();
                }
                if (json.has("configVersion")) {
                    int version = json.getAsJsonPrimitive("configVersion").getAsInt();
                    if (version < CONFIG_VERSION) {
                        FastClick.LOGGER.warn("Migrating config from version {} to {}", version, CONFIG_VERSION);
                        // Perform any necessary migration steps here
                    }
                    else if (version > CONFIG_VERSION) {
                        FastClick.LOGGER.warn("Config file version {} is newer than expected {}. Using default", version, CONFIG_VERSION);
                        setDefault();
                    }
                }
            }

            save();
            FastClick.LOGGER.info("Config file loaded successfully.");
        } catch (IOException e) {
            FastClick.LOGGER.error("Failed to load config file", e);
        }
    }
}
