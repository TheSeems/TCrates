package me.theseems.tcrates.rewards;

import me.theseems.tcrates.Crate;

import java.util.Collection;
import java.util.UUID;

public interface RewardQueue {
  /**
   * Put entry
   *
   * @param granted to add
   */
  void put(UUID player, String crateName, int index, boolean granted);

  /**
   * Remove entry
   *
   * @param player to remove
   * @param crateName to remove
   * @param index to remove
   */
  void remove(UUID player, String crateName, int index);

  void remove(RewardQueueEntry entry);

  default void generateAndPut(Crate crate, UUID player) {
    putAll(player, crate.getName(), crate.getRewardContainer().generate(player));
  }

  default void putAll(UUID player, String crateName, Collection<Integer> collection) {
    for (Integer integer : collection) {
      put(player, crateName, integer, false);
    }
  }

  /**
   * Get all not granted rewards
   *
   * @return not granted rewards
   */
  Collection<RewardQueueEntry> getNotGranted();
}
