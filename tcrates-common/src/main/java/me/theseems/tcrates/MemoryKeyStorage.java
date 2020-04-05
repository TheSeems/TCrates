package me.theseems.tcrates;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class MemoryKeyStorage implements KeyStorage {
  private Map<UUID, Map<String, Integer>> storage;

  public MemoryKeyStorage() {
    storage = new ConcurrentHashMap<>();
  }

  @Override
  public int getKeysFor(UUID player, String crateName) {
    if (!storage.containsKey(player)) return 0;
    if (!storage.get(player).containsKey(crateName)) return 0;
    return storage.get(player).get(crateName);
  }

  @Override
  public void setKeysFor(UUID player, String crateName, int count) {
    if (!storage.containsKey(player)) {
      storage.put(player, new ConcurrentHashMap<>());
    }
    storage.get(player).put(crateName, count);
  }
}
