package me.theseems.tcrates.events;

import me.theseems.tcrates.Crate;
import me.theseems.tcrates.TCratesAPI;
import me.theseems.tcrates.rewards.CrateReward;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import java.util.*;

public class CrateCloseEvent extends Event implements Cancellable {
  private static final HandlerList handlers = new HandlerList();
  private UUID player;
  private Collection<Integer> rewards;
  private String crateName;
  private boolean isCancelled;

  public CrateCloseEvent(UUID player, Collection<Integer> rewards, String crateName) {
    this.player = player;
    this.rewards = rewards;
    this.crateName = crateName;
    this.isCancelled = false;
  }

  public UUID getPlayerUUID() {
    return player;
  }

  public Player getPlayer() throws NullPointerException {
    return Bukkit.getPlayer(player);
  }

  public Optional<Crate> getCrate() {
    return TCratesAPI.getCrateManager().find(crateName);
  }

  public Collection<CrateReward> getExactRewards() {
    List<CrateReward> rewardList = new ArrayList<>();
    getCrate()
        .ifPresent(
            crate -> {
              for (Integer index : rewards) {
                crate.getRewardContainer().find(index).ifPresent(rewardList::add);
              }
            });

    return rewardList;
  }

  public Collection<Integer> getRewards() {
    return rewards;
  }

  public String getCrateName() {
    return crateName;
  }

  @Override
  public HandlerList getHandlers() {
    return handlers;
  }

  public static HandlerList getHandlerList() {
    return handlers;
  }

  @Override
  public boolean isCancelled() {
    return isCancelled;
  }

  @Override
  public void setCancelled(boolean b) {
    isCancelled = b;
  }
}
