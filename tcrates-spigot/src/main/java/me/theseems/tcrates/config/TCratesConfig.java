package me.theseems.tcrates.config;

import java.util.List;

public class TCratesConfig {
  private List<CrateConfig> crates;

  public TCratesConfig(List<CrateConfig> crates) {
    this.crates = crates;
  }

  public List<CrateConfig> getCrates() {
    return crates;
  }

  public void setCrates(List<CrateConfig> crates) {
    this.crates = crates;
  }
}
