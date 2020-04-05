package me.theseems.tcrates.animations;

import com.gmail.filoghost.holographicdisplays.api.Hologram;
import me.theseems.tcrates.Crate;
import me.theseems.tcrates.CrateAnimation;
import me.theseems.tcrates.TCratesAPI;
import me.theseems.tcrates.TCratesPlugin;
import me.theseems.tcrates.animations.circle.CircleRoll;
import me.theseems.tcrates.events.CrateOpenEvent;
import me.theseems.tcrates.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

public class CircleCrateAnimation implements CrateAnimation, Listener {

  static class GrabStorage {
    private Queue<Integer> integers;
    private Collection<Integer> original;

    public Integer take() {
      if (isEmpty()) return null;
      return integers.poll();
    }

    public Collection<Integer> getOriginal() {
      return original;
    }

    public int size() {
      return integers.size();
    }

    public boolean isEmpty() {
      return integers.isEmpty();
    }

    public GrabStorage(Collection<Integer> original) {
      this.original = original;
      this.integers = new ArrayDeque<>();
      integers.addAll(original);
    }
  }

  private Map<UUID, GrabStorage> rewardMap;
  private Map<UUID, CircleRoll> players;

  public CircleCrateAnimation() {
    players = new ConcurrentHashMap<>();
    rewardMap = new ConcurrentHashMap<>();
    Bukkit.getScheduler().runTaskTimer(TCratesPlugin.getPlugin(), this::run, 2, 2);
    Bukkit.getServer().getPluginManager().registerEvents(this, TCratesPlugin.getPlugin());
  }

  @EventHandler
  public void onOpen(CrateOpenEvent e) {
    if (!e.isCancelled() && players.containsKey(e.getPlayerUUID())) {
      e.getPlayer().sendMessage("§7Пожалуйста, дождитесь окончания октрытия");
      e.setCancelled(true);
    }
  }

  private void generateState(CircleRoll circleRoll) {
    Crate crate = TCratesAPI.getCrateManager().get(circleRoll.getCrateName());
    rewardMap.put(
        circleRoll.getPlayer(),
        new GrabStorage(crate.getRewardContainer().generate(circleRoll.getPlayer())));
  }

  private void generateTick(CircleRoll circleRoll) {
    circleRoll.setRadius(
        circleRoll.getRadius()
            - (TCratesAPI.getCrateManager().get(circleRoll.getCrateName()))
                    .getMeta()
                    .getDouble("radius")
                    .orElse(3D)
                / 50);
  }

  private void removeState(CircleRoll circleRoll) {
    for (Hologram stand : circleRoll.getStands()) {
      stand.delete();
      Objects.requireNonNull(stand.getWorld())
          .spawnParticle(Particle.CLOUD, stand.getLocation(), 2, 0.1, 0.1, 0.1, 0.1);
    }

    circleRoll.getStands().clear();

    TCratesAPI.getCrateManager()
        .find(circleRoll.getCrateName())
        .ifPresent(
            crate ->
                crate.close(
                    circleRoll.getPlayer(), rewardMap.get(circleRoll.getPlayer()).getOriginal()));
    rewardMap.remove(circleRoll.getPlayer());
    players.remove(circleRoll.getPlayer());
  }

  private void appendItem(CircleRoll circleRoll, int index) {
    ItemStack stack =
        CircleRoll.createItemStack(
            circleRoll.getCrateName(),
            circleRoll.getPlayer(),
            rewardMap.get(circleRoll.getPlayer()).take());
    Hologram hologram = circleRoll.getStands().get(index);
    for (int i = 0; i < 2; i++) hologram.removeLine(0);

    hologram.appendItemLine(stack);
    hologram.appendTextLine(stack.getItemMeta().getDisplayName());
    hologram.appendTextLine(
        TCratesAPI.getCrateManager()
            .find(circleRoll.getCrateName())
            .flatMap(
                (Function<Crate, Optional<String>>) crate -> crate.getMeta().getString("win_line"))
            .orElse("§6Выигрыш!"));

    hologram
        .getLocation()
        .getWorld()
        .spawnParticle(
            Particle.REDSTONE,
            hologram.getLocation().clone(),
            1,
            0.01,
            0.01,
            0.01,
            0.001,
            new Particle.DustOptions(Color.ORANGE, 6));
  }

  private final int PULSE_PERIOD = 50;
  private final int GENERATE_TIME = 80;
  private final int GENERATE_PERIOD = 20;
  private final int CLOSE_DELAY = 30;

  public static int getIntProperty(CircleRoll roll, String name, int def) {
    return TCratesAPI.getCrateManager()
        .find(roll.getCrateName())
        .flatMap((Function<Crate, Optional<Integer>>) crate1 -> crate1.getMeta().getInteger(name))
        .orElse(def);
  }

  public static double getDoubleProperty(CircleRoll roll, String name, double def) {
    return TCratesAPI.getCrateManager()
      .find(roll.getCrateName())
      .flatMap((Function<Crate, Optional<Double>>) crate1 -> crate1.getMeta().getDouble(name))
      .orElse(def);
  }

  public void run() {
    players.forEach(
        (uuid, circleRoll) -> {
          int pulsePeriod = getIntProperty(circleRoll, "pulse_period", PULSE_PERIOD);
          int generateTime = getIntProperty(circleRoll, "generate_time", GENERATE_TIME);
          int generatePeriod = getIntProperty(circleRoll, "generate_period", GENERATE_PERIOD);
          int closeDelay = getIntProperty(circleRoll, "close_delay", CLOSE_DELAY);

          if (circleRoll.getTicks() % pulsePeriod == 0) {
            circleRoll.setColor(CircleRoll.getRandomColor());
          }

          if (circleRoll.getTicks() == generateTime) {
            generateState(circleRoll);
          }

          if (rewardMap.containsKey(uuid)
              && circleRoll.getTicks()
                  > generateTime + generatePeriod * rewardMap.get(uuid).getOriginal().size()) {

            if (circleRoll.getTicks()
                > generateTime
                    + closeDelay
                    + generatePeriod * rewardMap.get(uuid).getOriginal().size()) {
              removeState(circleRoll);
            }

            generateTick(circleRoll);
          }

          if (rewardMap.containsKey(uuid)
              && circleRoll.getTicks() > generateTime
              && (circleRoll.getTicks() - generateTime) % generatePeriod == 0) {
            if (!rewardMap.get(uuid).isEmpty()) {
              int index = (circleRoll.getTicks() - generateTime) / generatePeriod;
              index %= circleRoll.getSize();
              appendItem(circleRoll, index);
            } else {
              circleRoll.setTicks(circleRoll.getTicks() + 1);
              return;
            }
          }

          circleRoll.draw();
          circleRoll.setTicks(circleRoll.getTicks() + 1);
        });
  }

  @Override
  public void open(UUID player, Crate crate) {
    Player actual = Bukkit.getPlayer(player);
    if (actual == null) return;

    final Location[] location = {actual.getLocation()};
    crate
        .getMeta()
        .getString("block-crate-location")
        .flatMap(Utils::fromString)
        .ifPresent(blockLocation -> location[0] = blockLocation.clone().add(0.5, 0.5, 0.5));

    CircleRoll roll =
        new CircleRoll(
            location[0],
            actual.getUniqueId(),
            crate.getName(),
            crate.getMeta().getDouble("radius").orElse(3D),
            crate.getMeta().getInteger("size").orElse(8));
    roll.spawn();

    players.put(actual.getUniqueId(), roll);
  }
}
