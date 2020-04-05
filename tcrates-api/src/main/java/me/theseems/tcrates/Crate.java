package me.theseems.tcrates;

import me.theseems.tcrates.rewards.CrateRewardContainer;

import java.util.Collection;
import java.util.UUID;

public interface Crate {
  /**
   * Get reward container
   *
   * @return container
   */
  CrateRewardContainer getRewardContainer();

  /**
   * Get all players there are
   *
   * @return player container
   */
  Collection<UUID> getPlayers();

  /**
   * Get crate animation
   *
   * @return animation
   */
  CrateAnimation getAnimation();

  /**
   * Get crate meta
   *
   * @return meta
   */
  CrateMeta getMeta();

  /**
   * Get requirements
   *
   * @return requirements
   */
  CrateRequirements getRequirements();

  /**
   * Close crate with rewards
   *
   * @param player to close for
   * @param rewards to give
   */
  void close(UUID player, Collection<Integer> rewards);

  /**
   * Open a crate for player
   *
   * @param player to open for
   */
  void open(UUID player);

  /**
   * Get name of the crate
   *
   * @return name
   */
  String getName();
}
