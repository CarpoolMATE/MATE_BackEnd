package MATE.Carpool.domain.member.service;


import MATE.Carpool.common.Message;
import MATE.Carpool.config.jwt.JwtProvider;
import MATE.Carpool.config.userDetails.CustomUserDetails;
import MATE.Carpool.domain.member.dto.request.SocialMemberInfoDto;
import MATE.Carpool.domain.member.dto.response.MemberResponseDto;
import MATE.Carpool.domain.member.entity.Member;
import MATE.Carpool.domain.member.entity.ProviderType;
import MATE.Carpool.domain.member.repository.MemberRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class OauthService {

    @Value("${oauth.line.client_id}")
    private String lineClientId;
    @Value("${oauth.kakao.client_id}")
    private String kakaoClientId;

    @Value("${oauth.line.secret_id}")
    private String lineSecretKey;
    @Value("${oauth.kakao.secret_id}")
    private String kakaoSecretKey;

    @Value("${oauth.kakao.redirect_uri}")
    private String kakaoRedirectUri;
    @Value("${oauth.line.redirect_uri}")
    private String lineRedirectUri;

    private final JwtProvider jwtProvider;
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    private final static String KAKAO_TOKEN_URL_HOST="https://kauth.kakao.com/oauth/token";
    private final static String LINE_TOKEN_URL_HOST="https://api.line.me/oauth2/v2.1/token";


    public ResponseEntity<Message<MemberResponseDto>> socialLogin(String provider, String code, HttpServletResponse response, HttpServletRequest request ) throws JsonProcessingException {

        String accessKey = getAccessKey(provider, code, response);

        SocialMemberInfoDto socialMemberInfoDto = getMemberInfo(provider , accessKey);

        Member member  = registerMember(provider , socialMemberInfoDto);

        Authentication authentication = forceLogin(member);

//        jwtProvider.createTokenAndSavedTokenHttponly(authentication, response, request, member.getMemberId());
        jwtProvider.createTokenAndSaved(authentication, response,request);

        MemberResponseDto memberResponseDto = new MemberResponseDto(member);

        log.info(memberResponseDto.toString());

        return  ResponseEntity.ok(new Message<>("소셜 회원가입 성공",HttpStatus.OK,memberResponseDto));

    }

    public String getAccessKey(String provider, String code, HttpServletResponse response) throws JsonProcessingException {
        Map<String, String> providers = new HashMap<>();
        providers.put("providerUrl", provider.equals("KAKAO") ? KAKAO_TOKEN_URL_HOST : LINE_TOKEN_URL_HOST);
        providers.put("clientId", provider.equals("KAKAO") ? kakaoClientId : lineClientId);
        providers.put("clientSecret", provider.equals("KAKAO") ? kakaoSecretKey : lineSecretKey);
        providers.put("redirect",provider.equals("KAKAO") ? kakaoRedirectUri : lineRedirectUri);

        MultiValueMap<String , String > params= new LinkedMultiValueMap<>();

        params.add("client_id", providers.get("clientId"));
        params.add("grant_type", "authorization_code");
        params.add("code", code);
        params.add("redirect_uri", providers.get("redirect"));
        params.add("client_secret",providers.get("clientSecret"));

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);

        RestTemplate restTemplate = new RestTemplate();

        try {
            ResponseEntity<String> responseEntity = restTemplate.postForEntity(providers.get("providerUrl"), request, String.class);
            String responseBody = responseEntity.getBody();
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(responseBody);

            JsonNode accessTokenNode = jsonNode.get("access_token");
            if (accessTokenNode != null) {
                return accessTokenNode.asText();
            } else {
                throw new RuntimeException("토큰을 발급받지 못했습니다.");
            }
        } catch (Exception e) {
            throw new RuntimeException("소셜로그인중 오류가 발생했습니다. 관리자에게 문의해주세요.", e);
        }

    }

    public SocialMemberInfoDto getMemberInfo(String provider ,String accessToken) throws JsonProcessingException {


        // TODO : 구글까지온다면?
        String profile = provider == "KAKAO" ?"https://kapi.kakao.com/v2/user/me" : "https://api.line.me/v2/profile";
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + accessToken);

        HttpEntity<String> request = new HttpEntity<>(headers);

        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> responseEntity = restTemplate.postForEntity(profile, request, String.class);

        String responseBody = responseEntity.getBody();
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.readTree(responseBody);

        if(provider.equals("KAKAO")){
            return SocialMemberInfoDto.builder()
                    .username(jsonNode.get("id").asText())
                    .nickname(jsonNode.get("properties").get("nickname").asText())
                    .profileImage(jsonNode.get("properties").get("profile_image").asText())
                    .email(jsonNode.get("properties").get("nickname").asText()+"_kakao")
                    .build();

        }else{
            return SocialMemberInfoDto.builder()
                    .username(jsonNode.get("userId").asText())
                    .nickname(jsonNode.get("displayName").asText())
                    .profileImage("profile_image")
                    .email(jsonNode.get("userId").asText() + "@line.com")
                    .build();
        }

    }

    private Member registerMember(String provider ,SocialMemberInfoDto socialMemberInfoDto) {
        Optional<Member> member = memberRepository.findByMemberId(socialMemberInfoDto.getUsername());
        String originalNickname = socialMemberInfoDto.getNickname();
        String nickname = originalNickname;
        int suffix = 1;
        if(member.isEmpty()){
            while (memberRepository.existsByNickname(nickname)) {
                nickname = originalNickname + suffix;
                suffix++;
            }
               Member fMember = Member.builder()
                        .memberId(socialMemberInfoDto.getUsername())
                        .email(socialMemberInfoDto.getEmail()+"_"+ LocalDateTime.now())
                        .password(UUID.randomUUID().toString())
                        .providerType(provider.equals("KAKAO") ? ProviderType.KAKAO: ProviderType.LINE)
                        .nickname(nickname)
                        .build();
                memberRepository.save(fMember);
                return fMember;
        }
        return member.get();

    }

    private Authentication forceLogin(Member member) {

        CustomUserDetails userDetails = new CustomUserDetails(member);

        Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

        SecurityContextHolder.getContext().setAuthentication(authentication);

        return authentication;

    }
}
