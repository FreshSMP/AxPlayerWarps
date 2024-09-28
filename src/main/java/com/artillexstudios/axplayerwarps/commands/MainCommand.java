package com.artillexstudios.axplayerwarps.commands;

import com.artillexstudios.axapi.nms.NMSHandlers;
import com.artillexstudios.axapi.utils.StringUtils;
import com.artillexstudios.axplayerwarps.AxPlayerWarps;
import com.artillexstudios.axplayerwarps.commands.annotations.OwnWarps;
import com.artillexstudios.axplayerwarps.commands.subcommands.Create;
import com.artillexstudios.axplayerwarps.commands.subcommands.Info;
import com.artillexstudios.axplayerwarps.commands.subcommands.Open;
import com.artillexstudios.axplayerwarps.commands.subcommands.Reload;
import com.artillexstudios.axplayerwarps.guis.impl.EditWarpGui;
import com.artillexstudios.axplayerwarps.utils.CommandMessages;
import com.artillexstudios.axplayerwarps.warps.Warp;
import com.artillexstudios.axplayerwarps.warps.WarpManager;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandException;
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
import revxrsal.commands.exception.CommandErrorException;
import revxrsal.commands.orphan.OrphanCommand;
import revxrsal.commands.orphan.Orphans;

import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

import static com.artillexstudios.axplayerwarps.AxPlayerWarps.CONFIG;
import static com.artillexstudios.axplayerwarps.AxPlayerWarps.MESSAGEUTILS;

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

    @Subcommand({"warp", "go"})
    @CommandPermission("axplayerwarps.use")
    public void warp(@NotNull Player sender, Warp warp) {
        warp.teleportPlayer(sender);
    }

    @Subcommand({"create", "set"})
    @CommandPermission("axplayerwarps.create") // @CommandPermission("axplayerwarps.create.other") @Optional OfflinePlayer player
    public void create(@NotNull Player sender, String warpName) {
        Create.INSTANCE.execute(sender, warpName, null);
    }

    @Subcommand({"delete"})
    @CommandPermission("axplayerwarps.delete")
    public void delete(@NotNull Player sender, @OwnWarps Warp warp) {
        if (!warp.getOwner().equals(sender.getUniqueId())) return;
        warp.delete();
    }

    @Subcommand({"edit", "settings"})
    @CommandPermission("axplayerwarps.edit")
    public void edit(@NotNull Player sender, @OwnWarps Warp warp) {
        if (!warp.getOwner().equals(sender.getUniqueId())) return;
        new EditWarpGui(sender, warp).open();
    }

    @Subcommand({"info"})
    @CommandPermission("axplayerwarps.info")
    public void info(@NotNull CommandSender sender, Warp warp) {
        Info.INSTANCE.execute(sender, warp);
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

            handler.getAutoCompleter().registerParameterSuggestions(Warp.class, (args, sender, command) -> {
                return WarpManager.getWarps().stream().map(Warp::getName).toList();
            });

            handler.getAutoCompleter().registerParameterSuggestions(OwnWarps.class, (args, sender, command) -> {
                return WarpManager.getWarps().stream().filter(warp -> warp.getOwner().equals(sender.getUniqueId())).map(Warp::getName).toList();
            });

            handler.registerValueResolver(Warp.class, resolver -> {
                final String str = resolver.popForParameter();
                java.util.Optional<Warp> opt = WarpManager.getWarps().stream().filter(warp -> warp.getName().equalsIgnoreCase(str)).findAny();
                if (opt.isEmpty()) {
                    MESSAGEUTILS.sendLang(resolver.actor().as(BukkitCommandActor.class).getSender(), "errors.not-found", Map.of("%warp%", str));
                    throw new CommandErrorException();
                }
                return opt.get();
            });

            handler.getTranslator().add(new CommandMessages());
            handler.setLocale(new Locale("en", "US"));
        }

        handler.unregisterAllCommands();
        handler.register(Orphans.path(CONFIG.getStringList("open-command-aliases").toArray(String[]::new)).handler(new MainCommand()));
        handler.registerBrigadier();
    }
}
