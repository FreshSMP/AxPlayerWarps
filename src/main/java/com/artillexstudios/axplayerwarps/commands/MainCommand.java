package com.artillexstudios.axplayerwarps.commands;

import com.artillexstudios.axapi.utils.StringUtils;
import com.artillexstudios.axplayerwarps.AxPlayerWarps;
import com.artillexstudios.axplayerwarps.commands.annotations.AllWarps;
import com.artillexstudios.axplayerwarps.commands.annotations.OwnWarps;
import com.artillexstudios.axplayerwarps.commands.subcommands.Create;
import com.artillexstudios.axplayerwarps.commands.subcommands.Info;
import com.artillexstudios.axplayerwarps.commands.subcommands.Open;
import com.artillexstudios.axplayerwarps.guis.EditWarpGui;
import com.artillexstudios.axplayerwarps.utils.CommandMessages;
import com.artillexstudios.axplayerwarps.warps.Warp;
import com.artillexstudios.axplayerwarps.warps.WarpManager;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import revxrsal.commands.annotation.DefaultFor;
import revxrsal.commands.annotation.Optional;
import revxrsal.commands.annotation.Subcommand;
import revxrsal.commands.bukkit.BukkitCommandActor;
import revxrsal.commands.bukkit.BukkitCommandHandler;
import revxrsal.commands.bukkit.annotation.CommandPermission;
import revxrsal.commands.exception.CommandErrorException;
import revxrsal.commands.orphan.OrphanCommand;
import revxrsal.commands.orphan.Orphans;

import java.util.Locale;
import java.util.Map;

import static com.artillexstudios.axplayerwarps.AxPlayerWarps.CONFIG;
import static com.artillexstudios.axplayerwarps.AxPlayerWarps.LANG;
import static com.artillexstudios.axplayerwarps.AxPlayerWarps.MESSAGEUTILS;

public class MainCommand implements OrphanCommand {

    @DefaultFor({"~"})
    @CommandPermission("axplayerwarps.open")
    public void open(@NotNull CommandSender sender, @Optional @CommandPermission("axplayerwarps.use") Warp warp) {
        if (warp != null) {
            if (!(sender instanceof Player pl)) throw new CommandErrorException("must-be-player");
            warp.teleportPlayer(pl);
            return;
        }
        Open.INSTANCE.execute(sender, null);
    }

    @Subcommand({"help"})
    @CommandPermission("axplayerwarps.help")
    public void help(@NotNull CommandSender sender) {
        for (String m : LANG.getStringList("help")) {
            sender.sendMessage(StringUtils.formatToString(m));
        }
    }

    @Subcommand({"open"})
    @CommandPermission("axplayerwarps.open")
    public void open2(@NotNull CommandSender sender, @CommandPermission("axplayerwarps.open.other") @Optional Player player) {
        Open.INSTANCE.execute(sender, player);
    }

    @Subcommand({"warp", "go"})
    @CommandPermission("axplayerwarps.use")
    public void warp(@NotNull Player sender, @AllWarps Warp warp) {
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
        if (!warp.getOwner().equals(sender.getUniqueId())) {
            MESSAGEUTILS.sendLang(sender, "errors.not-your-warp");
            return;
        }
        warp.delete();
    }

    @Subcommand({"edit", "settings"})
    @CommandPermission("axplayerwarps.edit")
    public void edit(@NotNull Player sender, @OwnWarps Warp warp) {
        if (!warp.getOwner().equals(sender.getUniqueId())) {
            MESSAGEUTILS.sendLang(sender, "errors.not-your-warp");
            return;
        }
        new EditWarpGui(sender, warp).open();
    }

    @Subcommand({"info"})
    @CommandPermission("axplayerwarps.info")
    public void info(@NotNull CommandSender sender, @AllWarps Warp warp) {
        Info.INSTANCE.execute(sender, warp);
    }

    private static BukkitCommandHandler handler = null;

    public static void registerCommand() {
        if (handler == null) {
            handler = BukkitCommandHandler.create(AxPlayerWarps.getInstance());

            handler.getAutoCompleter().registerSuggestionFactory(parameter -> {
                if (parameter.hasAnnotation(AllWarps.class)) {
                    return (args, sender, command) -> {
                        return WarpManager.getWarps().stream().map(Warp::getName).toList();
                    };
                }
                if (parameter.hasAnnotation(OwnWarps.class)) {
                    return (args, sender, command) -> {
                        return WarpManager.getWarps().stream().filter(warp -> warp.getOwner().equals(sender.getUniqueId())).map(Warp::getName).toList();
                    };
                }
                return null;
            });

            handler.registerValueResolver(Warp.class, resolver -> {
                final String str = resolver.popForParameter();
                java.util.Optional<Warp> opt = WarpManager.getWarps().stream().filter(warp -> warp.getName().equals(str)).findAny();
                if (opt.isEmpty()) {
                    MESSAGEUTILS.sendLang(resolver.actor().as(BukkitCommandActor.class).getSender(), "errors.not-found", Map.of("%warp%", str));
                    throw new CommandErrorException();
                }
                return opt.get();
            });

            handler.getTranslator().add(new CommandMessages());
            handler.setLocale(Locale.of("en", "US"));
        }

        handler.unregisterAllCommands();
        handler.register(Orphans.path(CONFIG.getStringList("main-command-aliases").toArray(String[]::new)).handler(new MainCommand()));
        handler.register(Orphans.path(CONFIG.getStringList("admin-command-aliases").toArray(String[]::new)).handler(new AdminCommand()));
        handler.registerBrigadier();
    }
}
