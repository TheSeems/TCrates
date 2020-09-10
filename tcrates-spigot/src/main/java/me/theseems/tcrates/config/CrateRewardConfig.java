package me.theseems.tcrates.config;

import me.theseems.tcrates.MemoryCrateMeta;

public class CrateRewardConfig {
  private String name;
  private String type;
  private Double probability;
  private RewardIconConfig icon;
  private MemoryCrateMeta meta;
  private RewardIconConfig other;

  public CrateRewardConfig(
      Double probability, String name, RewardIconConfig icon, MemoryCrateMeta meta, String type) {
    this.probability = probability;
    this.name = name;
    this.icon = icon;
    this.meta = meta;
    this.type = type;
  }

  public CrateRewardConfig(
          Double probability, String name, RewardIconConfig icon, MemoryCrateMeta meta, String type, RewardIconConfig other) {
    this.probability = probability;
    this.name = name;
    this.icon = icon;
    this.meta = meta;
    this.type = type;
    this.other = other;
  }

  public Double getProbability() {
    return probability;
  }

  public void setProbability(Double probability) {
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

  public RewardIconConfig getOther() {
    return other;
  }

  public void setOther(RewardIconConfig other) {
    this.other = other;
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
