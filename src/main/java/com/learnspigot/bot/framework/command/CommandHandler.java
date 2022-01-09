package com.learnspigot.bot.framework.command;

import com.learnspigot.bot.LearnSpigotBot;
import com.learnspigot.bot.LearnSpigotConstant;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.privileges.CommandPrivilege;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public final class CommandHandler extends ListenerAdapter {
    private final @NotNull LearnSpigotBot bot;
    private final @NotNull Map<String, Map.Entry<Method, Object>> commandMap;
    private final @NotNull SimpleDateFormat format = new SimpleDateFormat("[HH:mm:ss]");

    public CommandHandler(final @NotNull LearnSpigotBot bot) {
        this.bot = bot;
        this.commandMap = new ConcurrentHashMap<>();
        bot.jda().addEventListener(this);
    }

    private void handleCommand(final @NotNull SlashCommandEvent event) {
        Map.Entry<String, Map.Entry<Method, Object>> entry =
                commandMap.entrySet().stream().filter(command -> command.getKey().equalsIgnoreCase(event.getName()))
                        .findFirst().orElse(null);

        if (entry != null) {
            Method method = entry.getValue().getKey();
            Object object = entry.getValue().getValue();
            Command command = method.getAnnotation(Command.class);

            if (event.getMember() == null) {
                return;
            }

            CommandInfo info = new CommandInfo(this, event, command.usage());

            try {
                method.invoke(object, info);
            } catch (IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
            }

            if (command.log()) {
                System.out.println(format.format(new Date()) +
                        " [LOG]: " + info.author().getAsTag() + " executed "
                        + info.message());
            }
        }
    }

    public void registerCommands(final @NotNull Object... objects) {
        for (Object object : objects) {
            for (Method method : object.getClass().getMethods()) {
                if (method.getAnnotation(Command.class) != null) {
                    Command command = method.getAnnotation(Command.class);
                    registerCommand(command.label(), method, object, command);

                    for (String alias : command.aliases()) {
                        registerCommand(alias, method, object, command);
                    }
                }
            }
        }
    }

    private void registerCommand(final @NotNull String label, final @NotNull Method method,
                                 final @NotNull Object object, final @NotNull Command command) {
        registerSlashCommand(label, command);
        commandMap.put(label.toLowerCase(), new AbstractMap.SimpleEntry<>(method, object));
    }

    private void registerSlashCommand(final @NotNull String label, final @NotNull Command command) {
        String[] args = command.usage().split(" ");
        List<OptionData> optionData = new ArrayList<>();
        for (int i = 1; i < args.length; i++) {
            optionData.add(new OptionData(OptionType.STRING, args[i].substring(1, args[i].length()-1),
                    args[i].substring(1, args[i].length()-1), args[i].startsWith("<")));
        }

        CommandData commandData = new CommandData(label, command.description());
        commandData.setDefaultEnabled(command.roleId() == 0);

        if (!optionData.isEmpty()) {
            commandData.addOptions(optionData);
        }
        Guild guild = bot.jda().getGuildById(((long) LearnSpigotConstant.GUILD_ID.get()));
        assert guild != null;
        guild.upsertCommand(commandData).queue(upsertCommand -> {
            if (command.roleId() != 0) {
                guild.updateCommandPrivilegesById(upsertCommand.getId(),
                        new CommandPrivilege(CommandPrivilege.Type.ROLE, true, command.roleId())).queue();
            }
        });
    }

    @Override
    public void onSlashCommand(final @NotNull SlashCommandEvent event) {
        handleCommand(event);
    }

    public @NotNull Map<String, Map.Entry<Method, Object>> commandMap() {
        return commandMap;
    }
}
