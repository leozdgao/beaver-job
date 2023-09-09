package me.leozdgao.beaver.worker.lb;

import me.leozdgao.beaver.worker.Worker;

import java.util.List;
import java.util.Random;

/**
 * 随机取
 * @author leozdgao
 */
public class RandomLoadBalancer implements WorkerLoadBalancer {
    private final Random random = new Random();

    @Override
    public Worker getNextAvailable(List<Worker> workers) {
        if (workers == null || workers.size() == 0) {
            return null;
        }

        int index = random.nextInt(workers.size());
        return workers.get(index);
    }
}
