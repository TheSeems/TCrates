package me.theseems.tcrates;

import java.util.UUID;

public interface CrateAnimation {
  /**
   * Animate crate for player
   *
   * @param player to animate for
   * @param crate to open
   */
  void open(UUID player, Crate crate);
}
