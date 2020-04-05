package me.theseems.tcrates.utils;

import com.google.common.base.Joiner;
import me.theseems.tcrates.Crate;
import me.theseems.tcrates.ProbabilityRewardContainer;
import me.theseems.tcrates.SimpleCrate;
import me.theseems.tcrates.config.CrateConfig;
import me.theseems.tcrates.config.CrateRewardConfig;
import me.theseems.tcrates.rewards.GroupReward;
import me.theseems.tcrates.rewards.IconReward;
import me.theseems.tcrates.rewards.MoneyReward;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class Utils {
  public static Optional<Location> fromString(String str) {
    String[] splintered = str.split(";");
    if (splintered.length < 6) return Optional.empty();

    String world = splintered[0];
    if (Bukkit.getWorld(world) == null) return Optional.empty();

    try {
      double x = Double.parseDouble(splintered[1]);
      double y = Double.parseDouble(splintered[2]);
      double z = Double.parseDouble(splintered[3]);
      float yaw = Float.parseFloat(splintered[4]);
      float pitch = Float.parseFloat(splintered[5]);
      return Optional.of(new Location(Bukkit.getWorld(world), x, y, z, yaw, pitch));
    } catch (NumberFormatException e) {
      return Optional.empty();
    }
  }

  public static CrateConfig makeConfig(Crate crate) {
    if (!(crate instanceof SimpleCrate))
      throw new IllegalStateException("For now we do support only SimpleCrate crates");
    SimpleCrate simpleCrate = (SimpleCrate) crate;

    List<CrateRewardConfig> crateRewardConfigList = new ArrayList<>();
    ProbabilityRewardContainer rewardContainer =
        (ProbabilityRewardContainer) simpleCrate.getRewardContainer();

    for (ProbabilityRewardContainer.ProbableReward probableReward :
        rewardContainer.getRewardList()) {
      ItemStack itemStack = ((IconReward) probableReward.getReward()).getIcon(UUID.randomUUID());
      if (probableReward.getReward() instanceof GroupReward) {

        crateRewardConfigList.add(
            new CrateRewardConfig(
                probableReward.getProbability(),
                itemStack.getItemMeta().getDisplayName(),
                itemStack.getType().name(),
                ((GroupReward) probableReward.getReward()).getGroupName(),
                ((GroupReward) probableReward.getReward()).getServer(), 0D, "group"));
      } else if (probableReward.getReward() instanceof MoneyReward) {

        crateRewardConfigList.add(
          new CrateRewardConfig(
            probableReward.getProbability(),
            itemStack.getItemMeta().getDisplayName(),
            itemStack.getType().name(),
            null,
            null, ((MoneyReward) probableReward.getReward()).getMoney(), "money"));
      }
    }

    return new CrateConfig(
        simpleCrate.getMeta(),
      crateRewardConfigList,
        simpleCrate.getName(),
        "circle",
        simpleCrate.getRequirements() != null);
  }

  public static String forLocation(Location location) {
    if (location.getWorld() == null) location.setWorld(Bukkit.getWorlds().get(0));

    Joiner joiner = Joiner.on(";").skipNulls();
    return joiner.join(
        location.getWorld().getName(),
        location.getX(),
        location.getY(),
        location.getZ(),
        location.getYaw(),
        location.getPitch());
  }
}
