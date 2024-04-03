package me.kyren223.kapi.commands;

import me.kyren223.kapi.annotations.Kapi;
import me.kyren223.kapi.commands.builtin.LiteralArgumentType;
import me.kyren223.kapi.data.Result;
import me.kyren223.kapi.utility.KapiRegistry;
import me.kyren223.kapi.utility.Log;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;

/**
 * A utility class for building commands.
 */
@Kapi
public class CommandBuilder {
    
    private final String name;
    private ArgumentBuilder<CommandBuilder> argumentBuilder;
    private BiConsumer<ExecutionCommandContext, String> failureHandler;
    
    private CommandBuilder(String name) {
        this.name = name;
    }
    
    /**
     * Creates a new command builder.
     *
     * @param name The name of the command
     * @return the command builder for further configuration
     */
    @Kapi
    public static ArgumentBuilder<CommandBuilder> create(String name) {
        CommandBuilder command = new CommandBuilder(name);
        command.argumentBuilder = ArgumentBuilder.construct(command);
        return command.argumentBuilder;
    }
    
    /**
     * Registers the command to the Kapi command registry.
     * Uses {@link KapiRegistry#register(String, CommandExecutor, TabCompleter)}.
     */
    @Kapi
    public void register() {
        KapiRegistry.register(name, this::onCommand, this::onTabComplete);
    }
    
    /**
     * Sets the failure handler for the command.<br>
     * <br>
     * This is called whenever an error occurs, this can be used
     * to provide feedback to the sender.<br>
     * <br>
     * It's recommended to check whether a
     * {@link ExecutionCommandContext} or a {@link SuggestionCommandContext}
     * is being passed to the handler.<br>
     * As you probably don't want to send a message to the sender
     * when a suggestion is being processed.
     *
     * @param failureHandler A consumer that handles command failures
     *                       The first parameter is the context of the command
     *                       The second parameter is the error message (can be null)
     * @return the command builder for chaining
     */
    @Kapi
    public CommandBuilder onFail(BiConsumer<ExecutionCommandContext, String> failureHandler) {
        this.failureHandler = failureHandler;
        return this;
    }
    
    /**
     * Sets the failure handler to automatically log the error message.<br>
     * <br>
     * This uses {@link Log#error(String, CommandSender...)} to log the error message.<br>
     * This does nothing if the error message is null or
     * if the context is a {@link SuggestionCommandContext}.
     *
     * @return the command builder for chaining
     */
    public CommandBuilder autoFailHandler() {
        return onFail((context, message) -> {
            if (message == null) return;
            Log.error(message, context.getSender());
        });
    }
    
    private boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        ExecutionCommandContext context = new ExecutionCommandContext(
                sender, command, label, args
        );
        
        ArgumentBuilder<?> builder = argumentBuilder;
        ArgumentType<?> type = LiteralArgumentType.literal(label).ignoreCase();
        String argumentName = null;
        List<String> arguments = new ArrayList<>(List.of(args));
        arguments.add(0, label);
        
        while (true) {
            // Check if all requirements are met
            for (var requirement : builder.getRequirements()) {
                if (requirement.second.test(context)) continue;
                
                if (failureHandler != null) {
                    failureHandler.accept(context, requirement.first);
                }
                
                return context.getReturnValue();
            }
            
            if (arguments.isEmpty()) {
                break;
            }
            
            // Parse current argument
            Result<?, String> result = type.parse(arguments);
            // TODO
            
            
        }
        
        builder.getExecutor().accept(context);
        
        return context.getReturnValue();
    }
    
    private List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        SuggestionCommandContext context = new SuggestionCommandContext(
                sender, command, label, args
        );
        
        return context.getReturnValue();
    }
}
