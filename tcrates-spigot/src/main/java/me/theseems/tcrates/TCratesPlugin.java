package me.theseems.tcrates;

import com.google.gson.GsonBuilder;
import me.theseems.tcrates.activators.BlockCrate;
import me.theseems.tcrates.api.TCratesSpigotApi;
import me.theseems.tcrates.commands.CrateCommand;
import me.theseems.tcrates.config.CrateConfig;
import me.theseems.tcrates.config.CrateRewardConfigManager;
import me.theseems.tcrates.config.DBConfig;
import me.theseems.tcrates.config.TCratesConfig;
import me.theseems.tcrates.handlers.AnnounceListener;
import me.theseems.tcrates.handlers.AutoGrantHandler;
import me.theseems.tcrates.handlers.CooldownListener;
import me.theseems.tcrates.handlers.GroupMergeListener;
import me.theseems.tcrates.rewards.CommandReward;
import me.theseems.tcrates.rewards.GroupReward;
import me.theseems.tcrates.rewards.ItemReward;
import me.theseems.tcrates.rewards.MoneyReward;
import me.theseems.tcrates.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;
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

import static me.theseems.tcrates.api.TCratesSpigotApi.blockCrate;

public class TCratesPlugin extends JavaPlugin {
  private static Plugin plugin;
  private static CrateRewardConfigManager configManager;

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
    Bukkit.getScheduler().runTaskTimer(this, blockCrate, 35, 35);
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
    plugin = this;
    TCratesAPI.setCrateManager(new SimpleCrateManager());
    TCratesAPI.setRewardQueue(new MemoryRewardQueue());
    configManager = new CrateRewardConfigManager();
    TCratesSpigotApi.setManager(configManager);
  }

  @Override
  public void onEnable() {
    TCratesSpigotApi.getManager()
        .register(
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
    TCratesSpigotApi.getManager()
        .register(
            "item",
            crateRewardConfig ->
                new ItemReward() {
                  @Override
                  public ItemStack get(Player player) {
                    crateRewardConfig
                        .getMeta()
                        .set("__other", crateRewardConfig.getOther().getStack());
                    return crateRewardConfig.getOther().getStack();
                  }

                  @Override
                  public ItemStack getIcon(UUID player) {
                    return crateRewardConfig.getIcon().getStack();
                  }

                  @Override
                  public String getName() {
                    return crateRewardConfig.getName();
                  }
                });

    if (Bukkit.getPluginManager().getPlugin("LuckPerms") != null) {
      TCratesSpigotApi.getManager()
          .register(
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
    } else {
      System.err.println("LuckPerms is not installed here, so 'group' type will not work!");
      System.err.println("As an alternative you may user commands in your crate's rewards");
    }

    TCratesSpigotApi.getManager()
        .register(
            "command",
            crateRewardConfig ->
                new CommandReward(crateRewardConfig.getMeta().getString("command").orElse("")) {
                  @Override
                  public ItemStack getIcon(UUID player) {
                    return crateRewardConfig.getIcon().getStack();
                  }

                  @Override
                  public String getName() {
                    return crateRewardConfig.getName();
                  }
                });

    try {
      loadDatabase();
    } catch (Exception e) {
      getLogger().warning("Error getting info about database, using memory one");
      TCratesAPI.setKeyStorage(new MemoryKeyStorage());
    }

    for (World world : Bukkit.getWorlds()) {
      getLogger().severe("World: " + world);
    }
    loadFeatures();
    try {
      loadCases();
    } catch (IOException e) {
      getLogger().warning("Error loading crates: " + e.getMessage());
      e.printStackTrace();
    }

    TCratesSpigotApi.setGrantHandler(new AutoGrantHandler());
    Objects.requireNonNull(getCommand("crate")).setExecutor(new CrateCommand());

    if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
      new CrateExpansion().register();
    }

    getLogger().info("Loading cooldown feature...");
    getServer().getPluginManager().registerEvents(new CooldownListener(), this);
    getLogger().info("Loading announce feature...");
    getServer().getPluginManager().registerEvents(new AnnounceListener(), this);
    getLogger().info("Loading group merge feature...");
    getServer().getPluginManager().registerEvents(new GroupMergeListener(), this);
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
    TCratesSpigotApi.getGrantHandler().getTask().cancel();
  }

  public static Logger getPluginLogger() {
    return plugin.getLogger();
  }
}
