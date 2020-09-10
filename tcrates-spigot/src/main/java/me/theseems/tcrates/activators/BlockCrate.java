package me.theseems.tcrates.activators;

import fr.mrmicky.fastparticle.FastParticle;
import fr.mrmicky.fastparticle.ParticleType;
import me.theseems.tcrates.Crate;
import me.theseems.tcrates.TCratesAPI;
import me.theseems.tcrates.TCratesPlugin;
import me.theseems.tcrates.events.CrateCloseEvent;
import me.theseems.tcrates.events.CrateOpenEvent;
import me.theseems.tcrates.utils.Utils;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class BlockCrate implements Listener, Runnable {
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

  @EventHandler(priority = EventPriority.MONITOR)
  public void onClose(CrateCloseEvent e) {
    System.out.println(
        "Crate close : " + e.getCrateName() + " " + e.getRewards() + " for " + e.getPlayer());
    inUse.remove(e.getCrateName());
  }

  @EventHandler(priority = EventPriority.MONITOR)
  public void onOpen(CrateOpenEvent e) {
    if (!e.isCancelled()
        && crates.containsValue(e.getCrateName())
        && !inUse.containsKey(e.getCrateName())) {
      System.out.println(
          "Crate open: "
              + e.getPlayerUUID()
              + " ("
              + e.getPlayer().getName()
              + ")"
              + " "
              + e.getCrateName()
              + " "
              + e.isCancelled());
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
        e.getPlayer()
            .sendMessage(
                "§7Кейс уже открывается игроком §6"
                    + inUse.get(crates.get(location)).getDisplayName());
        return;
      }

      String crateName = crates.get(location);
      Optional<Crate> optionalCrate = TCratesAPI.getCrateManager().find(crateName);

      if (optionalCrate.isPresent()) {
        Crate crate = optionalCrate.get();
        if (crate.getMeta().getString("on_open_command").isPresent()) {
          e.getPlayer().chat("/" + crate.getMeta().getString("on_open_command").get());
          return;
        }

        if (crate.getRequirements().canOpen(e.getPlayer().getUniqueId())) {
          crate.open(e.getPlayer().getUniqueId());
        } else {
          e.getPlayer()
              .sendMessage(
                  crate
                      .getMeta()
                      .getString("no_access_message")
                      .orElse("§cВы не можете открыть этот кейс! Недостаточно ключей!"));
          crate.getMeta().getString("no_access_command").ifPresent(s -> e.getPlayer().chat(s));
        }
      } else {
        TCratesPlugin.getPluginLogger()
            .warning(
                "There is a block associated with a crate "
                    + crateName
                    + " but it cannot be found");
      }
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

    FastParticle.spawnParticle(location.getWorld(), ParticleType.FLAME, location, 50);
  }

  public void scan() {
    for (String crate : TCratesAPI.getCrateManager().getCrates()) {
      @SuppressWarnings("OptionalGetWithoutIsPresent")
      Crate actual = TCratesAPI.getCrateManager().find(crate).get();
      actual
          .getMeta()
          .getString("block-crate-location")
          .ifPresent(
              s -> {
                Optional<Location> d = Utils.fromString(s);
                if (d.isPresent()) {
                  Location location = d.get();
                  if (!crates.containsKey(location)) register(crate, location);
                } else
                  TCratesPlugin.getPluginLogger()
                      .warning("Invalid location meta for crate " + crate + ": " + s);
              });
    }
  }

  public void unregister(Location location) {
    crates.remove(location);
    FastParticle.spawnParticle(location.getWorld(), ParticleType.FLAME, location, 10);
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

  @Override
  public void run() {

  }
}
