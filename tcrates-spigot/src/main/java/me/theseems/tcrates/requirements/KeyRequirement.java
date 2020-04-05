package me.theseems.tcrates.requirements;

import me.theseems.tcrates.CrateRequirements;
import me.theseems.tcrates.TCratesAPI;

import java.util.UUID;

public class KeyRequirement implements CrateRequirements {
  private String crate;

  public KeyRequirement(String crate) {
    this.crate = crate;
  }

  @Override
  public boolean canOpen(UUID player) {
    return TCratesAPI.getKeyStorage().getKeysFor(player, crate) > 0;
  }
}
