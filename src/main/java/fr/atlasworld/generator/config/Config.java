package fr.atlasworld.generator.config;

import com.google.gson.GsonBuilder;
import com.google.gson.annotations.SerializedName;
import fr.atlasworld.generator.file.FileManager;
import fr.atlasworld.generator.file.GsonFileLoader;

import java.io.File;

public record Config (@SerializedName("api_token") String apiToken, @SerializedName("atlasnet_server_id") String AtlasNetworkServerId) {
    private static final String CONFIG_FILE = "config.json";

    private static Config config;

    private static Config getConfig() {
        if (config == null) {
            GsonFileLoader<Config> loader = new GsonFileLoader<>(FileManager.getWorkingDirectoryFile(CONFIG_FILE),
                    new GsonBuilder().setPrettyPrinting().create(), Config.class);

            if (!loader.fileExists()) {
                loader.save(new Config("", ""));
            }

            config = loader.load();
        }

        return config;
    }
}
