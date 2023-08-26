package fr.atlasworld.generator.commands;

import com.google.gson.GsonBuilder;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import fr.atlasworld.generator.config.ServerConfigFile;
import fr.atlasworld.generator.file.FileManager;
import fr.atlasworld.generator.file.GsonFileLoader;

import java.io.File;

public class CreateCommand {
    public static void register(CommandDispatcher<CommandSource> dispatcher) {
        dispatcher.register(LiteralArgumentBuilder.<CommandSource>literal("create")
                .then(RequiredArgumentBuilder.<CommandSource, String>argument("id", StringArgumentType.word())
                        .executes(ctx -> create(ctx.getSource(), ctx.getArgument("id", String.class)))
                )
        );
    }

    private static int create(CommandSource source, String id) {
        source.logger().info("Creating server..");

        File serverFilesDirectory = FileManager.getWorkingDirectoryFile("hub/downloads/servers");
        File serversConfigDirectory = FileManager.getWorkingDirectoryFile("servers");

        File serverDir = new File(serverFilesDirectory, id);
        if (serverDir.exists()) {
            source.logger().error("Cannot create multiple servers with the same name!");
            return Command.SINGLE_SUCCESS;
        }

        try {
            serverDir.mkdirs();
        } catch (Exception e) {
            source.logger().error("Could not create server", e);
            return Command.SINGLE_SUCCESS;
        }

        File configs = new File(serverDir, "configuration");
        File plugins = new File(serverDir, "plugins");
        File worlds = new File(serverDir, "worlds");
        File other = new File(serverDir, "files");


        configs.mkdirs();
        source.logger().info("Created configurations directory");
        plugins.mkdirs();
        source.logger().info("Created plugins directory");
        worlds.mkdirs();
        source.logger().info("Created worlds directory");
        other.mkdirs();
        source.logger().info("Created files directory");

        GsonFileLoader<ServerConfigFile> serverLoader = new GsonFileLoader<>(
                new File(serversConfigDirectory, id + ".json"),
                new GsonBuilder().setPrettyPrinting().create(),
                ServerConfigFile.class
        );

        if (!serversConfigDirectory.isDirectory()) {
            serversConfigDirectory.mkdirs();
        }

        serverLoader.createFile();
        serverLoader.save(ServerConfigFile.create(id));

        source.logger().info("Server created!");

        return Command.SINGLE_SUCCESS;
    }
}
