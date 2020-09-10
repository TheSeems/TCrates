package me.theseems.tcrates.rewards;

import me.theseems.tcrates.CrateMeta;

import java.util.Collection;
import java.util.Optional;
import java.util.UUID;

public interface CrateRewardContainer {
  /**
   * Generate rewards for players
   *
   * @param player to generate for
   * @return rewards
   */
  Collection<Integer> generate(UUID player);

  /**
   * Generate rewards for players
   *
   * @param player to generate for
   * @param crateMeta to generate for
   * @return rewards
   */
  Collection<Integer> generate(UUID player, CrateMeta crateMeta);

  /**
   * Grab subset of rewards
   *
   * @param limit count of rewards
   * @return subset of rewards
   */
  Collection<Integer> grab(int limit);

  /**
   * Get reward by index
   *
   * @param index to get
   * @return reward
   */
  Optional<CrateReward> find(int index);

  /**
   * Return size of container
   * @return size
   */
  int size();
}
