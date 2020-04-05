package me.theseems.tcrates.events;

import me.theseems.tcrates.Crate;
import me.theseems.tcrates.TCratesAPI;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import java.util.Optional;
import java.util.UUID;

public class CrateOpenEvent extends Event implements Cancellable {
  private static final HandlerList handlers = new HandlerList();
  private UUID player;
  private String crateName;
  private boolean isCancelled;

  public CrateOpenEvent(UUID player, String crateName) {
    this.player = player;
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
