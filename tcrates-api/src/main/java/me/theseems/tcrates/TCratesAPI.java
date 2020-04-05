package me.theseems.tcrates;

import me.theseems.tcrates.rewards.RewardQueue;

public class TCratesAPI {
  /** Crate manager * */
  private static CrateManager crateManager;

  /** Reward queue * */
  private static RewardQueue rewardQueue;

  /** Key storage * */
  private static KeyStorage keyStorage;

  public TCratesAPI() {}

  public static CrateManager getCrateManager() {
    return crateManager;
  }

  public static void setCrateManager(CrateManager crateManager) {
    TCratesAPI.crateManager = crateManager;
  }

  public static RewardQueue getRewardQueue() {
    return rewardQueue;
  }

  public static void setRewardQueue(RewardQueue rewardQueue) {
    TCratesAPI.rewardQueue = rewardQueue;
  }

  public static KeyStorage getKeyStorage() {
    return keyStorage;
  }

  public static void setKeyStorage(KeyStorage keyStorage) {
    TCratesAPI.keyStorage = keyStorage;
  }
}
