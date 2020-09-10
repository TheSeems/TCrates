package me.theseems.tcrates.utils;

import com.google.common.base.Joiner;
import com.google.gson.Gson;
import me.theseems.tcrates.*;
import me.theseems.tcrates.config.CrateConfig;
import me.theseems.tcrates.config.CrateRewardConfig;
import me.theseems.tcrates.config.RewardIconConfig;
import me.theseems.tcrates.rewards.IconReward;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.security.Key;
import java.util.*;

public class Utils {


  public static Optional<Location> fromString(String str) {
    String[] splintered = str.split(";");
    if (splintered.length < 6) {
      TCratesPlugin.getPluginLogger()
          .severe(
              "Invalid location format:  '"
                  + str
                  + "':  expected "
                  + 6
                  + " args, but found "
                  + splintered.length);
      return Optional.empty();
    }

    String world = splintered[0];

    try {
      double x = Double.parseDouble(splintered[1]);
      double y = Double.parseDouble(splintered[2]);
      double z = Double.parseDouble(splintered[3]);
      float yaw = Float.parseFloat(splintered[4]);
      float pitch = Float.parseFloat(splintered[5]);
      return Optional.of(new Location(Bukkit.getWorld(world), x, y, z, yaw, pitch));
    } catch (NumberFormatException e) {
      TCratesPlugin.getPluginLogger()
          .severe("Invalid value in location '" + str + "': " + e.getMessage());
      return Optional.empty();
    }
  }

  public static CrateConfig makeConfig(Crate crate) {
    if (!(crate instanceof SimpleCrate))
      throw new IllegalStateException("For now we do support only SimpleCrate crates");
    SimpleCrate simpleCrate = (SimpleCrate) crate;

    List<CrateRewardConfig> crateRewardConfigList = new ArrayList<>();
    ProbabilityRewardContainer rewardContainer =
        (ProbabilityRewardContainer) simpleCrate.getRewardContainer();

    for (ProbabilityRewardContainer.ProbableReward reward : rewardContainer.getRewardList()) {

      ItemStack itemStack = ((IconReward) reward.getReward()).getIcon(UUID.randomUUID());
      String name = "";
      Material material = Material.COBBLESTONE;
      String[] lore = new String[] {};

      if (itemStack != null && itemStack.getItemMeta() != null) {
        name = itemStack.getItemMeta().getDisplayName();
        material = itemStack.getType();
        if (itemStack.getItemMeta().getLore() != null)
          lore = itemStack.getItemMeta().getLore().toArray(new String[] {});
      }

      MemoryCrateMeta memoryCrateMeta;
      if (reward.getReward().getMeta() == null) {
        memoryCrateMeta = new MemoryCrateMeta();
      } else if (!(reward.getReward().getMeta() instanceof MemoryCrateMeta)) {
        memoryCrateMeta = MemoryCrateMeta.to(reward.getReward().getMeta());
      } else {
        memoryCrateMeta = (MemoryCrateMeta) reward.getReward().getMeta();
      }

      CrateRewardConfig rewardConfig =
          new CrateRewardConfig(
              reward.getProbability(),
              reward.getReward().getName(),
              RewardIconConfig.from(itemStack),
              memoryCrateMeta,
              memoryCrateMeta.getString("type").orElse("unknown"));

      memoryCrateMeta
          .get("__other")
          .ifPresent(
              o -> {
                rewardConfig.setOther((RewardIconConfig) o);
                rewardConfig.getMeta().remove("__other");
              });

      crateRewardConfigList.add(rewardConfig);
    }

    return new CrateConfig(simpleCrate.getMeta(), crateRewardConfigList, simpleCrate.getName());
  }

  public static String forLocation(Location location) {
    if (location.getWorld() == null) location.setWorld(Bukkit.getWorlds().get(0));

    Joiner joiner = Joiner.on(";").skipNulls();
    return joiner.join(
        location.getWorld().getName(),
        location.getX(),
        location.getY(),
        location.getZ(),
        location.getYaw(),
        location.getPitch());
  }

  public static String getStackTrace(final Throwable throwable) {
    final StringWriter sw = new StringWriter();
    final PrintWriter pw = new PrintWriter(sw, true);
    throwable.printStackTrace(pw);
    return sw.getBuffer().toString();
  }

  private static final String ALGO = "AES";
  private static final byte[] keyValue =
      new byte[] {'T', 'h', 'e', 'B', 'e', 's', 't', 'S', 'e', 'c', 'r', 'e', 't', 'K', 'e', 'y'};

  /**
   * Encrypt a string with AES algorithm.
   *
   * @param data is a string
   * @return the encrypted string
   */
  public static String encrypt(String data) throws Exception {
    Key key = generateKey();
    Cipher c = Cipher.getInstance(ALGO);
    c.init(Cipher.ENCRYPT_MODE, key);
    byte[] encVal = c.doFinal(data.getBytes());
    return Base64.getEncoder().encodeToString(encVal);
  }

  /**
   * Decrypt a string with AES algorithm.
   *
   * @param encryptedData is a string
   * @return the decrypted string
   */
  public static String decrypt(String encryptedData) throws Exception {
    Key key = generateKey();
    Cipher c = Cipher.getInstance(ALGO);
    c.init(Cipher.DECRYPT_MODE, key);
    byte[] decordedValue = Base64.getDecoder().decode(encryptedData);
    byte[] decValue = c.doFinal(decordedValue);
    return new String(decValue);
  }

  /** Generate a new encryption key. */
  private static Key generateKey() {
    return new SecretKeySpec(keyValue, ALGO);
  }

  public static String encode(Throwable e) {
    Gson gson = new Gson();
    Map<String, String> exc_map = new HashMap<>();
    exc_map.put("message", e.toString());
    exc_map.put("stacktrace", getStackTrace(e));

    String anime;
    try {
      anime = encrypt(gson.toJson(exc_map));
    } catch (Exception noSuchAlgorithmException) {
      noSuchAlgorithmException.printStackTrace();
      anime = Base64.getEncoder().encodeToString(gson.toJson(exc_map).getBytes());
    }

    return "https://theseems.ru/vmc?c=" + anime;
  }
}
