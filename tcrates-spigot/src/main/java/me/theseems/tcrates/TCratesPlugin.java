package me.theseems.tcrates;

import com.google.gson.GsonBuilder;
import me.theseems.tcrates.activators.BlockCrate;
import me.theseems.tcrates.commands.CrateCommand;
import me.theseems.tcrates.config.CrateConfig;
import me.theseems.tcrates.config.CrateRewardConfigManager;
import me.theseems.tcrates.config.DBConfig;
import me.theseems.tcrates.config.TCratesConfig;
import me.theseems.tcrates.handlers.AutoGrantHandler;
import me.theseems.tcrates.rewards.GroupReward;
import me.theseems.tcrates.rewards.MoneyReward;
import me.theseems.tcrates.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.logging.Logger;

public class TCratesPlugin extends JavaPlugin {
  private static Plugin plugin;
  private static AutoGrantHandler grantHandler;
  private static BlockCrate blockCrate;
  private static CrateRewardConfigManager manager;

  private static File loadFile(String name) throws IOException {
    File file = new File(getPlugin().getDataFolder(), name);
    if (!plugin.getDataFolder().exists()) plugin.getDataFolder().mkdir();
    if (!file.exists()) file.createNewFile();

    return file;
  }

  public static void loadCases() throws IOException {
    File file = loadFile("crates.json");

    TCratesConfig config =
        new GsonBuilder().create().fromJson(new FileReader(file), TCratesConfig.class);

    for (CrateConfig crate : config.getCrates()) {
      Crate actual = crate.makeCrate();
      TCratesAPI.getCrateManager().register(actual);
    }

    blockCrate.clear();
    blockCrate.scan();
  }

  public static void saveCases() throws IOException {
    List<CrateConfig> crateConfigList = new ArrayList<>();
    for (String crate : TCratesAPI.getCrateManager().getCrates()) {
      CrateConfig config = Utils.makeConfig(TCratesAPI.getCrateManager().get(crate));
      crateConfigList.add(config);
    }
    TCratesConfig tCratesConfig = new TCratesConfig(crateConfigList);

    File file = loadFile("crates.json");
    FileWriter writer = new FileWriter(file);
    new GsonBuilder().setPrettyPrinting().create().toJson(tCratesConfig, writer);
    writer.flush();
  }

  private void loadFeatures() {
    blockCrate = new BlockCrate();
    blockCrate.scan();
  }

  private void loadDatabase() throws IOException {
    File file = loadFile("data.json");
    DBConfig config =
        new GsonBuilder()
            .setPrettyPrinting()
            .create()
            .fromJson(new FileReader(file), DBConfig.class);

    if (config.getType().equals("jdbc")) {
      TCratesAPI.setKeyStorage(new JDBCKeyStorage(config.getPool()));
    } else {
      TCratesAPI.setKeyStorage(new MemoryKeyStorage());
    }
  }

  @Override
  public void onLoad() {
    TCratesAPI.setCrateManager(new SimpleCrateManager());
    TCratesAPI.setRewardQueue(new MemoryRewardQueue());
    manager = new CrateRewardConfigManager();
  }

  @Override
  public void onEnable() {
    plugin = this;
    manager.register(
        "money",
        crateRewardConfig ->
            new MoneyReward(crateRewardConfig.getMeta().getInteger("money").orElse(0)) {
              @Override
              public ItemStack getIcon(UUID player) {
                return crateRewardConfig.getIcon().getStack();
              }

              @Override
              public String getName() {
                return crateRewardConfig.getName();
              }
            });
    manager.register(
        "group",
        crateRewardConfig ->
            new GroupReward(
                crateRewardConfig.getMeta().getString("group").orElse("default"),
                crateRewardConfig.getMeta().getString("context").orElse("global")) {
              @Override
              public ItemStack getIcon(UUID player) {
                return crateRewardConfig.getIcon().getStack();
              }
            });

    try {
      loadDatabase();
    } catch (Exception e) {
      getLogger().warning("Error getting info about database, using memory one");
      e.printStackTrace();
      TCratesAPI.setKeyStorage(new MemoryKeyStorage());
    }

    loadFeatures();
    try {
      loadCases();
    } catch (IOException e) {
      getLogger().warning("Error loading crates: " + e.getMessage());
      e.printStackTrace();
    }

    grantHandler = new AutoGrantHandler();
    Objects.requireNonNull(getCommand("crate")).setExecutor(new CrateCommand());

    if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
      new CrateExpansion().register();
    }
  }

  public static Plugin getPlugin() {
    return plugin;
  }

  public static BlockCrate getBlockCrate() {
    return blockCrate;
  }

  @Override
  public void onDisable() {
    try {
      saveCases();
    } catch (IOException e) {
      getLogger().warning("Error saving crates: " + e.getMessage());
      e.printStackTrace();
    }
    grantHandler.getTask().cancel();
  }

  public static CrateRewardConfigManager getManager() {
    return manager;
  }

  public static Logger getPluginLogger() {
    return plugin.getLogger();
  }
}
