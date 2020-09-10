package me.theseems.tcrates.handlers;

import me.theseems.tcrates.events.CrateCloseEvent;
import me.theseems.tcrates.events.CrateOpenEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class CooldownListener implements Listener {
  private Map<String, Date> dateMap;

  public CooldownListener() {
    dateMap = new ConcurrentHashMap<>();
  }

  @EventHandler(priority = EventPriority.HIGHEST)
  public void onOpen(CrateOpenEvent e) {
    if (!dateMap.containsKey(e.getCrateName()))
      dateMap.putIfAbsent(e.getCrateName(), new Date());

    Date from = dateMap.get(e.getCrateName());
    Date to = new Date();

    long seconds = ChronoUnit.SECONDS.between(from.toInstant(), to.toInstant());
    long cooldown =
        e.getCrate()
            .orElseThrow(() -> new IllegalStateException("No crate in event!"))
            .getMeta()
            .getInteger("cooldown")
            .orElse(0);

    if (seconds < cooldown) {
      e.setCancelled(true);
      e.getPlayer()
          .sendMessage("§7Этот кейс можно будет открыть через " + (cooldown - seconds) + " с.");
    }
  }

  @EventHandler
  public void onClose(CrateCloseEvent e) {
    dateMap.put(e.getCrateName(), new Date());
  }
}
