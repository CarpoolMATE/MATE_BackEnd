package MATE.Carpool.domain.member.controller;


import MATE.Carpool.domain.member.dto.response.MemberResponseDto;
import MATE.Carpool.domain.member.service.OauthService;
import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/api/social")
@RequiredArgsConstructor
public class OauthController {

    private final OauthService oauthService;

    @Value("${oauth.kakao.client_id}")
    private String client_id;
    @Value("${oauth.kakao.redirect_uri}")
    private String redirect_uri;

    
    //프론트작업
    @GetMapping("/test")
    public String test(Model model) {
        String location = "https://kauth.kakao.com/oauth/authorize?response_type=code&client_id="+client_id+"&redirect_uri="+redirect_uri;
        model.addAttribute("location", location);
        System.out.println(location);

        return "socialLogin";
    }
    @GetMapping("/test2")
    public ResponseEntity<MemberResponseDto> callback2(@RequestParam("access_token") String access_token, HttpServletResponse response) throws Exception {
        System.out.println(access_token);
        return oauthService.kakaoLogin2(access_token,response,client_id);
    }



    @GetMapping("/kakao/callback")
    public ResponseEntity<MemberResponseDto> kakaoCallback(@RequestParam("code") String code, HttpServletResponse response) throws Exception {
            System.out.println(code);
        return oauthService.socialLogin("KAKAO",code,response,client_id);
    }
    @GetMapping("/line/callback")
    public String lineCallback(@RequestParam("code") String code, HttpServletResponse response) throws Exception {
        System.out.println(code);
        return oauthService.getAccessTokenFromLine("LINE",code,response,"2006743266");
//        return oauthService.socialLogin("LINE",code,response,client_id);
    }


}
