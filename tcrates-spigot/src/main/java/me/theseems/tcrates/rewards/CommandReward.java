package me.theseems.tcrates.rewards;

import me.clip.placeholderapi.PlaceholderAPI;
import me.theseems.tcrates.CrateMeta;
import me.theseems.tcrates.MemoryCrateMeta;
import org.bukkit.Bukkit;

import java.util.UUID;

public abstract class CommandReward implements IconReward {
  private String command;
  private MemoryCrateMeta meta;

  @Override
  public MemoryCrateMeta getMeta() {
    return meta;
  }

  public void setMeta(MemoryCrateMeta meta) {
    this.meta = meta;
  }

  public CommandReward(String command) {
    this.command = command;
    this.meta = new MemoryCrateMeta();
    meta.set("type", "command");
    meta.set("command", command);
  }

  @Override
  public void setMeta(CrateMeta meta) {
    this.meta = MemoryCrateMeta.to(meta);
  }

  @Override
  public void give(UUID player) {
    if (Bukkit.getPlayer(player) != null) {
      String command = PlaceholderAPI.setPlaceholders(Bukkit.getPlayer(player), this.command);
      System.err.println("Executing command: " + command);
      Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command);
    }
  }

  public String getCommand() {
    return command;
  }
}
