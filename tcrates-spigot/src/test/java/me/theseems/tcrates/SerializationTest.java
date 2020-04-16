package me.theseems.tcrates;

import com.google.gson.GsonBuilder;
import me.theseems.tcrates.config.CrateConfig;
import me.theseems.tcrates.config.CrateRewardConfig;
import me.theseems.tcrates.config.RewardIconConfig;
import me.theseems.tcrates.config.TCratesConfig;
import me.theseems.tcrates.rewards.CrateReward;
import me.theseems.tcrates.rewards.MoneyReward;
import org.bukkit.inventory.ItemStack;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class SerializationTest {
  @Test
  public void test() throws IOException {
    MemoryCrateMeta memoryCrateMeta = new MemoryCrateMeta();
    memoryCrateMeta.set("example", "anime is my love");
    memoryCrateMeta.set("test", true);

    CrateRewardConfig crateRewardConfig =
        new CrateRewardConfig(
            10,
            "Anime",
            new RewardIconConfig("STONE", "Welcome", new String[] {"first", "second", "third"}),
            memoryCrateMeta,
            "none");
    List<CrateRewardConfig> crateRewardConfigList = new ArrayList<>();

    for (int i = 0; i < 3; i++) crateRewardConfigList.add(crateRewardConfig);

    CrateConfig config =
        new CrateConfig(memoryCrateMeta, crateRewardConfigList, "example", "circle", false);

    TCratesConfig config1 =
        new TCratesConfig(
            new ArrayList<>() {
              {
                add(config);
              }
            });

    String kek = new GsonBuilder().setPrettyPrinting().create().toJson(config1);

    System.out.println(kek);
    TCratesConfig crateConfig =
        new GsonBuilder().setPrettyPrinting().create().fromJson(kek, TCratesConfig.class);
    System.out.println(crateConfig);
  }

  @Test
  public void testCrate() {
    ProbabilityRewardContainer probabilityRewardContainer = new ProbabilityRewardContainer();
    SimpleCrate crate = new SimpleCrate("example");
    CrateReward reward =
        new MoneyReward(100) {
          @Override
          public ItemStack getIcon(UUID player) {
            return null;
          }

          @Override
          public String getName() {
            return "money-reward";
          }
        };

    reward.getMeta().set("type", "money");
    probabilityRewardContainer.addReward(100, reward);
    crate.setRewardContainer(probabilityRewardContainer);

    TCratesAPI.setCrateManager(new SimpleCrateManager());
    MemoryCrateMeta memoryCrateMeta = new MemoryCrateMeta();
    memoryCrateMeta.set("exmaple-1", 111);
    memoryCrateMeta.set("example-2", "anime");
    crate.setCrateMeta(memoryCrateMeta);

    System.out.println(
        new GsonBuilder().setPrettyPrinting().create().toJson(crate));
  }
}
