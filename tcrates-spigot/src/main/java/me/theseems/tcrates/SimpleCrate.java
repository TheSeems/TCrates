package me.theseems.tcrates;

import me.theseems.tcrates.events.CrateCloseEvent;
import me.theseems.tcrates.events.CrateOpenEvent;
import me.theseems.tcrates.requirements.KeyRequirement;
import me.theseems.tcrates.rewards.CrateRewardContainer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.UUID;

public class SimpleCrate implements Crate {
  private CrateRewardContainer rewardContainer;
  private CrateAnimation animation;
  private String name;
  private Collection<UUID> players;
  private MemoryCrateMeta crateMeta;
  private KeyRequirement requirements;

  public SimpleCrate(String name) {
    this.name = name;
    this.players = new ArrayList<>();
    this.crateMeta = new MemoryCrateMeta();
    this.requirements = new KeyRequirement(name);
  }

  public void setRewardContainer(CrateRewardContainer rewardContainer) {
    this.rewardContainer = rewardContainer;
  }

  public void setAnimation(CrateAnimation animation) {
    this.animation = animation;
  }

  @Override
  public CrateRewardContainer getRewardContainer() {
    return rewardContainer;
  }

  @Override
  public Collection<UUID> getPlayers() {
    return players;
  }

  @Override
  public CrateAnimation getAnimation() {
    return animation;
  }

  @Override
  public MemoryCrateMeta getMeta() {
    return crateMeta;
  }

  @Override
  public KeyRequirement getRequirements() {
    return requirements;
  }

  @Override
  public void close(UUID player, Collection<Integer> rewards) {
    players.remove(player);
    CrateCloseEvent event = new CrateCloseEvent(player, rewards, name);
    TCratesPlugin.getPlugin().getServer().getPluginManager().callEvent(event);

    if (!event.isCancelled()) {
      TCratesAPI.getRewardQueue().putAll(player, name, rewards);
      TCratesAPI.getKeyStorage().setKeysFor(player, name, TCratesAPI.getKeyStorage().getKeysFor(player, name) - 1);
    }
  }

  public void setCrateMeta(MemoryCrateMeta crateMeta) {
    this.crateMeta = crateMeta;
  }

  public void setRequirements(KeyRequirement requirements) {
    this.requirements = requirements;
  }

  @Override
  public void open(UUID player) {
    CrateOpenEvent event = new CrateOpenEvent(player, name);
    TCratesPlugin.getPlugin().getServer().getPluginManager().callEvent(event);

    if (!event.isCancelled()){
      players.add(player);
      animation.open(player, this);
    }
  }

  @Override
  public String getName() {
    return name;
  }
}
