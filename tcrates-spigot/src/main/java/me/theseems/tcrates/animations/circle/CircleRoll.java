package me.theseems.tcrates.animations.circle;

import com.gmail.filoghost.holographicdisplays.api.Hologram;
import com.gmail.filoghost.holographicdisplays.api.HologramsAPI;
import me.clip.placeholderapi.PlaceholderAPI;
import me.theseems.tcrates.Crate;
import me.theseems.tcrates.TCratesAPI;
import me.theseems.tcrates.TCratesPlugin;
import me.theseems.tcrates.animations.CircleCrateAnimation;
import me.theseems.tcrates.rewards.CrateReward;
import me.theseems.tcrates.rewards.CrateRewardContainer;
import me.theseems.tcrates.rewards.IconReward;
import org.bukkit.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.security.SecureRandom;
import java.util.*;

public class CircleRoll {

  public static Color getRandomColor() {
    return Color.fromRGB(
        new SecureRandom().nextInt(256),
        new SecureRandom().nextInt(256),
        new SecureRandom().nextInt(256));
  }

  public static ItemStack createItemStack(String crateName, UUID player, int index) {
    //noinspection OptionalGetWithoutIsPresent
    Crate crate = TCratesAPI.getCrateManager().find(crateName).get();
    if (index >= crate.getRewardContainer().size()) {
      index = crate.getRewardContainer().grab(1).iterator().next();
    }

    CrateRewardContainer container = crate.getRewardContainer();
    ItemStack stack;
    String name;

    Optional<CrateReward> reward = container.find(index);
    if (reward.isPresent()) {
      CrateReward crateReward = reward.get();
      if (crateReward instanceof IconReward) {
        stack = new ItemStack(((IconReward) crateReward).getIcon(player));
        name =
            stack.getItemMeta() != null
                ? stack.getItemMeta().getDisplayName()
                : crateReward.getName();
      } else {
        stack = new ItemStack(Material.BEDROCK);
        name = crateReward.getName();
      }
    } else {
      stack = new ItemStack(Material.BARRIER);
      name = "";
    }

    name = PlaceholderAPI.setPlaceholders(Bukkit.getPlayer(player), name);
    ItemMeta meta = stack.getItemMeta();
    meta.setDisplayName(name);
    stack.setItemMeta(meta);

    return stack;
  }

  private List<Hologram> stands;
  private UUID player;

  private String crateName;
  private Location center;
  private Color color;

  private double angle;
  private double offset;
  private int size;
  private int ticks;
  private double radius;

  public CircleRoll(Location center, UUID player, String crateName, double radius, int size) {
    this.radius = radius;
    this.size = size;

    this.stands = new ArrayList<>();
    this.crateName = crateName;
    this.player = player;
    this.crateName = crateName;
    this.center = center;

    double speed = .1;
    this.offset = 2D * Math.PI / size;
    this.ticks = 0;
    this.color = Color.WHITE;

    angle = 2 * Math.PI * 10 / 20D; // the seconds for which the runnable has been running
    angle *= speed; // apply speed
  }

  public void spawn() {
    for (int i = stands.size(); i < size; i++) {
      Location location = center.clone();
      Hologram hologram = HologramsAPI.createHologram(TCratesPlugin.getPlugin(), location);
      ItemStack stack = createItemStack(crateName, player, i);
      hologram.appendItemLine(stack);
      hologram.appendTextLine(Objects.requireNonNull(stack.getItemMeta()).getDisplayName());
      stands.add(hologram);
    }
  }

  public void draw() {
    for (Hologram stand : stands) {
      if (stand.isDeleted()) continue;

      double x = radius * Math.sin(angle);
      double y = radius * Math.cos(angle);

      Location newLoc = center.clone().add(x, 0, y);

      // teleport to the new offset location
      stand.teleport(newLoc);

      newLoc
          .getWorld()
          .spawnParticle(
              Particle.REDSTONE,
              stand.getLocation().clone().add(0, -0.5, 0),
              CircleCrateAnimation.getIntProperty(this, "particle_count", 1),
              CircleCrateAnimation.getDoubleProperty(this, "particle_v", .1),
              CircleCrateAnimation.getDoubleProperty(this, "particle_v1", .1),
              CircleCrateAnimation.getDoubleProperty(this, "particle_v2", .1),
              CircleCrateAnimation.getDoubleProperty(this, "particle_v3", .1),
              new Particle.DustOptions(
                  color, CircleCrateAnimation.getIntProperty(this, "particle_size", 1)));

      angle += offset; // Update angle
    }

    angle += 1D / radius;
  }

  public List<Hologram> getStands() {
    return stands;
  }

  public void setStands(List<Hologram> stands) {
    this.stands = stands;
  }

  public UUID getPlayer() {
    return player;
  }

  public void setPlayer(UUID player) {
    this.player = player;
  }

  public String getCrateName() {
    return crateName;
  }

  public void setCrateName(String crateName) {
    this.crateName = crateName;
  }

  public Location getCenter() {
    return center;
  }

  public void setCenter(Location center) {
    this.center = center;
  }

  public Color getColor() {
    return color;
  }

  public void setColor(Color color) {
    this.color = color;
  }

  public double getAngle() {
    return angle;
  }

  public void setAngle(double angle) {
    this.angle = angle;
  }

  public double getOffset() {
    return offset;
  }

  public void setOffset(double offset) {
    this.offset = offset;
  }

  public int getSize() {
    return size;
  }

  public void setSize(int size) {
    this.size = size;
  }

  public int getTicks() {
    return ticks;
  }

  public void setTicks(int ticks) {
    this.ticks = ticks;
  }

  public double getRadius() {
    return radius;
  }

  public void setRadius(double radius) {
    this.radius = radius;
  }
}
