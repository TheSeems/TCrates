package me.theseems.tcrates.animations;

import com.gmail.filoghost.holographicdisplays.api.Hologram;
import fr.mrmicky.fastparticle.FastParticle;
import fr.mrmicky.fastparticle.ParticleType;
import me.theseems.tcrates.Crate;
import me.theseems.tcrates.CrateAnimation;
import me.theseems.tcrates.TCratesAPI;
import me.theseems.tcrates.TCratesPlugin;
import me.theseems.tcrates.animations.circle.CircleRoll;
import me.theseems.tcrates.utils.Utils;
import org.bukkit.*;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

public class CircleCrateAnimation implements CrateAnimation, Listener {

  static class GrabStorage {
    private final Queue<Integer> integers;
    private final Collection<Integer> original;

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

  private final Map<UUID, GrabStorage> rewardMap;
  private final Map<UUID, CircleRoll> players;

  public CircleCrateAnimation() {
    players = new ConcurrentHashMap<>();
    rewardMap = new ConcurrentHashMap<>();
    Bukkit.getScheduler().runTaskTimer(TCratesPlugin.getPlugin(), this::run, 2, 2);
  }

  private void generateState(CircleRoll circleRoll) {
    Crate crate = TCratesAPI.getCrateManager().get(circleRoll.getCrateName());
    rewardMap.put(
        circleRoll.getPlayer(),
        new GrabStorage(
            crate
                .getRewardContainer()
                .generate(circleRoll.getPlayer(), circleRoll.getCrate().getMeta())));
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
      FastParticle.spawnParticle(
          stand.getLocation().getWorld(),
          ParticleType.REDSTONE,
          stand.getLocation(),
          5,
          Color.fromRGB(0, 0, 0));
      stand.delete();
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
    for (int i = 0; i < hologram.size() + 1; i++) {
      hologram.removeLine(0);
    }

    hologram.appendItemLine(stack);
    hologram.appendTextLine(stack.getItemMeta().getDisplayName());
    hologram.appendTextLine(
        TCratesAPI.getCrateManager()
            .find(circleRoll.getCrateName())
            .flatMap(crate -> crate.getMeta().getString("win_line"))
            .orElse("§6Выигрыш!"));

    TCratesAPI.getCrateManager()
        .find(circleRoll.getCrateName())
        .ifPresent(
            crate -> {
              boolean isHere = crate.getMeta().get("geffesht").isPresent();
              if (isHere) {
                crate
                    .getMeta()
                    .set(
                        "geffesht",
                        crate.getMeta().get("geffesht").get()
                            + " "
                            + stack.getItemMeta().getDisplayName());
              } else crate.getMeta().set("geffesht", stack.getItemMeta().getDisplayName());
            });
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
    crate.getMeta().set("geffesht", "");

    if (crate.getMeta().getKeys().contains("firework")) {
      for (int i = 0; i < 3; i++) {
        Firework firework =
            (Firework) actual.getWorld().spawnEntity(location[0], EntityType.FIREWORK);
        FireworkMeta meta = firework.getFireworkMeta();
        meta.setPower(crate.getMeta().getInteger("firework-power").orElse(0));
        meta.addEffect(
            FireworkEffect.builder()
                .with(
                    FireworkEffect.Type.valueOf(
                        crate.getMeta().getString("firework-shape").orElse("BURST")))
                .trail(crate.getMeta().get("firework-trail").isPresent())
                .flicker(crate.getMeta().get("firework-flicker").isPresent())
                .withFade(
                    Color.fromRGB(
                        crate.getMeta().getInteger("firework-fade-red").orElse(0),
                        crate.getMeta().getInteger("firework-fade-green").orElse(0),
                        crate.getMeta().getInteger("firework-fade-blue").orElse(0)))
                .withColor(
                    Color.fromRGB(
                        crate.getMeta().getInteger("firework-red").orElse(0),
                        crate.getMeta().getInteger("firework-green").orElse(0),
                        crate.getMeta().getInteger("firework-blue").orElse(0)))
                .build());
        firework.setFireworkMeta(meta);
      }
    }
  }
}
