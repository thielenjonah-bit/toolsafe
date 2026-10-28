package dev.toolsafe;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import net.fabricmc.loader.api.FabricLoader;

import java.nio.file.Files;
import java.nio.file.Path;

public class ModConfig {
    private static final Path FILE = FabricLoader.getInstance().getConfigDir().resolve("toolsafe.json");
    private static final Gson GSON = new Gson();

    public static boolean enabled = true;

    public static void load() {
        try {
            if (Files.exists(FILE)) {
                JsonObject o = GSON.fromJson(Files.readString(FILE), JsonObject.class);
                if (o != null && o.has("enabled")) enabled = o.get("enabled").getAsBoolean();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void save() {
        try {
            JsonObject o = new JsonObject();
            o.addProperty("enabled", enabled);
            Files.writeString(FILE, GSON.toJson(o));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
