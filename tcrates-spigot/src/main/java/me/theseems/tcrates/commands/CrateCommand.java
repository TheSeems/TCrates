package me.theseems.tcrates.commands;

import me.theseems.tcrates.Crate;
import me.theseems.tcrates.TCratesAPI;
import me.theseems.tcrates.TCratesPlugin;
import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.io.IOException;
import java.util.Arrays;
import java.util.Optional;

public class CrateCommand implements CommandExecutor {
  public void sendBanner(CommandSender sender) {
    sender.sendMessage(
        "§a§lTCrates §fby TheSeems<me@theseems.ru> §7v"
            + TCratesPlugin.getPlugin().getDescription().getVersion());
  }

  private boolean open(CommandSender commandSender, Command command, String s, String[] args) {
    if (args.length == 0) {
      commandSender.sendMessage("§7Please, specify crate to open");
      return true;
    }

    if (!(commandSender instanceof Player)) {
      commandSender.sendMessage("§7This command is only available in game");
      return true;
    }

    Player actual = (Player) commandSender;

    TCratesAPI.getCrateManager()
        .find(args[0])
        .ifPresentOrElse(
            crate -> {
              if (crate.getRequirements().canOpen(actual.getUniqueId())) {
                actual.sendMessage("§7Opening...");
                crate.open(actual.getUniqueId());
              } else {
                actual.sendMessage("§7You cannot open this case...");
              }
            },
            () -> actual.sendMessage("§7No crate found by name '" + args[0] + "'"));

    return true;
  }

  private boolean put(CommandSender sender, Command command, String s, String[] args) {
    if (!(sender instanceof Player)) {
      sender.sendMessage("§7This command is only available in game");
      return true;
    }

    if (args.length == 0) {
      sender.sendMessage("§7Please, specify crate to register block for");
      return false;
    }

    String crateName = args[0];
    Optional<Crate> optionalCrate = TCratesAPI.getCrateManager().find(crateName);
    if (!optionalCrate.isPresent()) {
      sender.sendMessage("§7Crate '" + args[0] + "' is not found");
      return false;
    }

    Player actual = (Player) sender;
    Block block = actual.getTargetBlockExact(10);

    if (block == null) {
      sender.sendMessage("§7Please, look at the crate-block");
      return false;
    }

    TCratesPlugin.getBlockCrate().register(args[0], block.getLocation());
    sender.sendMessage("§aBlock is registered");
    return false;
  }

  private boolean setKeys(CommandSender sender, Command command, String s, String[] args) {
    if (args.length < 3) {
      sender.sendMessage("§7Please, specify player, crate and key count to set");
      return false;
    }

    Player player = Bukkit.getPlayer(args[0]);
    if (player == null) {
      sender.sendMessage("§7Player '" + args[0] + "' is offline");
      return false;
    }

    String crateName = args[1];
    Optional<Crate> optionalCrate = TCratesAPI.getCrateManager().find(crateName);
    if (!optionalCrate.isPresent()) {
      sender.sendMessage("§7Crate '" + args[1] + "' is not found");
      return false;
    }

    try {
      TCratesAPI.getKeyStorage()
          .setKeysFor(player.getUniqueId(), crateName, Integer.parseInt(args[2]));
      sender.sendMessage(
          "§aSet §7" + Integer.parseInt(args[2]) + "§a key(s) for a player " + args[0]);
      return true;
    } catch (NumberFormatException e) {
      sender.sendMessage("§7Invalid key count: " + args[2]);
      return false;
    }
  }

  private boolean setMeta(CommandSender sender, Command command, String s, String[] args) {
    if (args.length < 3) {
      sender.sendMessage("§7Please, specify crate, key and value to set");
      return false;
    }

    String crateName = args[0];
    Optional<Crate> optionalCrate = TCratesAPI.getCrateManager().find(crateName);
    if (!optionalCrate.isPresent()) {
      sender.sendMessage("§7Crate '" + args[0] + "' is not found");
      return false;
    }

    optionalCrate.get().getMeta().set(args[1], args[2]);
    sender.sendMessage(
        "§aSet meta §7" + args[1] + " : " + optionalCrate.get().getMeta().getString(args[1]).get());
    return true;
  }

  private boolean listMeta(CommandSender sender, Command command, String s, String[] args) {
    if (args.length == 0) {
      sender.sendMessage("§7Please, specify crate to get meta for");
      return false;
    }

    String crateName = args[0];
    Optional<Crate> optionalCrate = TCratesAPI.getCrateManager().find(crateName);
    if (!optionalCrate.isPresent()) {
      sender.sendMessage("§7Crate '" + args[0] + "' is not found");
      return false;
    }

    for (String key : optionalCrate.get().getMeta().getKeys()) {
      optionalCrate
          .get()
          .getMeta()
          .get(key)
          .ifPresent(o -> sender.sendMessage("§6" + key + " §8: §e" + o));
    }
    return true;
  }

  public boolean reload(CommandSender sender, Command command, String s, String[] args) {
    try {
      TCratesPlugin.loadCases();
      sender.sendMessage("§aOK!");
      return true;
    } catch (IOException e) {
      sender.sendMessage("§cError reloading plugin: " + e.getMessage());
      e.printStackTrace();
      return false;
    }
  }

  public boolean save(CommandSender sender, Command command, String s, String[] args) {
    try {
      TCratesPlugin.saveCases();
      sender.sendMessage("§aOK!");
      return true;
    } catch (IOException e) {
      sender.sendMessage("§cError saving crates: " + e.getMessage());
      e.printStackTrace();
      return false;
    }
  }

  @Override
  public boolean onCommand(
      CommandSender commandSender, Command command, String s, String[] strings) {
    if (strings.length == 0) {
      sendBanner(commandSender);
      return true;
    }

    String sub = strings[0];
    strings = Arrays.copyOfRange(strings, 1, strings.length);

    if (!commandSender.hasPermission("tcrates." + sub)) {
      sendBanner(commandSender);
      return false;
    }

    switch (sub) {
      case "open":
        return open(commandSender, command, s, strings);
      case "put":
        return put(commandSender, command, s, strings);
      case "setkeys":
        return setKeys(commandSender, command, s, strings);
      case "setmeta":
        return setMeta(commandSender, command, s, strings);
      case "listmeta":
        return listMeta(commandSender, command, s, strings);
      case "reload":
        return reload(commandSender, command, s, strings);
      case "save":
        return save(commandSender, command, s, strings);
      default:
        sendBanner(commandSender);
        break;
    }

    return true;
  }
}
