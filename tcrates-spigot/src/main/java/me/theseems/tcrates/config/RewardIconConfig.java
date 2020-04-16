package me.theseems.tcrates.config;

import me.theseems.tcrates.TCratesPlugin;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;

public class RewardIconConfig {
  private String material;
  private String displayName;
  private String[] lore;

  public ItemStack getStack() {
    Material material;
    material = Material.matchMaterial(this.material);
    if (material == null) {
      TCratesPlugin.getPluginLogger().warning("No such material: '" + this.material + "'");
      material = Material.OAK_BUTTON;
    }

    ItemStack stack = new ItemStack(material);
    ItemMeta meta = stack.getItemMeta();

    assert meta != null;
    meta.setDisplayName(displayName);

    meta.setLore(Arrays.asList(lore));
    stack.setItemMeta(meta);

    return stack;
  }

  public static RewardIconConfig from(ItemStack stack) {
    if (stack == null)
      return null;

    System.out.println("Stack = " + stack);
    RewardIconConfig iconConfig = new RewardIconConfig();
    iconConfig.material = stack.getType().toString();
    if (stack.getItemMeta() != null) {
      iconConfig.displayName = stack.getItemMeta().getDisplayName();
      iconConfig.lore = stack.getItemMeta().getLore().toArray(new String[0]);
    }
    return iconConfig;
  }

  public RewardIconConfig(String material, String displayName, String[] lore) {
    this.material = material;
    this.displayName = displayName;
    this.lore = lore;
  }

  public RewardIconConfig() {
  }
}
