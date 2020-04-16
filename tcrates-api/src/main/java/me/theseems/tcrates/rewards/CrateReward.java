package me.theseems.tcrates.rewards;

import me.theseems.tcrates.CrateMeta;

import java.util.UUID;

public interface CrateReward {
  /**
   * Give reward to player
   *
   * @param player receiver
   */
  void give(UUID player);

  /**
   * Get name of reward
   *
   * @return name
   */
  String getName();

  CrateMeta getMeta();

  void setMeta(CrateMeta meta);
}
