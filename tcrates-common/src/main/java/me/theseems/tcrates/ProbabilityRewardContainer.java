package me.theseems.tcrates;

import me.theseems.tcrates.rewards.CrateReward;
import me.theseems.tcrates.rewards.CrateRewardContainer;

import java.security.SecureRandom;
import java.util.*;

public class ProbabilityRewardContainer implements CrateRewardContainer {
  public static class ProbableReward {
    private Double probability;
    private CrateReward reward;
    private int index;

    public ProbableReward(Double probability, CrateReward reward) {
      this.probability = probability;
      this.reward = reward;
    }

    public Double getProbability() {
      return probability;
    }

    public CrateReward getReward() {
      return reward;
    }

    public void setIndex(int index) {
      this.index = index;
    }

    public int getIndex() {
      return index;
    }

    @Override
    public String toString() {
      return "ProbableReward{" + "probability=" + probability + ", reward=" + reward + '}';
    }
  }

  private List<ProbableReward> rewardList;

  public ProbabilityRewardContainer() {
    rewardList = new ArrayList<>();
  }

  public void addReward(Double probability, CrateReward reward) {
    ProbableReward probableReward = new ProbableReward(probability, reward);
    probableReward.setIndex(rewardList.size());
    rewardList.add(probableReward);
  }

  public int randomFor(int low, int high) {
    if (high == low) return high;

    return new SecureRandom().nextInt(high - low) + low;
  }

  @Override
  public Collection<Integer> generate(UUID player) {
    List<Integer> answer = new ArrayList<>();

    for (int i = 0; i < rewardList.size(); i++) {
      double random = Math.random() * 100D;
      if (random < rewardList.get(i).probability) {
        answer.add(i);
      }
    }

    return answer;
  }

  /**
   * Generate rewards for players
   *
   * @param player to generate for
   * @param crateMeta to generate for
   * @return rewards
   */
  @Override
  public Collection<Integer> generate(UUID player, CrateMeta crateMeta) {
    int minRewards = crateMeta.getInteger("minRewards").orElse(0);
    int maxRewards = crateMeta.getInteger("maxRewards").orElse(rewardList.size());

    if (minRewards > maxRewards) {
      throw new IllegalStateException("Min rewards size must be lower than max rewards size!");
    }

    List<Integer> answer = new ArrayList<>();

    for (int i = 0; i < rewardList.size(); i++) {
      double random = Math.random() * 100D;
      if (random < rewardList.get(i).probability && answer.size() + 1 <= maxRewards) {
        answer.add(i);
      }
    }

    if (answer.size() < minRewards) {
      int need = minRewards - answer.size();
      List<ProbableReward> sorted = new ArrayList<>(rewardList);
      boolean unique = crateMeta.getBoolean("unique").orElse(true);
      if (unique) {
        for (Integer integer : answer) {
          sorted.remove(getRewardList().get(integer));
        }
      }

      if (need > sorted.size()) {
        System.err.println(
            "It is impossible to obtain unique rewards size higher than "
                + minRewards
                + " and lower than "
                + maxRewards
                + " (remaining "
                + sorted.size()
                + " and need for " + need + ")");
        System.err.println("So that rewards size will be lower than " + minRewards);
      }

      sorted.sort(Comparator.comparing(o -> -o.probability));
      for (int i = 0; i < Math.min(need, sorted.size()); i++) {
        answer.add(sorted.get(i).index);
      }
    }

    return answer;
  }

  @Override
  public Collection<Integer> grab(int limit) {
    int serials = randomFor(limit / 2, limit);
    List<Integer> crateRewards = new ArrayList<>();
    while (serials > 0) {
      for (int i = 0; i < rewardList.size(); i++) {
        double random = new SecureRandom().nextDouble() * 100;
        if (random < rewardList.get(i).probability) {
          crateRewards.add(i);
        } else {
          crateRewards.add((i + 1) % crateRewards.size());
        }
        serials--;
      }
    }

    for (int i = 0; i < limit - serials; i++) {
      crateRewards.add(new SecureRandom().nextInt(rewardList.size()));
    }

    Collections.shuffle(crateRewards, new SecureRandom());
    return crateRewards;
  }

  @Override
  public Optional<CrateReward> find(int index) {
    if (index >= rewardList.size()) return Optional.empty();
    return Optional.of(rewardList.get(index).reward);
  }

  @Override
  public int size() {
    return rewardList.size();
  }

  public List<ProbableReward> getRewardList() {
    return rewardList;
  }

  public void setRewardList(List<ProbableReward> rewardList) {
    this.rewardList = rewardList;
  }
}
