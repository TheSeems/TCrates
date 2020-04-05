package me.theseems.tcrates;

import me.theseems.tcrates.rewards.CrateReward;
import me.theseems.tcrates.rewards.CrateRewardContainer;

import java.security.SecureRandom;
import java.util.*;

public class ProbabilityRewardContainer implements CrateRewardContainer {
  public static class ProbableReward {
    private Integer probability;
    private CrateReward reward;

    public ProbableReward(Integer probability, CrateReward reward) {
      this.probability = probability;
      this.reward = reward;
    }

    public Integer getProbability() {
      return probability;
    }

    public CrateReward getReward() {
      return reward;
    }
  }

  private List<ProbableReward> rewardList;

  public ProbabilityRewardContainer() {
    rewardList = new ArrayList<>();
  }

  public void addReward(Integer probability, CrateReward reward) {
    rewardList.add(new ProbableReward(probability, reward));
  }

  public int randomFor(int low, int high) {
    if (high == low) return high;

    return new SecureRandom().nextInt(high - low) + low;
  }

  public List<Integer> randomSub(int size) {
    List<Integer> answer = new ArrayList<>();

    int startPosition = 0;
    if (rewardList.size() - size - 1 >= 0) {
      startPosition = randomFor(0, rewardList.size() - size - 1);
    }

    for (int i = startPosition; i < Math.min(rewardList.size(), startPosition + size); i++) {
      answer.add(i);
    }

    return answer;
  }

  @Override
  public Collection<Integer> generate(UUID player) {
    List<Integer> answer = new ArrayList<>();

    for (int i = 0; i < rewardList.size(); i++) {
      int random = new SecureRandom().nextInt(100);
      if (random <= rewardList.get(i).probability) {
        answer.add(i);
      }
    }

    return answer;
  }

  @Override
  public Collection<Integer> grab(int limit) {
    int serials = randomFor(limit / 2, limit);
    List<Integer> crateRewards = new ArrayList<>(randomSub(serials));

    for (int i = 0; i < limit - serials; i++) {
      crateRewards.add(new SecureRandom().nextInt(rewardList.size()));
    }

    Collections.shuffle(crateRewards);
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
