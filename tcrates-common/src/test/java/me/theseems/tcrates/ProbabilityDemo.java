package me.theseems.tcrates;

import java.util.UUID;

public class ProbabilityDemo {
  public static void main(String[] args) {
    UUID player = UUID.randomUUID();

    ProbabilityRewardContainer container = new ProbabilityRewardContainer();
    container.addReward(50D, new DummyReward("50%"));
    container.addReward(40D, new DummyReward("40%"));
    container.addReward(10D, new DummyReward("10%"));
    container.addReward(5D, new DummyReward("5%"));

    MemoryCrateMeta memoryCrateMeta = new MemoryCrateMeta();
    memoryCrateMeta.set("minRewards", 1);
    memoryCrateMeta.set("maxRewards", 3);

    for (Integer integer : container.generate(player, memoryCrateMeta)) {
      container.find(integer).get().give(player);
    }
  }
}
