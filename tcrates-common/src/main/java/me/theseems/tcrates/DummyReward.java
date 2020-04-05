package me.theseems.tcrates;

import me.theseems.tcrates.rewards.CrateReward;

import java.util.UUID;
import java.util.logging.Logger;

public class DummyReward implements CrateReward {
  private String name;

  public DummyReward(String name) {
    this.name = name;
  }

  @Override
  public void give(UUID player) {
    Logger.getLogger("DummyReward").info(player + " received dummy reward '" + name + "'");
  }

  @Override
  public String getName() {
    return "Dummy<" + name + ">";
  }
}
