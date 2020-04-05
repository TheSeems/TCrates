package me.theseems.tcrates.rewards;

import java.util.Optional;
import java.util.UUID;

public interface RewardQueueEntry {
  /**
   * Try to take reward
   * @return optional of reward
   */
  Optional<CrateReward> take();

  /**
   * Get index in crate
   * @return index
   */
  int getIndex();

  /**
   * Get winner of the reward
   * @return player getting reward
   */
  UUID getPlayer();

  /**
   * Was the reward granted to a player
   * @return granted
   */
  boolean isGranted();

  /**
   * Get crate name
   * @return crate name
   */
  String getCrateName();
}
