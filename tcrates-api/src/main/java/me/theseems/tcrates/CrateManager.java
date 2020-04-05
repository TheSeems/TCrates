package me.theseems.tcrates;

import java.util.Collection;
import java.util.Optional;

public interface CrateManager {
  /**
   * Register crate to manager
   *
   * @param crate to register
   */
  void register(Crate crate);

  /**
   * Unregister crate by name
   *
   * @param name to unregister
   */
  void unregister(String name);

  /**
   * Get crates there are
   *
   * @return crates
   */
  Collection<String> getCrates();

  /**
   * Find crate by name
   *
   * @param name to find
   * @return optional crate
   */
  Optional<Crate> find(String name);

  /**
   * Get crate by name
   *
   * @param name to get
   * @return crate or null if not found
   */
  Crate get(String name);
}
