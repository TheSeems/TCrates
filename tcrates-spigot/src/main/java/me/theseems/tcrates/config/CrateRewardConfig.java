package me.theseems.tcrates.config;

import me.theseems.tcrates.MemoryCrateMeta;

public class CrateRewardConfig {
  private int probability;
  private String name;
  private RewardIconConfig icon;
  private MemoryCrateMeta meta;
  private String type;

  public CrateRewardConfig(int probability, String name, RewardIconConfig icon, MemoryCrateMeta meta, String type) {
    this.probability = probability;
    this.name = name;
    this.icon = icon;
    this.meta = meta;
    this.type = type;
  }

  public int getProbability() {
    return probability;
  }

  public void setProbability(int probability) {
    this.probability = probability;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public RewardIconConfig getIcon() {
    return icon;
  }

  public void setIcon(RewardIconConfig icon) {
    this.icon = icon;
  }

  public MemoryCrateMeta getMeta() {
    return meta;
  }

  public void setMeta(MemoryCrateMeta meta) {
    this.meta = meta;
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }
}
