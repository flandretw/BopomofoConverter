package flandretw.bopomofo.converter.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.ChatFormatting;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;

public class BopomofoConfig {
    private static final Path CONFIG_PATH = FabricLoader.getInstance().getConfigDir()
            .resolve("bopomofo-converter.json");
    private static final Path LEGACY_CONFIG_PATH = FabricLoader.getInstance().getConfigDir()
            .resolve("bopomofo-translator.json");
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    // Default values as requested: base game default color (YELLOW and WHITE are
    // common, we will use default Formatting behavior or WHITE), no bold, no
    // italic.
    public ChatFormatting textColor = ChatFormatting.WHITE;
    public boolean bold = false;
    public boolean italic = false;
    public boolean underline = false;

    private static BopomofoConfig instance;

    public static BopomofoConfig getInstance() {
        if (instance == null) {
            instance = load();
        }
        return instance;
    }

    public static BopomofoConfig load() {
        Path targetPath = Files.exists(CONFIG_PATH) ? CONFIG_PATH : (Files.exists(LEGACY_CONFIG_PATH) ? LEGACY_CONFIG_PATH : null);
        if (targetPath != null) {
            try (Reader reader = Files.newBufferedReader(targetPath)) {
                BopomofoConfig loaded = GSON.fromJson(reader, BopomofoConfig.class);
                if (loaded != null) {
                    if (loaded.textColor == null) loaded.textColor = ChatFormatting.WHITE;
                    return loaded;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        BopomofoConfig config = new BopomofoConfig();
        config.save();
        return config;
    }

    public void save() {
        try {
            Files.createDirectories(CONFIG_PATH.getParent());
            try (Writer writer = Files.newBufferedWriter(CONFIG_PATH)) {
                GSON.toJson(this, writer);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
