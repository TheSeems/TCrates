package me.theseems.tcrates.handlers;

import me.theseems.tcrates.events.CrateCloseEvent;
import me.theseems.tcrates.rewards.CrateReward;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import java.text.MessageFormat;

public class AnnounceListener implements Listener {
  @EventHandler(priority = EventPriority.MONITOR)
  public void onCloseEvent(CrateCloseEvent e) {
    if (e.isCancelled()) return;
    if (!e.getCrate().isPresent()) return;

    if (e.getExactRewards().size() == 0) {
        e.getPlayer().sendMessage("§7В этот раз ничего не выпало(");
        e.getPlayer().sendMessage("§eBetter luck next time");
        return;
    }

    e.getCrate()
        .get()
        .getMeta()
        .getString("announce")
        .ifPresent(
            s -> {
              StringBuilder builder = new StringBuilder();
              for (CrateReward exactReward : e.getExactRewards()) {
                builder.append(exactReward.getName()).append(", ");
              }
              if (builder.length() != 0) {
                builder.delete(builder.length() - 2, builder.length());
              }

              Bukkit.broadcastMessage(
                  MessageFormat.format(s, e.getPlayer().getName(), builder.toString())
                      .replace('&', '§'));
            });
  }
}
