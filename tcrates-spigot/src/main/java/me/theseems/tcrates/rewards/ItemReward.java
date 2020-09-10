package me.theseems.tcrates.rewards;

import me.theseems.tcrates.CrateMeta;
import me.theseems.tcrates.MemoryCrateMeta;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Objects;
import java.util.UUID;

public abstract class ItemReward implements IconReward {
  private MemoryCrateMeta meta;
  public abstract ItemStack get(Player player);

  @Override
  public MemoryCrateMeta getMeta() {
    return meta;
  }

  @Override
  public void setMeta(CrateMeta meta) {
    this.meta = MemoryCrateMeta.to(meta);
  }

  @Override
  public void give(UUID player) {
    Player actual = Bukkit.getPlayer(player);
    Objects.requireNonNull(actual).getInventory().addItem(get(actual));
  }
}
