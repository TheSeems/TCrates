package me.theseems.tcrates;

import java.util.Collection;
import java.util.Optional;

public interface CrateMeta {
  void set(String key, Object value);

  Optional<Object> get(String key);
  Optional<Integer> getInteger(String key);
  Optional<Double> getDouble(String key);
  Optional<String> getString(String key);


  Collection<String> getKeys();
}
