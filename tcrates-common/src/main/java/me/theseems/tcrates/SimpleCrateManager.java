package me.theseems.tcrates;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class SimpleCrateManager implements CrateManager {
  private Map<String, Crate> crateMap;

  public SimpleCrateManager() {
    crateMap = new HashMap<>();
  }

  @Override
  public void register(Crate crate) {
    crateMap.put(crate.getName(), crate);
  }

  @Override
  public void unregister(String name) {
    crateMap.remove(name);
  }

  @Override
  public Collection<String> getCrates() {
    return crateMap.keySet();
  }

  @Override
  public Optional<Crate> find(String name) {
    return Optional.ofNullable(crateMap.get(name));
  }

  @Override
  public Crate get(String name) {
    return crateMap.get(name);
  }
}
