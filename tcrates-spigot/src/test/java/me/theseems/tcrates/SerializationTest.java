package me.theseems.tcrates;

import com.google.gson.GsonBuilder;
import me.theseems.tcrates.config.CrateConfig;
import me.theseems.tcrates.config.CrateRewardConfig;
import me.theseems.tcrates.config.TCratesConfig;
import org.bukkit.Material;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class SerializationTest {
  @Test
  public void test() throws IOException {
    MemoryCrateMeta memoryCrateMeta = new MemoryCrateMeta();
    memoryCrateMeta.set("example", "anime is my love");

    CrateRewardConfig crateRewardConfig = new CrateRewardConfig(10, "Anime", Material.REDSTONE.name(), "owner", "inst", 0D, "group");
    List<CrateRewardConfig> crateRewardConfigList = new ArrayList<>();

    for (int i = 0; i < 3; i++)
    crateRewardConfigList.add(crateRewardConfig);

    CrateConfig config = new CrateConfig(memoryCrateMeta, crateRewardConfigList, "example", "circle", false);

    TCratesConfig config1 = new TCratesConfig(new ArrayList<>(){{
      add(config);
    }});


    String kek = new GsonBuilder().setPrettyPrinting().create().toJson(config1);

    System.out.println(kek);
    TCratesConfig crateConfig = new GsonBuilder().setPrettyPrinting().create().fromJson(kek, TCratesConfig.class);
    System.out.println(crateConfig);
  }
}
