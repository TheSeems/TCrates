package me.theseems.tcrates.config;

import me.theseems.tcrates.TCratesPlugin;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class RewardIconConfig {
  private String material;
  private String displayName;
  private short durability;
  private int amount = 1;
  private Map<String, Integer> enchantments;
  private String[] lore;

  public ItemStack getStack() {
    Material material;
    material = Material.matchMaterial(this.material);
    if (material == null) {
      TCratesPlugin.getPluginLogger().warning("No such material: '" + this.material + "'");
      material = Material.STONE;
    }

    ItemStack stack = new ItemStack(material);
    if (enchantments != null)
      enchantments.forEach(
          (name, level) -> {
            if (name.toUpperCase().equals("SHARPNESS"))
              name = "DAMAGE_ALL";
            if (name.toUpperCase().equals("LOOTING"))
              name = "LOOT_BONUS_MOBS";
            Enchantment enchantment = Enchantment.getByName(name.toUpperCase());
            if (enchantment == null) {
              System.err.println("There's no enchantment with name '" + name + "'");
              return;
            }
            stack.addEnchantment(enchantment, level);
          });

    ItemMeta meta = stack.getItemMeta();
    stack.setDurability(durability);

    assert meta != null;
    meta.setDisplayName(displayName);

    if (lore != null) meta.setLore(Arrays.asList(lore));
    stack.setItemMeta(meta);
    stack.setAmount(amount);

    return stack;
  }

  public static RewardIconConfig from(ItemStack stack) {
    if (stack == null) return null;

    RewardIconConfig iconConfig = new RewardIconConfig();
    iconConfig.material = stack.getType().toString();
    if (stack.getItemMeta() != null) {
      iconConfig.displayName = stack.getItemMeta().getDisplayName();
      if (stack.getItemMeta().getLore() != null)
        iconConfig.lore = stack.getItemMeta().getLore().toArray(new String[0]);
    }

    if (!stack.getEnchantments().isEmpty()) iconConfig.enchantments = new HashMap<>();
    else iconConfig.enchantments = null;
    stack
        .getEnchantments()
        .forEach(
            (enchantment, integer) ->
                iconConfig.enchantments.put(enchantment.getName(), integer));

    iconConfig.durability = stack.getDurability();
    iconConfig.amount = stack.getAmount();
    return iconConfig;
  }

  public RewardIconConfig(String material, String displayName, short durability, String[] lore) {
    this.material = material;
    this.displayName = displayName;
    this.durability = durability;
    this.lore = lore;
  }

  public RewardIconConfig(
      String material,
      String displayName,
      short durability,
      String[] lore,
      Map<String, Integer> enchantments) {
    this.material = material;
    this.displayName = displayName;
    this.durability = durability;
    this.lore = lore;
    this.enchantments = enchantments;
  }

  public RewardIconConfig() {}
}
