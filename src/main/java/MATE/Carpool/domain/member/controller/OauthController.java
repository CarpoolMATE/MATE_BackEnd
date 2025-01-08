package MATE.Carpool.domain.member.controller;


import MATE.Carpool.domain.member.dto.response.MemberResponseDto;
import MATE.Carpool.domain.member.service.OauthService;
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

    @GetMapping("/kakao/callback")
    public ResponseEntity<MemberResponseDto> kakaoCallback(@RequestParam("code") String code, HttpServletResponse response) throws Exception {
            System.out.println(code);
        return oauthService.socialLogin("KAKAO",code,response);
    }
    @GetMapping("/line/callback")
    public ResponseEntity<MemberResponseDto> lineCallback(@RequestParam("code") String code, HttpServletResponse response) throws Exception {
        System.out.println(code);
        return oauthService.socialLogin("LINE",code,response);
    }


}
