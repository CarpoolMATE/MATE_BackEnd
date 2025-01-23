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

    public void saveRefreshToken(String memberId, String refreshToken,String ip, long duration) {
        String key = "refresh:" + memberId;

        ObjectMapper om =new ObjectMapper();



        try{
            Map<String,Object> data = new HashMap<>();
            data.put("ip" , ip);
            data.put("token", refreshToken);

            Map<String,Object> wrap = new HashMap<>();
            wrap.put("data", data);

            String jsonValue = om.writeValueAsString(wrap);

            redisTemplate.opsForValue().set(key, jsonValue, duration, TimeUnit.MILLISECONDS);

        }catch (Exception e){
            e.printStackTrace();
        }


    }

    //Token 조회
    public UserIpTokenDto getRefreshToken(String memberId) {
        String key = "refresh:" + memberId;

        String jsonValue = (String) redisTemplate.opsForValue().get(key);

        if (jsonValue == null) {
            throw new RuntimeException("키를 찾을 수 없습니다.: " + key);
        }

        ObjectMapper objectMapper = new ObjectMapper();
        try {
            // JSON을 Map으로 변환
            Map<String, Object> map = objectMapper.readValue(jsonValue, Map.class);
            Map<String, Object> data = (Map<String, Object>) map.get("data");

            // ip와 token을 추출
            String ip = (String) data.get("ip");
            String token = (String) data.get("token");

            return new UserIpTokenDto(ip, token); // ip와 token으로 UserIpTokenDto 객체 생성
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("키를 JSON 변환에 실패하였습니다.", e);
        }
    }

    //test를위한 메서드
    public void testRedis(String memberId) {
        String key = "refresh:" + memberId;

        String jsonValue = (String) redisTemplate.opsForValue().get(key);

        ObjectMapper objectMapper = new ObjectMapper();

        try {
            Optional.ofNullable(jsonValue)
                    .map(value -> {
                        try {
                            return objectMapper.readValue(value, Map.class);
                        } catch (Exception e) {
                            throw new RuntimeException("JSON 변환 실패", e);
                        }
                    })
                    .map(map -> (Map<String, Object>) map.get("data"))
                    .map(data -> new UserIpTokenDto(
                            (String) data.get("ip"),  // IP 추출
                            (String) data.get("token")  // token 추출
                    ))
                    .ifPresent(userIpTokenDto -> {
                        log.info("ip : {}", userIpTokenDto.getIp());
                        log.info("token frontFive: {}", userIpTokenDto.getToken().substring(0, 5));
                        log.info("token backFive: {}", userIpTokenDto.getToken().substring(userIpTokenDto.getToken().length() - 5));

                    });
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("키를 JSON 변환에 실패하였습니다.", e);
        }
    }




    //Token 삭제
    public void deleteRefreshToken(String memberId) {
        String key = "refresh:" + memberId;
        redisTemplate.delete(key);
    }


}
