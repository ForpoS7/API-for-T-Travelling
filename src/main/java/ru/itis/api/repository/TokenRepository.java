package ru.itis.api.repository;

import org.springframework.data.keyvalue.repository.KeyValueRepository;
import ru.itis.api.entity.redis.RefreshToken;

public interface TokenRepository extends KeyValueRepository<RefreshToken, String> {
}
