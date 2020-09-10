package me.theseems.tcrates.handlers;

import me.theseems.tcrates.rewards.CrateReward;
import me.theseems.tcrates.rewards.RewardQueueEntry;
import me.theseems.tcrates.TCratesAPI;
import me.theseems.tcrates.TCratesPlugin;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import java.util.Optional;
import java.util.UUID;

public class AutoGrantHandler implements Runnable {
  private BukkitTask task;

  public AutoGrantHandler() {
    this.task = Bukkit.getScheduler().runTaskTimer(TCratesPlugin.getPlugin(), this, 10, 10);
  }

  @Override
  public void run() {
    for (RewardQueueEntry entry : TCratesAPI.getRewardQueue().getNotGranted()) {
      UUID player = entry.getPlayer();
      Player actual = Bukkit.getPlayer(player);
      if (actual == null) {
        continue;
      }

      Optional<CrateReward> optionalCrateReward = entry.take();
      if (optionalCrateReward.isPresent()) {
        CrateReward crateReward = optionalCrateReward.get();
        System.out.println("Granting: " + crateReward);
        TCratesAPI.getRewardQueue().remove(entry);
        crateReward.give(player);
      } else {
        TCratesPlugin.getPluginLogger()
            .warning(
                "Cannot grant non-granted entry: "
                    + entry.getIndex()
                    + "@"
                    + entry.getCrateName()
                    + " for "
                    + entry.getPlayer());
      }
    }
  }

  public BukkitTask getTask() {
    return task;
  }
}
