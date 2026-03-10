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

    static {
        enabled = false;
    }

    public static void save() {
        try {
            Files.deleteIfExists(configFile);

            JsonObject json = new JsonObject();
            json.addProperty("enabled", enabled);

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
            }

            save();
            FastClick.LOGGER.info("Config file loaded successfully.");
        } catch (IOException e) {
            FastClick.LOGGER.error("Failed to load config file", e);
        }
    }
}
