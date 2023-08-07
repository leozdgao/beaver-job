package me.leozdgao.beaver.spi;

import me.leozdgao.beaver.spi.model.Task;

public interface TaskPersistenceQueryService {
    Task findTaskById(Long id);
}
