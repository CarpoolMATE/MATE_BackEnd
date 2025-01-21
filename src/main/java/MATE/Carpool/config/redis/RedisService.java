package MATE.Carpool.config.redis;


import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class RedisService {

    private final RedisTemplate<String, Object> redisTemplate;

    public void saveRefreshToken(String memberId, String refreshToken, long duration) {
        String key = "refresh:" + memberId;
        redisTemplate.opsForValue().set(key, refreshToken, duration, TimeUnit.MILLISECONDS);
    }

    //Token 조회
    public String getRefreshToken(String memberId) {
        String key = "refresh:" + memberId;
        return (String) redisTemplate.opsForValue().get(key);
    }

    //Token 삭제
    public void deleteRefreshToken(String memberId) {
        String key = "refresh:" + memberId;
        redisTemplate.delete(key);
    }


}
