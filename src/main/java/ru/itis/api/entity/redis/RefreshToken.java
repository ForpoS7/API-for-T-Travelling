package ru.itis.api.entity.redis;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

@Data
@AllArgsConstructor
@NoArgsConstructor
@RedisHash("refresh_token")
@Accessors(chain = true)
public class RefreshToken {

    @Id
    private String id;

    private String rawToken;

}
