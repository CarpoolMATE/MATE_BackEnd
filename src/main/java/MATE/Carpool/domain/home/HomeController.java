package MATE.Carpool.domain.home;

import MATE.Carpool.config.redis.RedisService;
import MATE.Carpool.config.userDetails.CustomUserDetails;
import jakarta.annotation.security.PermitAll;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequiredArgsConstructor
public class HomeController {

    private final RedisService redisService;

    @PermitAll
    @GetMapping("/")
    public String index() {
        return "카풀 메이트 API 서버";
    }

    @GetMapping("/ip")
    public void ipTest(HttpServletRequest request){
        String ip = request.getHeader("X-Forwarded-For");
        if (ip != null && ip.contains(",")) {
            ip = ip.split(",")[0]; 
        }
        log.info("X-Forwarded-For : " + ip);
    
        if (ip == null) {
            ip = request.getHeader("Proxy-Client-IP");
            log.info("Proxy-Client-IP : " + ip);
        }
        if (ip == null) {
            ip = request.getHeader("WL-Proxy-Client-IP");
            log.info("WL-Proxy-Client-IP : " + ip);
        }
        if (ip == null) {
            ip = request.getHeader("HTTP_CLIENT_IP");
            log.info("HTTP_CLIENT_IP : " + ip);
        }
        if (ip == null) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
            log.info("HTTP_X_FORWARDED_FOR : " + ip);
        }
        if (ip == null) {
            ip = request.getRemoteAddr();
            log.info("getRemoteAddr : " + ip);
        }
        log.info("Result : IP Address : " + ip);
    }

    @GetMapping("/ip2")
    public void ipTest2(@AuthenticationPrincipal CustomUserDetails userDetails) {

        redisService.testRedis(userDetails.getUsername());
    }



    
   @PostMapping("/")
    public ResponseEntity<String> post(@RequestBody String test){
        return ResponseEntity.ok("postMapping"+test);
    }
}

