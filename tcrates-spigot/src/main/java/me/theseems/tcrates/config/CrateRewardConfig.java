package me.theseems.tcrates.config;

import me.theseems.tcrates.TCratesPlugin;
import me.theseems.tcrates.rewards.CrateReward;
import me.theseems.tcrates.rewards.GroupReward;
import me.theseems.tcrates.rewards.MoneyReward;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.UUID;

public class CrateRewardConfig {
  private int probability;
  private String name;
  private String material;
  private String group;
  private String server;
  private double money;
  private String type;

  public CrateRewardConfig(
    int probability,
    String name,
    String material,
    String group,
    String server,
    Double money,
    String type) {
    this.probability = probability;
    this.name = name;
    this.material = material;
    this.group = group;
    this.server = server;
    this.money = money;
    this.type = type;

    if (server == null) {
      server = "inst";
    }
  }

  public CrateReward formReward() {
    Material mat = Material.matchMaterial(material);
    if (mat == null) {
      TCratesPlugin.getPluginLogger()
        .warning("No such material: '" + material + "' for crate " + name + " in config");
      mat = Material.BEDROCK;
    }

    ItemStack stack = new ItemStack(mat);
    ItemMeta meta = stack.getItemMeta();
    assert meta != null;
    meta.setDisplayName(name);
    stack.setItemMeta(meta);

    if (type.equals("group")) {
      return new GroupReward(group, server) {
        @Override
        public ItemStack getIcon(UUID player) {
          return stack;
        }
      };
    } else if (type.equals("money")) {
      return new MoneyReward(money) {
        @Override
        public String getName() {
          return "⛃ " + money;
        }

        @Override
        public ItemStack getIcon(UUID player) {
          return stack;
        }
      };
    }
    return null;
  }

  public int getProbability() {
    return probability;
  }

  public void setProbability(int probability) {
    this.probability = probability;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getMaterial() {
    return material;
  }

  public void setMaterial(String material) {
    this.material = material;
  }

  public String getGroup() {
    return group;
  }

  public void setGroup(String group) {
    this.group = group;
  }

  public String getServer() {
    return server;
  }

  public void setServer(String server) {
    this.server = server;
  }

  public double getMoney() {
    return money;
  }

  public String getType() {
    return type;
  }
}
