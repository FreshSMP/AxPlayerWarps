package com.artillexstudios.axplayerwarps.commands;

import com.artillexstudios.axapi.utils.StringUtils;
import com.artillexstudios.axplayerwarps.commands.annotations.AllWarps;
import com.artillexstudios.axplayerwarps.commands.annotations.OwnWarps;
import com.artillexstudios.axplayerwarps.commands.subcommands.Create;
import com.artillexstudios.axplayerwarps.commands.subcommands.Info;
import com.artillexstudios.axplayerwarps.commands.subcommands.Open;
import com.artillexstudios.axplayerwarps.guis.EditWarpGui;
import com.artillexstudios.axplayerwarps.warps.Warp;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import revxrsal.commands.annotation.DefaultFor;
import revxrsal.commands.annotation.Optional;
import revxrsal.commands.annotation.Subcommand;
import revxrsal.commands.bukkit.annotation.CommandPermission;
import revxrsal.commands.exception.CommandErrorException;
import revxrsal.commands.orphan.OrphanCommand;

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
}
