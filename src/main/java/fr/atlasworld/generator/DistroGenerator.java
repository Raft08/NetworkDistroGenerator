package fr.atlasworld.generator;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import fr.atlasworld.generator.commands.CommandSource;
import fr.atlasworld.generator.commands.CreateCommand;
import fr.atlasworld.generator.commands.GenerateCommand;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DistroGenerator {
    private static final Logger logger = LoggerFactory.getLogger("DistroGen");

    public static void main(String[] args) {
        CommandDispatcher<CommandSource> dispatcher = new CommandDispatcher<>();
        register(dispatcher);

        StringBuilder argsParser = new StringBuilder();
        for (String arg : args) {
            argsParser.append(arg).append(" ");
        }

        try {
            dispatcher.execute(argsParser.toString().trim(), new CommandSource(logger));
        } catch (CommandSyntaxException e) {
            DistroGenerator.logger.error("Unknown command!");
            DistroGenerator.logger.error("{} <-- [HERE]", argsParser.toString().trim());
        }
    }

    private static void register(CommandDispatcher<CommandSource> dispatcher) {
        CreateCommand.register(dispatcher);
        GenerateCommand.register(dispatcher);
    }
}
