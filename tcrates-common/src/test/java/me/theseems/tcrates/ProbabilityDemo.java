package me.theseems.tcrates;

import java.util.UUID;

public class ProbabilityDemo {
  public static void main(String[] args) {
    UUID player = UUID.randomUUID();

    ProbabilityRewardContainer container = new ProbabilityRewardContainer();
    container.addReward(50, new DummyReward("50%"));
    container.addReward(40, new DummyReward("40%"));
    container.addReward(10, new DummyReward("10%"));
    container.addReward(5, new DummyReward("5%"));

    for (Integer integer : container.generate(player)) {
      container.find(integer).get().give(player);
    }
  }
}
