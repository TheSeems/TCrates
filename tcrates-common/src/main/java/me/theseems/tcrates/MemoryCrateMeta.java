package me.theseems.tcrates;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class MemoryCrateMeta implements CrateMeta {
  private Map<String, Object> map;

  public MemoryCrateMeta() {
    map = new ConcurrentHashMap<>();
  }

  @Override
  public void set(String key, Object value) {
    map.put(key, value);
  }

  @Override
  public Optional<Object> get(String key) {
    return Optional.of(map.get(key));
  }

  @Override
  public Optional<Integer> getInteger(String key) {
    Optional<Double> doubleOptional = getDouble(key);
    return doubleOptional.map(Double::intValue);
  }

  @Override
  public Optional<Double> getDouble(String key) {
    if (!map.containsKey(key)) return Optional.empty();
    try {
      return Optional.of((double) map.get(key));
    } catch (Exception e) {
      return Optional.empty();
    }
  }

  @Override
  public Optional<String> getString(String key) {
    if (!map.containsKey(key)) return Optional.empty();
    try {
      return Optional.of((String) map.get(key));
    } catch (Exception e) {
      return Optional.empty();
    }
  }

  @Override
  public Collection<String> getKeys() {
    return map.keySet();
  }

  public Map<String, Object> getMap() {
    return map;
  }

  public void setMap(Map<String, Object> map) {
    this.map = map;
  }

  public static MemoryCrateMeta to(CrateMeta meta) {
    MemoryCrateMeta memoryCrateMeta = new MemoryCrateMeta();
    for (String key : meta.getKeys()) {
      Optional<Object> objectOptional = meta.get(key);
      if (!objectOptional.isPresent()) continue;

      memoryCrateMeta.set(key, objectOptional.get());
    }
    return memoryCrateMeta;
  }
}
