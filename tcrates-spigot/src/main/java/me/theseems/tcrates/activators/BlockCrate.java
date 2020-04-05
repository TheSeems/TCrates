package me.theseems.tcrates.activators;

import me.theseems.tcrates.Crate;
import me.theseems.tcrates.TCratesAPI;
import me.theseems.tcrates.TCratesPlugin;
import me.theseems.tcrates.events.CrateCloseEvent;
import me.theseems.tcrates.events.CrateOpenEvent;
import me.theseems.tcrates.utils.Utils;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.util.Vector;

import java.util.*;

public class BlockCrate implements Listener {
  private Map<Location, String> crates;
  private Map<String, Player> inUse;

  public BlockCrate() {
    crates = new HashMap<>();
    inUse = new HashMap<>();
    TCratesPlugin.getPlugin()
        .getServer()
        .getPluginManager()
        .registerEvents(this, TCratesPlugin.getPlugin());
  }

  @EventHandler
  public void onClose(CrateCloseEvent e) {
    System.out.println(
        "Crate close : " + e.getCrateName() + " " + e.getRewards() + " for " + e.getPlayer());
    inUse.remove(e.getCrateName());
  }

  @EventHandler
  public void onOpen(CrateOpenEvent e) {
    if (!e.isCancelled() && crates.containsValue(e.getCrateName())) {
      inUse.put(e.getCrateName(), e.getPlayer());
    }
  }

  @EventHandler
  public void onClick(PlayerInteractEvent e) {
    if (e.getClickedBlock() == null) return;
    Location location = e.getClickedBlock().getLocation();
    if (crates.containsKey(location)) {

      e.setCancelled(true);
      if (inUse.containsKey(crates.get(location))) {
        e.getPlayer().sendMessage("§7Кейс уже открывается игроком §6" + inUse.get(crates.get(location)).getDisplayName());
        return;
      }

      String crateName = crates.get(location);
      TCratesAPI.getCrateManager()
          .find(crateName)
          .ifPresentOrElse(
              crate -> {
                if (crate.getRequirements().canOpen(e.getPlayer().getUniqueId())) {
                  crate.open(e.getPlayer().getUniqueId());
                } else {
                  e.getPlayer().sendMessage("§cВы не можете открыть этот кейс");
                }
              },
              () ->
                  TCratesPlugin.getPluginLogger()
                      .warning(
                          "There is a block associated with a crate "
                              + crateName
                              + " but it cannot be found"));
    }
  }

  public void register(String crateName, Location location) {
    if (crates.containsKey(location)) return;

    Optional<Crate> crateOptional = TCratesAPI.getCrateManager().find(crateName);
    if (!crateOptional.isPresent()) {
      throw new IllegalStateException("No create found under name '" + crateName + "'");
    }

    crateOptional.get().getMeta().set("block-crate-location", Utils.forLocation(location));

    crates.put(location, crateName);
    Objects.requireNonNull(location.getWorld())
        .spawnParticle(
            Particle.FLAME,
            location.clone().add(new Vector(0.5, 0.5, 0.5)),
            40,
            0.1,
            0.1,
            0.1,
            0.1);
  }

  public void scan() {
    for (String crate : TCratesAPI.getCrateManager().getCrates()) {
      @SuppressWarnings("OptionalGetWithoutIsPresent")
      Crate actual = TCratesAPI.getCrateManager().find(crate).get();
      actual
          .getMeta()
          .getString("block-crate-location")
          .ifPresent(
              s ->
                  Utils.fromString(s)
                      .ifPresentOrElse(
                          location -> {
                            if (!crates.containsKey(location)) register(crate, location);
                          },
                          () ->
                              TCratesPlugin.getPluginLogger()
                                  .warning("Invalid location meta for crate " + crate + ": " + s)));
    }
  }

  public void unregister(Location location) {
    crates.remove(location);
    Objects.requireNonNull(location.getWorld())
        .spawnParticle(
            Particle.BLOCK_CRACK, location.clone(), 40, location.clone().getBlock().getBlockData());
  }

  public String getOpener(String crateName) {
    if (inUse.containsKey(crateName))
      return inUse.get(crateName).getName() + " открывает прямо сейчас!";
    return "";
  }

  public void clear() {
    crates.clear();
    inUse.clear();
  }
}
