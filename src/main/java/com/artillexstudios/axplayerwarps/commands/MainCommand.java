package com.artillexstudios.axplayerwarps.commands;

import com.artillexstudios.axapi.nms.NMSHandlers;
import com.artillexstudios.axplayerwarps.AxPlayerWarps;
import com.artillexstudios.axplayerwarps.commands.subcommands.Create;
import com.artillexstudios.axplayerwarps.commands.subcommands.Open;
import com.artillexstudios.axplayerwarps.commands.subcommands.Reload;
import com.artillexstudios.axplayerwarps.utils.CommandMessages;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import revxrsal.commands.annotation.DefaultFor;
import revxrsal.commands.annotation.Optional;
import revxrsal.commands.annotation.Subcommand;
import revxrsal.commands.bukkit.BukkitCommandActor;
import revxrsal.commands.bukkit.BukkitCommandHandler;
import revxrsal.commands.bukkit.annotation.CommandPermission;
import revxrsal.commands.bukkit.exception.InvalidPlayerException;
import revxrsal.commands.orphan.OrphanCommand;
import revxrsal.commands.orphan.Orphans;

import java.util.Locale;
import java.util.stream.Collectors;

import static com.artillexstudios.axplayerwarps.AxPlayerWarps.CONFIG;

public class MainCommand implements OrphanCommand {

    @DefaultFor({"~"})
    @CommandPermission("axplayerwarps.open")
    public void open(@NotNull CommandSender sender) {
        Open.INSTANCE.execute(sender, null);
    }

    @Subcommand({"open"})
    @CommandPermission("axplayerwarps.open")
    public void open2(@NotNull CommandSender sender, @CommandPermission("axplayerwarps.open.other") @Optional Player player) {
        Open.INSTANCE.execute(sender, player);
    }

    @Subcommand({"create", "set"})
    @CommandPermission("axplayerwarps.create")
    public void create(@NotNull Player sender, String warpName, @CommandPermission("axplayerwarps.create.other") @Optional OfflinePlayer player) {
        Create.INSTANCE.execute(sender, warpName, player);
    }

    @Subcommand({"reload"})
    @CommandPermission("axplayerwarps.reload")
    public void reload(@NotNull CommandSender sender) {
        Reload.INSTANCE.execute(sender);
    }

    private static BukkitCommandHandler handler = null;

    public static void registerCommand() {
        if (handler == null) {
            handler = BukkitCommandHandler.create(AxPlayerWarps.getInstance());

            handler.registerValueResolver(0, OfflinePlayer.class, context -> {
                String value = context.pop();
                if (value.equalsIgnoreCase("self") || value.equalsIgnoreCase("me")) return ((BukkitCommandActor) context.actor()).requirePlayer();
                OfflinePlayer player = NMSHandlers.getNmsHandler().getCachedOfflinePlayer(value);
                if (player == null && !(player = Bukkit.getOfflinePlayer(value)).hasPlayedBefore()) throw new InvalidPlayerException(context.parameter(), value);
                return player;
            });

            handler.getAutoCompleter().registerParameterSuggestions(OfflinePlayer.class, (args, sender, command) -> {
                return Bukkit.getOnlinePlayers().stream().map(HumanEntity::getName).collect(Collectors.toSet());
            });

            handler.getTranslator().add(new CommandMessages());
            handler.setLocale(new Locale("en", "US"));
        }

        handler.unregisterAllCommands();
        handler.register(Orphans.path(CONFIG.getStringList("open-command-aliases").toArray(String[]::new)).handler(new MainCommand()));
        handler.registerBrigadier();
    }
}
