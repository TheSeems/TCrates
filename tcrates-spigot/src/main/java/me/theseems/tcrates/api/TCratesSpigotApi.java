package me.theseems.tcrates.api;

import me.theseems.tcrates.TCratesPlugin;
import me.theseems.tcrates.activators.BlockCrate;
import me.theseems.tcrates.config.CrateRewardConfigManager;
import me.theseems.tcrates.handlers.AutoGrantHandler;

public class TCratesSpigotApi {

  public static AutoGrantHandler grantHandler;
  public static BlockCrate blockCrate;
  public static CrateRewardConfigManager manager;
  public static TCratesPlugin plugin;

  public TCratesSpigotApi(TCratesPlugin plugin) {
    TCratesSpigotApi.plugin = plugin;
  }

  public static TCratesPlugin getPlugin() {
    return plugin;
  }

  public static AutoGrantHandler getGrantHandler() {
    return grantHandler;
  }

  public static void setGrantHandler(AutoGrantHandler grantHandler) {
    TCratesSpigotApi.grantHandler = grantHandler;
  }

  public static BlockCrate getBlockCrate() {
    return blockCrate;
  }

  public static void setBlockCrate(BlockCrate blockCrate) {
    TCratesSpigotApi.blockCrate = blockCrate;
  }

  public static CrateRewardConfigManager getManager() {
    return manager;
  }

  public static void setManager(CrateRewardConfigManager manager) {
    TCratesSpigotApi.manager = manager;
  }
}
