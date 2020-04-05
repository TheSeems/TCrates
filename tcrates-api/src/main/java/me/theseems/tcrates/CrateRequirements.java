package me.theseems.tcrates;

import java.util.UUID;

public interface CrateRequirements {
  /**
   * Can a player open crate
   *
   * @param player to check
   * @return verdict if player can open crate
   */
  boolean canOpen(UUID player);
}
