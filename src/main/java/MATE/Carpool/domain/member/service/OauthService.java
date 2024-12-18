package MATE.Carpool.domain.member.service;


import MATE.Carpool.common.PKEncryption;
import MATE.Carpool.config.jwt.JwtProvider;
import MATE.Carpool.config.jwt.JwtTokenDto;
import MATE.Carpool.config.jwt.RefreshToken;
import MATE.Carpool.config.jwt.RefreshTokenRepository;
import MATE.Carpool.config.userDetails.CustomUserDetails;
import MATE.Carpool.domain.member.dto.request.SocialMemberInfoDto;
import MATE.Carpool.domain.member.dto.response.KakaoTokenResponseDto;
import MATE.Carpool.domain.member.dto.response.MemberResponseDto;
import MATE.Carpool.domain.member.entity.Member;
import MATE.Carpool.domain.member.entity.ProviderType;
import MATE.Carpool.domain.member.repository.MemberRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.netty.handler.codec.http.HttpHeaderValues;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Optional;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class OauthService {

    @Value("${jwt.refresh.time}")
    private long refreshTimeMillis;

    private final JwtProvider jwtProvider;
    private final MemberRepository memberRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PKEncryption pkEncryption;

    private final static String KAUTH_TOKEN_URL_HOST="https://kauth.kakao.com";



    public ResponseEntity<MemberResponseDto> kakaoLogin(String code, HttpServletResponse response,String clientId) throws Exception {
        String accessKey= getAccessTokenFromKakao(code,clientId);
        SocialMemberInfoDto kakaoMemberInfoDto = getSocialMemberInfo(accessKey);

        Member member = registerKakaoMemberIfNeeded(kakaoMemberInfoDto);
        Authentication authentication=forceLogin(member);
        jwtProvider.createTokenAndSavedRefresh(authentication,response,member.getMemberId());

        MemberResponseDto memberResponseDto = new MemberResponseDto(member);


        log.info(memberResponseDto.toString());
        return  ResponseEntity.ok(memberResponseDto);
    }


    public ResponseEntity<MemberResponseDto> kakaoLogin2(String accessKey, HttpServletResponse response,String clientId) throws Exception {
        SocialMemberInfoDto kakaoMemberInfoDto = getSocialMemberInfo(accessKey);

        Member member = registerKakaoMemberIfNeeded(kakaoMemberInfoDto);
        Authentication authentication=forceLogin(member);
        jwtProvider.createTokenAndSavedRefresh(authentication,response,member.getMemberId());

        MemberResponseDto memberResponseDto = new MemberResponseDto(member);


        log.info(memberResponseDto.toString());
        return  ResponseEntity.ok(memberResponseDto);
    }


    public String getAccessTokenFromKakao(String code,String clientId) {

        KakaoTokenResponseDto kakaoTokenResponseDto = WebClient.create(KAUTH_TOKEN_URL_HOST).post()
                .uri(uriBuilder -> uriBuilder
                        .scheme("https")
                        .path("/oauth/token")
                        .queryParam("grant_type", "authorization_code")
                        .queryParam("client_id", clientId)
                        .queryParam("code", code)
                        .build(true))
                .header(HttpHeaders.CONTENT_TYPE, HttpHeaderValues.APPLICATION_X_WWW_FORM_URLENCODED.toString())
                .retrieve()
                .bodyToMono(KakaoTokenResponseDto.class)
                .block();

        assert kakaoTokenResponseDto != null;
        return kakaoTokenResponseDto.getAccessToken();
    }


    private SocialMemberInfoDto getSocialMemberInfo(String accessToken) throws JsonProcessingException {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + accessToken);
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        HttpEntity<MultiValueMap<String, String>> kakaoUserInfoRequest = new HttpEntity<>(headers);
        RestTemplate rt = new RestTemplate();
        ResponseEntity<String> response = rt.exchange(
                "https://kapi.kakao.com/v2/user/me",
                HttpMethod.GET,
                kakaoUserInfoRequest,
                String.class
        );

        if (response.getStatusCode() != HttpStatus.OK) {
            throw new RestClientException("카카오 서버가 원활하지 않음. Status : " + response.getStatusCode());
        }

        String responseBody = response.getBody();
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.readTree(responseBody);

        System.out.println(jsonNode);


        return SocialMemberInfoDto.builder()
                .nickname(jsonNode.get("properties").get("nickname").asText())
                .profileImage(jsonNode.get("properties").get("profile_image").asText())
                .email("test@email.com")
                .build();
    }

    private Member registerKakaoMemberIfNeeded(SocialMemberInfoDto socialMemberInfoDto) {
        Optional<Member> member = memberRepository.findByEmail(socialMemberInfoDto.getEmail());
        Member fMember = null;
        if (member.isEmpty()) {
            fMember = Member.builder()
                    .memberId(socialMemberInfoDto.getNickname()+UUID.randomUUID().toString())
                    .email(socialMemberInfoDto.getEmail())
                    .password(UUID.randomUUID().toString())
                    .providerType(ProviderType.KAKAO)
                    .nickname(socialMemberInfoDto.getNickname())
                    .build();

            memberRepository.save(fMember);
        }
        return fMember;
    }

    private Authentication forceLogin(Member member) {

        CustomUserDetails userDetails = new CustomUserDetails(member);



        Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);
        return authentication;

    }
}
