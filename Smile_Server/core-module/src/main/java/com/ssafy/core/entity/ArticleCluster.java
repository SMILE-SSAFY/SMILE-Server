package com.ssafy.core.entity;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import java.util.List;

/***
 * @author 신민철
 * Redis에 저장할 Entity
 */
@Data
@RedisHash(value = "markers", timeToLive = 1800)
@Builder
public class ArticleCluster {
    @Id
    private Long id;

    private Long userId;
}
