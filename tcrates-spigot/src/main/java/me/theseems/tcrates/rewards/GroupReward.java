package me.theseems.tcrates.rewards;

import me.theseems.tcrates.CrateMeta;
import me.theseems.tcrates.MemoryCrateMeta;
import me.theseems.tcrates.TCratesPlugin;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.model.group.Group;
import net.luckperms.api.node.NodeType;
import net.luckperms.api.node.types.InheritanceNode;
import net.luckperms.api.node.types.PrefixNode;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;

import java.util.Objects;
import java.util.UUID;

public abstract class GroupReward implements IconReward {
  private String groupName;
  private String server;

  private MemoryCrateMeta meta;

  @Override
  public MemoryCrateMeta getMeta() {
    return meta;
  }

  public void setMeta(MemoryCrateMeta meta) {
    this.meta = meta;
  }

  @Override
  public void setMeta(CrateMeta meta) {
    this.meta = MemoryCrateMeta.to(meta);
    meta.set("type", "group");
    meta.set("group", groupName);
    meta.set("server", server);
  }

  public GroupReward(String groupName, String server) {
    this.groupName = groupName;
    this.server = server;
  }

  public String getPrivilege() {
    RegisteredServiceProvider<LuckPerms> provider =
        Bukkit.getServicesManager().getRegistration(LuckPerms.class);
    LuckPerms luckPerms = provider.getProvider();
    Group group =
        luckPerms
            .getGroupManager()
            .loadGroup(groupName)
            .join()
            .orElseThrow(
                () ->
                    new IllegalStateException(
                        "There is no group '" + groupName + "' in luckperms to get name of"));

    return group.getNodes().stream()
        .filter(NodeType.PREFIX::matches)
        .map(
            node -> {
              if (!(node instanceof PrefixNode)) return "";
              return ((PrefixNode) node).getMetaValue();
            })
        .findAny()
        .orElse("");
  }

  private void sendTitle(UUID player) {
    String privilege = getPrivilege();
    if (!privilege.isEmpty())
      Objects.requireNonNull(Bukkit.getPlayer(player))
          .sendTitle("Вам выдана привилегия", privilege.replace("&", "§"));
  }

  @Override
  public void give(UUID player) {
    RegisteredServiceProvider<LuckPerms> provider =
        Bukkit.getServicesManager().getRegistration(LuckPerms.class);
    LuckPerms luckPerms = provider.getProvider();
    Group group = luckPerms.getGroupManager().loadGroup(groupName).join().orElse(null);
    System.out.println("Giving...");
    if (group == null) {
      TCratesPlugin.getPluginLogger()
          .warning("There is no group '" + groupName + "' for user " + player);
    } else {
      InheritanceNode node =
          InheritanceNode.builder(group.getName()).withContext("server", server).build();
      LuckPermsProvider.get()
          .getUserManager()
          .loadUser(player)
          .thenAccept(
              user -> {
                Group primary =
                    LuckPermsProvider.get()
                        .getGroupManager()
                        .loadGroup(user.getPrimaryGroup())
                        .join()
                        .orElse(null);

                if (primary.getWeight().orElse(0) > group.getWeight().orElse(0)) {
                  Player pl = Bukkit.getPlayer(player);
                  if (pl != null) {
                    pl.sendMessage(
                        "§7Привилегия '"
                            + group.getDisplayName().replace("&", "§")
                            + "§7' не была выдана, так как она хуже вашей!");
                  }
                  return;
                }

                user.data().add(node);
                sendTitle(player);
                luckPerms.getUserManager().saveUser(user);
              });
    }
  }

  @Override
  public String getName() {
    return getPrivilege();
  }

  public String getGroupName() {
    return groupName;
  }

  public void setGroupName(String groupName) {
    this.groupName = groupName;
  }

  @Override
  public String toString() {
    return "GroupReward{"
        + "groupName='"
        + groupName
        + '\''
        + ", server='"
        + server
        + '\''
        + ", meta="
        + meta.getClass().getName()
        + '}';
  }

  public String getServer() {
    return server;
  }

  public void setServer(String server) {
    this.server = server;
  }
}
