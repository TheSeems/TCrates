package me.theseems.tcrates.rewards;

import me.theseems.tcrates.TCratesPlugin;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.model.group.Group;
import net.luckperms.api.node.NodeType;
import net.luckperms.api.node.types.InheritanceNode;
import net.luckperms.api.node.types.PrefixNode;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;

import java.util.Base64;
import java.util.Date;
import java.util.Objects;
import java.util.UUID;

public abstract class GroupReward implements IconReward {
  private String groupName;
  private String server;
  private LuckPerms luckPerms;

  public GroupReward(String groupName, String server) {
    this.groupName = groupName;
    this.server = server;
    RegisteredServiceProvider<LuckPerms> provider =
        Bukkit.getServicesManager().getRegistration(LuckPerms.class);
    luckPerms = provider.getProvider();
  }

  public String getPrivilege() {
    Group group = luckPerms.getGroupManager().getGroup(groupName);
    if (group == null) {
      TCratesPlugin.getPluginLogger()
          .warning("There is no group '" + groupName + "' in luckperms to get name of");
      return "<ОШИБКА>";
    }

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
          .sendTitle("Вам выдана привилегия", privilege.replace("&", "§"), 20, 20, 20);
  }

  @Override
  public void give(UUID player) {
    Group group = luckPerms.getGroupManager().getGroup(groupName);
    if (group == null) {
      TCratesPlugin.getPluginLogger()
          .warning("There is no group '" + groupName + "' for user " + player);

      Player actual = Bukkit.getPlayer(player);
      if (actual == null)
        return;

      TextComponent component = new TextComponent("§7Произошла ошибка во время выдачи награды: §c§l[СООБЩИТЬ ОБ ОШИБКЕ]");
      component.setClickEvent(
          new ClickEvent(
              ClickEvent.Action.OPEN_URL,
              "https://theseems.ru/vmc/?c="
                  + Base64.getEncoder()
                      .encodeToString(("tcrates-not-granted-" + groupName + new Date().toString() + "-to-" + actual.getName()).getBytes())));
      actual.spigot().sendMessage(component);
    } else {
      InheritanceNode node =
          InheritanceNode.builder(group.getName()).withContext("server", server).build();
      if (luckPerms != null) {
        LuckPermsProvider.get()
            .getUserManager()
            .loadUser(player)
            .thenAccept(
                user -> {
                  user.data().add(node);
                  sendTitle(player);
                });
      }
    }
  }

  @Override
  public String getName() {
    return "§7Привилегия '§7" + getPrivilege() + "§7'";
  }

  public String getGroupName() {
    return groupName;
  }

  public void setGroupName(String groupName) {
    this.groupName = groupName;
  }

  public String getServer() {
    return server;
  }

  public void setServer(String server) {
    this.server = server;
  }
}
