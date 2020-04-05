package me.theseems.tcrates.rewards;

import org.bukkit.inventory.ItemStack;

import java.util.UUID;

public interface IconReward extends CrateReward {
  /**
   * Get icon for player
   * @param player to see
   * @return icon
   */
  ItemStack getIcon(UUID player);
}
