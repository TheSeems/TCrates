package me.theseems.tcrates.rewards;

import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.plugin.RegisteredServiceProvider;

import java.util.UUID;

import static org.bukkit.Bukkit.getServer;

public abstract class MoneyReward implements IconReward {
  private double money;

  Economy getEconomy() {
    if (getServer().getPluginManager().getPlugin("Vault") == null) {
      return null;
    }
    RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
    if (rsp == null) {
      return null;
    }
    return rsp.getProvider();
  }

  public MoneyReward(double money) {
    this.money = money;
  }

  @Override
  public void give(UUID player) {
    getEconomy().depositPlayer(Bukkit.getOfflinePlayer(player), money);
  }

  public double getMoney() {
    return money;
  }
}
