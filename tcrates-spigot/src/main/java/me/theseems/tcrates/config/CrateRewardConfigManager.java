package me.theseems.tcrates.config;

import me.theseems.tcrates.MemoryCrateMeta;
import me.theseems.tcrates.TCratesPlugin;
import me.theseems.tcrates.rewards.CrateReward;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.function.Function;

public class CrateRewardConfigManager {
  private Map<String, Function<CrateRewardConfig, CrateReward>> rewards;

  public CrateRewardConfigManager() {
    rewards = new ConcurrentSkipListMap<>();
  }

  public void register(String type, Function<CrateRewardConfig, CrateReward> function) {
    rewards.put(type, function);
  }

  public void unregister(String type) {
    rewards.remove(type);
  }

  public Optional<CrateReward> make(CrateRewardConfig crateConfig) {
    CrateReward reward;
    if (!rewards.containsKey(crateConfig.getType())) {
      TCratesPlugin.getPluginLogger()
          .warning(
              "Reward type '" + crateConfig.getType() + "' is not found. Consider changing it");

      reward = rewards.get("money").apply(crateConfig);
    } else {
      reward = rewards.get(crateConfig.getType()).apply(crateConfig);
    }

    reward.setMeta(new MemoryCrateMeta());
    for (String key : crateConfig.getMeta().getKeys()) {
      Optional<Object> objectOptional = crateConfig.getMeta().get(key);
      if (!objectOptional.isPresent()) continue;

      reward.getMeta().set(key, objectOptional.get());
    }

    reward.getMeta().set("type", crateConfig.getType());
    return Optional.of(reward);
  }
}
