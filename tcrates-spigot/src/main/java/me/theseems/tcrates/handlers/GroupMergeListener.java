package me.theseems.tcrates.handlers;

import me.theseems.tcrates.Crate;
import me.theseems.tcrates.events.CrateCloseEvent;
import me.theseems.tcrates.rewards.CrateReward;
import me.theseems.tcrates.rewards.GroupReward;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.model.group.Group;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.RegisteredServiceProvider;

public class GroupMergeListener implements Listener {
  @EventHandler
  public void onClose(CrateCloseEvent e) {
    if (!e.getCrate().isPresent()) return;

    Crate crate = e.getCrate().get();
    Group topGroup = null;
    int maxWeight = 0;

    RegisteredServiceProvider<LuckPerms> provider =
        Bukkit.getServicesManager().getRegistration(LuckPerms.class);
    LuckPerms luckPerms = provider.getProvider();

    for (CrateReward exactReward : e.getExactRewards()) {
      System.err.println(
          "Checking reward: " + exactReward + " (" + (exactReward instanceof GroupReward) + ")");
      if (!(exactReward instanceof GroupReward)) continue;
      Group group =
          luckPerms
              .getGroupManager()
              .loadGroup(((GroupReward) exactReward).getGroupName())
              .join()
              .orElse(null);

      if (group == null) {
        System.err.println(
            "Group '"
                + ((GroupReward) exactReward).getGroupName()
                + "' is not found for '"
                + e.getCrateName()
                + "'");
        continue;
      }

      if (group.getWeight().orElse(0) > maxWeight) {
        topGroup = group;
        maxWeight = group.getWeight().orElse(0);
      }
    }

    if (topGroup == null) {
      System.out.println("Top group is not found for crate '" + e.getCrateName() + "'");
      return;
    }

    System.out.println("Top group is FOUND for crate '" + e.getCrateName() + "': " + topGroup.getName());

    for (int i = 0; i < crate.getRewardContainer().size(); i++) {
      Group finalTopGroup = topGroup;
      int finalI = i;
      crate
          .getRewardContainer()
          .find(i)
          .ifPresent(
              crateReward -> {
                if (!(crateReward instanceof GroupReward)) return;
                GroupReward groupReward = (GroupReward) crateReward;
                if (!groupReward.getGroupName().equals(finalTopGroup.getName()))
                  e.removeReward(finalI);
              });
    }
  }
}
