package me.theseems.tcrates;

import me.theseems.tcrates.rewards.RewardQueue;
import me.theseems.tcrates.rewards.RewardQueueEntry;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

public class MemoryRewardQueue implements RewardQueue {
  private CopyOnWriteArrayList<RewardQueueEntry> entries;

  public MemoryRewardQueue() {
    entries = new CopyOnWriteArrayList<>();
  }

  @Override
  public void put(UUID player, String crateName, int index, boolean granted) {
    System.out.println("Entities : " + entries);
    if (granted) {
      entries.remove(new SimpleRewardQueueEntry(index, player, crateName, false));
    } else {
      entries.add(new SimpleRewardQueueEntry(index, player, crateName, false));
    }
  }

  @Override
  public void remove(UUID player, String crateName, int index) {
    entries.removeIf(
        entry ->
            entry.getPlayer() == player
                && entry.getCrateName().equals(crateName)
                && entry.getIndex() == index);
  }

  @Override
  public void remove(RewardQueueEntry entry) {
    entries.remove(entry);
  }

  @Override
  public Collection<RewardQueueEntry> getNotGranted() {
    return Collections.unmodifiableList(entries);
  }
}
