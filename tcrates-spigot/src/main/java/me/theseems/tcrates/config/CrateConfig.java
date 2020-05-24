package me.theseems.tcrates.config;

import me.theseems.tcrates.*;
import me.theseems.tcrates.animations.CircleCrateAnimation;
import me.theseems.tcrates.api.TCratesSpigotApi;
import me.theseems.tcrates.requirements.KeyRequirement;
import me.theseems.tcrates.rewards.CrateReward;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

public class CrateConfig {
  private MemoryCrateMeta meta;
  private List<CrateRewardConfig> rewardList;
  private String name;
  private String animation;

  public CrateConfig(
      MemoryCrateMeta meta,
      List<CrateRewardConfig> rewardList,
      String name,
      String animation,
      boolean needKeys) {
    this.meta = meta;
    this.rewardList = rewardList;
    this.name = name;
    this.animation = animation;
  }

  public Crate makeCrate() {
    if (!Objects.equals(animation, "circle")) {
      throw new IllegalStateException("Animations other than 'circle' are not supported");
    }

    CrateAnimation animation = new CircleCrateAnimation();
    SimpleCrate crate = new SimpleCrate(name);

    ProbabilityRewardContainer rewardContainer = new ProbabilityRewardContainer();
    for (CrateRewardConfig crateRewardConfig : rewardList) {
      Optional<CrateReward> reward = TCratesSpigotApi.getManager().make(crateRewardConfig);
      if (!reward.isPresent()) continue;

      rewardContainer.addReward(crateRewardConfig.getProbability(), reward.get());
    }

    crate.setRewardContainer(rewardContainer);
    crate.setAnimation(animation);
    crate.setCrateMeta(meta);

    crate.setRequirements(new KeyRequirement(name));

    return crate;
  }

  public MemoryCrateMeta getMeta() {
    return meta;
  }

  public void setMeta(MemoryCrateMeta meta) {
    this.meta = meta;
  }

  public List<CrateRewardConfig> getRewardList() {
    return rewardList;
  }

  public void setRewardList(List<CrateRewardConfig> rewardList) {
    this.rewardList = rewardList;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getAnimation() {
    return animation;
  }

  public void setAnimation(String animation) {
    this.animation = animation;
  }
}
