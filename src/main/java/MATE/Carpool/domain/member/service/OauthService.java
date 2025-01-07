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
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;
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



    public ResponseEntity<MemberResponseDto> socialLogin(String provider,String code, HttpServletResponse response,String clientId) throws Exception {
        
        //code는 받아왔음
        //accessToken 요청
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


    //RestTemplate > Map 구조를 해석할수 없어서 "org.springframework.web.client.RestClientException: No HttpMessageConverter for java.util.HashMap and content type "application/x-www-form-urlencoded"
    //MultiValueMap 로 바꿔서 해결
    public String getAccessTokenFromLine(String provider,String code, HttpServletResponse response , String clientID) throws Exception {

        String tokenUrl ="https://api.line.me/oauth2/v2.1/token";
        MultiValueMap<String, String > params = new LinkedMultiValueMap<>();

        params.add("client_id", clientID);
        params.add("grant_type", "authorization_code");
        params.add("code", code);
        params.add("redirect_uri", "http://localhost:8080/api/social/line/callback");
        params.add("client_secret","1bbd05a2c3cf5ab1a1501b2eae5fd992");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);

        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> responseEntity = restTemplate.postForEntity(tokenUrl, request, String.class);


        String responseBody = responseEntity.getBody();
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.readTree(responseBody);

        System.out.println("accessToken : "+jsonNode.get("access_token").asText());
        String accessToken = jsonNode.get("access_token").asText();

        return getProfileTokenFromLine(accessToken);
    }


    public String getProfileTokenFromLine(String accessToken) throws Exception {

        String profile ="https://api.line.me/v2/profile";
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + accessToken);

        HttpEntity<String> request = new HttpEntity<>(headers);

        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> responseEntity = restTemplate.postForEntity(profile, request, String.class);

        String responseBody = responseEntity.getBody();
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.readTree(responseBody);

        jsonNode.get("userId").asText();
        String nickname =jsonNode.get("displayName").asText();

        //회원가입 시키고

        //인증객체 만들고

        //accessToken , RefreshToken 발급


        System.out.println(jsonNode.toString());

        return null;
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
