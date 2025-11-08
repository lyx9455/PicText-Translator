package com.example.utils;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

/**
 * 限流通用工具
 * 支持单次冷却、频率升级、周期限制等多种限流策略。
 */
@Slf4j
@Component
public class FlowUtils {

    @Resource
    private StringRedisTemplate template;

    /**
     * 单次请求冷却限制：
     * 请求成功后，在 blockTime 秒内不得再次请求。
     * @param key       Redis键
     * @param blockTime 冷却时间（秒）
     * @return 是否允许请求（true = 允许）
     */
    public boolean limitOnceCheck(String key, int blockTime) {
        return internalCheck(key, 1, blockTime, overclock -> false);
    }

    /**
     * 单次请求冷却 + 违规加重惩罚限制：
     * 若冷却时间内重复请求，将触发更长的封禁期。
     * @param key         Redis键
     * @param frequency   请求频率
     * @param baseTime    基础冷却时间（秒）
     * @param upgradeTime 升级封禁时间（秒）
     * @return 是否允许请求（true = 允许）
     */
    public boolean limitOnceUpgradeCheck(String key, int frequency, int baseTime, int upgradeTime) {
        return internalCheck(key, frequency, baseTime, overclock -> {
            if (overclock) {
                template.opsForValue().set(key, "1", upgradeTime, TimeUnit.SECONDS);
                log.debug("【限流升级】key={} | 冷却时间从 {}s 升级为 {}s", key, baseTime, upgradeTime);
            }
            return false;
        });
    }

    /**
     * 时间段内多次请求限制：
     * 例如：3秒内最多请求20次，超出后封禁 blockTime 秒。
     * @param counterKey 计数键
     * @param blockKey   封禁标识键
     * @param blockTime  封禁时间（秒）
     * @param frequency  允许的最大次数
     * @param period     计数周期（秒）
     * @return 是否允许请求（true = 允许）
     */
    public boolean limitPeriodCheck(String counterKey, String blockKey, int blockTime, int frequency, int period) {
        return internalCheck(counterKey, frequency, period, overclock -> {
            if (overclock) {
                template.opsForValue().set(blockKey, "", blockTime, TimeUnit.SECONDS);
                log.debug("【周期限流】key={} | 已封禁 {} 秒", blockKey, blockTime);
            }
            return !overclock;
        });
    }

    /**
     * 限流核心逻辑。
     * @param key       Redis键
     * @param frequency 允许的最大次数
     * @param period    统计周期（秒）
     * @param action    超限时执行策略
     * @return 是否允许请求（true = 允许）
     */
    private boolean internalCheck(String key, int frequency, int period, LimitAction action) {
        String count = template.opsForValue().get(key);
        log.debug("【限流检查】key={} | 当前count={}", key, count);

        // 第一次请求
        if (count == null) {
            template.opsForValue().set(key, "1", period, TimeUnit.SECONDS);
            log.debug("【限流检查】key={} | 第一次请求，设置冷却 {} 秒", key, period);
            return true;
        }

        // 增加计数
        long value = Optional.ofNullable(template.opsForValue().increment(key)).orElse(0L);
        int current = Integer.parseInt(count);

        // 确保键在周期内存在
        if (value != current + 1) {
            template.expire(key, period, TimeUnit.SECONDS);
        }

        boolean overclock = value > frequency;
        if (overclock) {
            log.debug("【限流触发】key={} | 当前值={} > 限制值={}", key, value, frequency);
        }

        boolean result = action.run(overclock);
        log.debug("【限流结果】key={} | overclock={} | 允许请求={}", key, overclock, result);
        return result;
    }

    /**
     * 限制行为接口
     */
    private interface LimitAction {
        boolean run(boolean overclock);
    }
}
