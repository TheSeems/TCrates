package me.theseems.tcrates;

import me.theseems.tcrates.rewards.CrateReward;

import java.util.UUID;
import java.util.logging.Logger;

public class DummyReward implements CrateReward {
  private String name;
  private MemoryCrateMeta meta;

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

  public void setName(String name) {
    this.name = name;
  }

  @Override
  public CrateMeta getMeta() {
    return meta;
  }

  @Override
  public void setMeta(CrateMeta meta) {
    this.meta = MemoryCrateMeta.to(meta);
  }
}
