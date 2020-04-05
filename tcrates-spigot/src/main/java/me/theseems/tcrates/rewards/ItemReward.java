package me.theseems.tcrates.rewards;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Objects;
import java.util.UUID;

public abstract class ItemReward implements IconReward {
  public abstract ItemStack get(Player player);

  @Override
  public ItemStack getIcon(UUID player) {
    return get(Bukkit.getPlayer(player));
  }


  @Override
  public void give(UUID player) {
    Player actual = Bukkit.getPlayer(player);
    Objects.requireNonNull(actual).getInventory().addItem(get(actual));
  }
}
