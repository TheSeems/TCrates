package me.theseems.tcrates;

import java.util.UUID;

public interface KeyStorage {
  /**
   * Get key count for player and crate
   * @param player to get keys of
   * @param crateName to get keys for
   * @return key count
   */
  int getKeysFor(UUID player, String crateName);

  /**
   * Set key count for player and crate
   * @param player to set keys of
   * @param crateName to set keys for
   * @param count of keys
   */
  void setKeysFor(UUID player, String crateName, int count);
}
