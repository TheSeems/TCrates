package me.theseems.tcrates.animations.circle;

import com.gmail.filoghost.holographicdisplays.api.Hologram;
import com.gmail.filoghost.holographicdisplays.api.HologramsAPI;
import fr.mrmicky.fastparticle.FastParticle;
import fr.mrmicky.fastparticle.ParticleType;
import me.clip.placeholderapi.PlaceholderAPI;
import me.theseems.tcrates.Crate;
import me.theseems.tcrates.TCratesAPI;
import me.theseems.tcrates.TCratesPlugin;
import me.theseems.tcrates.rewards.CrateReward;
import me.theseems.tcrates.rewards.CrateRewardContainer;
import me.theseems.tcrates.rewards.IconReward;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
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
  private double tetta;
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

    double speed =
        TCratesAPI.getCrateManager().get(crateName).getMeta().getDouble("speed").orElse(.1);
    this.offset = 2D * Math.PI / size;
    this.ticks = 0;
    this.color = Color.WHITE;

    angle =
        2
            * Math.PI
            * TCratesAPI.getCrateManager().get(crateName).getMeta().getDouble("time").orElse(10D)
            / 20D; // seconds for runnable to be ticking
    angle *= speed; // apply speed
  }

  public void spawn() {
    for (int i = stands.size(); i < size; i++) {
      Location location = center.clone().add(0, getCrateDouble("offsetY", 0.5), 0);
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

      double offsetX = getCrateDouble("offsetX", 0);

      x += offsetX;

      Location newLoc = center.clone().add(x, getCrateDouble("offsetY", 0.5), y);

      if (getCrateBool("spiral", false)) {
        newLoc.setY(
            center.clone().getY()
                + getCrateDouble("offsetY", 0.5)
                + getCrateDouble("spiral_multi", 1) * (Math.sin(angle + tetta)));
      }

      // teleport to the new offset location
      stand.teleport(newLoc);

      double red = getCrateDouble("dred", 1);
      double green = getCrateDouble("dgreen", 1);
      double blue = getCrateDouble("dblue", 1);

      FastParticle.spawnParticle(
          newLoc.getWorld(),
          ParticleType.REDSTONE,
          newLoc.clone().add(0, -0.5, 0),
          1,
          Color.fromRGB(((int) (255 * red)), ((int) (255 * green)), ((int) (255 * blue))));

      angle += offset; // Update angle
    }

    angle += 1D / radius * getCrateDouble("speed", 1);
    tetta += 1D / radius * getCrateDouble("spiral_speed", 1);
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

  public Crate getCrate() {
    return TCratesAPI.getCrateManager()
        .find(crateName)
        .orElseThrow(
            () ->
                new IllegalStateException(
                    "Cannot find crate '" + crateName + "' for the circle crate animation!"));
  }

  private int getCrateInt(String key, int def) {
    Optional<Crate> optionalCrate = TCratesAPI.getCrateManager().find(crateName);
    return optionalCrate.map(crate -> crate.getMeta().getInteger(key).orElse(def)).orElse(def);
  }

  private boolean getCrateBool(String key, boolean def) {
    Optional<Crate> optionalCrate = TCratesAPI.getCrateManager().find(crateName);
    return (boolean) optionalCrate.map(crate -> crate.getMeta().get(key).orElse(def)).orElse(def);
  }

  private double getCrateDouble(String key, double def) {
    Optional<Crate> optionalCrate = TCratesAPI.getCrateManager().find(crateName);
    return optionalCrate.map(crate -> crate.getMeta().getDouble(key).orElse(def)).orElse(def);
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
