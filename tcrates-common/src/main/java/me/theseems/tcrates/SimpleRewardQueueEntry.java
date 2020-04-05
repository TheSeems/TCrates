package me.theseems.tcrates;

import me.theseems.tcrates.rewards.CrateReward;
import me.theseems.tcrates.rewards.CrateRewardContainer;
import me.theseems.tcrates.rewards.RewardQueueEntry;

import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

public class SimpleRewardQueueEntry implements RewardQueueEntry {
  private int index;
  private UUID player;
  private String crateName;
  private boolean isGranted;

  public SimpleRewardQueueEntry(int index, UUID player, String crateName) {
    this.index = index;
    this.player = player;
    this.crateName = crateName;
  }

  public SimpleRewardQueueEntry(int index, UUID player, String crateName, boolean isGranted) {
    this.index = index;
    this.player = player;
    this.crateName = crateName;
    this.isGranted = isGranted;
  }

  @Override
  public Optional<CrateReward> take() {
    Optional<Crate> crate = TCratesAPI.getCrateManager().find(crateName);
    if (!crate.isPresent())
      return Optional.empty();
    CrateRewardContainer container = crate.get().getRewardContainer();
    return container.find(index);
  }

  @Override
  public int getIndex() {
    return index;
  }

  public void setIndex(int index) {
    this.index = index;
  }

  @Override
  public UUID getPlayer() {
    return player;
  }

  public void setPlayer(UUID player) {
    this.player = player;
  }

  @Override
  public String getCrateName() {
    return crateName;
  }

  public void setCrateName(String crateName) {
    this.crateName = crateName;
  }

  @Override
  public boolean isGranted() {
    return isGranted;
  }

  public void setGranted(boolean granted) {
    isGranted = granted;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof SimpleRewardQueueEntry)) return false;
    SimpleRewardQueueEntry that = (SimpleRewardQueueEntry) o;
    return getIndex() == that.getIndex() &&
      isGranted() == that.isGranted() &&
      Objects.equals(getPlayer(), that.getPlayer()) &&
      Objects.equals(getCrateName(), that.getCrateName());
  }

  @Override
  public int hashCode() {
    return Objects.hash(getIndex(), getPlayer(), getCrateName(), isGranted());
  }
}
