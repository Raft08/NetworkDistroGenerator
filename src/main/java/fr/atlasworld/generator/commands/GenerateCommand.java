package fr.atlasworld.generator.commands;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import fr.atlasworld.generator.config.ServerConfigFile;
import fr.atlasworld.generator.file.FileManager;
import fr.atlasworld.generator.file.GsonFileLoader;
import fr.atlasworld.generator.file.JsonFileLoader;
import org.slf4j.Logger;

import java.io.File;

public class GenerateCommand {
    public static void register(CommandDispatcher<CommandSource> dispatcher) {
        dispatcher.register(LiteralArgumentBuilder.<CommandSource>literal("generate")
                .executes(ctx -> generateDistro(ctx.getSource()))
        );
    }

    private static int generateDistro(CommandSource source) {
        source.logger().warn("Generating Distribution Index..");
        File downloadHubRootDirectory = FileManager.getWorkingDirectoryFile("hub");
        File downloadHubServersDirectory = new File(downloadHubRootDirectory, "downloads/servers");
        File serverConfigDirectory = FileManager.getWorkingDirectoryFile("servers");

        source.logger().info("Verifying..");
        File[] servers = downloadHubServersDirectory.listFiles();
        if (servers == null || servers.length < 1) {
            source.logger().warn("No file/assets found for the index.");
            return Command.SINGLE_SUCCESS;
        }

        JsonFileLoader indexLoader = new JsonFileLoader(new File(downloadHubRootDirectory, "index.json"), true);
        if (indexLoader.fileExists()) {
            source.logger().info("Deleting old index..");
            indexLoader.getFile().delete();
        }

        JsonArray indexContent = new JsonArray();

        for (File server : servers) {
            source.logger().info("Verifying '{}'..", server.getName());
            if (server.isFile()) {
                source.logger().error("{} may only contain server directories!", downloadHubRootDirectory.getAbsolutePath());
                return Command.SINGLE_SUCCESS;
            }

            GsonFileLoader<ServerConfigFile> serverConfigLoader = new GsonFileLoader<>(
                    new File(serverConfigDirectory, server.getName() + ".json"),
                    ServerConfigFile.class);

            if (!serverConfigLoader.fileExists()) {
                source.logger().error("Could not find configuration for server '{}'", server.getName());
                return Command.SINGLE_SUCCESS;
            }

            source.logger().info("Loading server configuration..");
            ServerConfigFile serverConfiguration = serverConfigLoader.load();

            source.logger().info("Starting indexing server..");
            JsonObject serverIndexEntry = new JsonObject();
            serverIndexEntry.addProperty("id", serverConfiguration.id());
            serverIndexEntry.addProperty("version", serverConfiguration.version());

            source.logger().info("Indexing server files..");
            JsonArray serverFileIndex = new JsonArray();

            indexDirectory(source.logger(), server, serverFileIndex, "");

            serverIndexEntry.add("files", serverFileIndex);
            indexContent.add(serverIndexEntry);
        }

        indexLoader.createFile();
        indexLoader.save(indexContent);

        return Command.SINGLE_SUCCESS;
    }

    private static void indexDirectory(Logger logger, File directory, JsonArray index, String remoteDir) {
        File[] files = directory.listFiles();

        if (files == null || files.length < 1) {
            logger.warn("{} is empty, skipping indexation for directory", directory.getAbsolutePath());
            return;
        }

        Gson gson = new Gson();
        for (File file : files) {
            if (file.isDirectory()) {
                indexDirectory(logger, file, index, remoteDir + "/" + file.getName());
                continue;
            }

            logger.info("Indexing {}", file.getAbsolutePath());

            String remoteLocation = remoteDir + "/" + file.getName();
            if (remoteLocation.startsWith("/")) {
                remoteLocation = remoteLocation.replaceFirst("/", "");
            }

            JsonObject fileEntry = new JsonObject();
            fileEntry.addProperty("name", file.getName());
            fileEntry.addProperty("location", file.getPath().replace("\\", "/"));
            fileEntry.addProperty("remote_location", remoteLocation);

            logger.info("Index: {}", gson.toJson(fileEntry));

            index.add(fileEntry);
        }
    }
}
