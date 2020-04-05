package me.theseems.tcrates.handlers;

import me.theseems.tcrates.rewards.RewardQueueEntry;
import me.theseems.tcrates.TCratesAPI;
import me.theseems.tcrates.TCratesPlugin;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

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

      entry
          .take()
          .ifPresentOrElse(
              crateReward -> {
                TCratesAPI.getRewardQueue().remove(entry);
                crateReward.give(player);
                actual.sendMessage(
                    "§7Выдана награда из кейса §7'§6"
                        + entry.getCrateName()
                        + "§7': "
                        + crateReward.getName().replace("&", "§"));
              },
              () ->
                  TCratesPlugin.getPluginLogger()
                      .warning(
                          "Cannot grant non-granted entry: "
                              + entry.getIndex()
                              + "@"
                              + entry.getCrateName()
                              + " for "
                              + entry.getPlayer()));
    }
  }

  public BukkitTask getTask() {
    return task;
  }
}
