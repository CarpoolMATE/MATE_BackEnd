package MATE.Carpool.config.redis;


import MATE.Carpool.config.jwt.UserIpTokenDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Slf4j
public class RedisService {

    private final RedisTemplate<String, Object> redisTemplate;

    public void saveRefreshToken(String memberId, String refreshToken, String ip, long duration) {
        String key = "refresh:" + memberId;

        try {
            if (refreshToken.startsWith("Bearer ")) {
                refreshToken = refreshToken.replaceFirst("^Bearer ", "");
            }
            UserIpTokenDto token = new UserIpTokenDto(ip,refreshToken);
            redisTemplate.opsForValue().set(key, token, duration, TimeUnit.MILLISECONDS);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //Token 조회
    public UserIpTokenDto getRefreshToken(String memberId) {
        String key = "refresh:" + memberId;

        UserIpTokenDto token =  (UserIpTokenDto) redisTemplate.opsForValue().get(key);

        if (token == null) {
            throw new RuntimeException("키를 찾을 수 없습니다.: " + key);
        }

        return token;
    }


    //Token 삭제
    public void deleteRefreshToken(String memberId) {
        String key = "refresh:" + memberId;
        redisTemplate.delete(key);
    }


}
