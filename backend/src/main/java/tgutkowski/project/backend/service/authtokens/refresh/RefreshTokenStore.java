package tgutkowski.project.backend.service.authtokens.refresh;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Optional;

@Service
public class RefreshTokenStore {

    private final RefreshTokenUtil refreshTokenUtil;
    private final StringRedisTemplate redis;

    @Value("${refresh.token.expiration.milliseconds}")
    private Long expiration;

    public RefreshTokenStore(RefreshTokenUtil refreshTokenUtil, StringRedisTemplate redis) {
        this.refreshTokenUtil = refreshTokenUtil;
        this.redis = redis;
    }

    public String issueToken(long userId) {
        String token = refreshTokenUtil.generateRefreshToken();
        String key = refreshTokenUtil.toRedisKey(token);
        redis.opsForValue().set(key, String.valueOf(userId), Duration.ofMillis(expiration));
        return token;
    }

    public Optional<Long> findTokenOwner(String token) {
        String key = refreshTokenUtil.toRedisKey(token);
        String userId = redis.opsForValue().get(key);
        return Optional.ofNullable(userId).map(Long::valueOf);
    }

    public void invalidateToken(String token) {
        String key = refreshTokenUtil.toRedisKey(token);
        redis.delete(key);
    }
}
