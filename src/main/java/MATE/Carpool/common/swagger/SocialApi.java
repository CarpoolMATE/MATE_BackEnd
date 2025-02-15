package MATE.Carpool.common.swagger;


import MATE.Carpool.domain.member.dto.response.MemberResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestParam;

@Tag(name = "소셜로그인 API")
public interface SocialApi {


    @Operation(
            summary = "회원가입 - 소셜로그인(카카오)",
            description = "카카오 소셜 회원가입 API입니다. 카카오에서 받은 인가 코드를 이용하여 액세스 토큰을 발급받고 사용자 정보를 조회합니다.",
            parameters = {
                    @Parameter(name = "code", description = "카카오에서 발급한 인가 코드", required = true, in = ParameterIn.QUERY, example = "abcdef1234567890"),
            }
    )
    @ApiResponse(
            responseCode = "200",
            description = "로그인 성공",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = MemberResponseDto.class))
    )
    @ApiResponse(
            responseCode = "400",
            description = "잘못된 요청 (code가 없거나 유효하지 않음)",
            content = @Content(mediaType = "application/json")
    )
    ResponseEntity<MemberResponseDto> kakaoCallback(@RequestParam("code") String code, HttpServletResponse response, HttpServletRequest request)throws Exception;


    @Operation(
            summary = "회원가입 - 소셜로그인(라인)",
            description = "라인 소셜 회원가입 API입니다. 라인에서 받은 인가 코드를 이용하여 액세스 토큰을 발급받고 사용자 정보를 조회합니다.",
            parameters = {
                    @Parameter(name = "code", description = "라인에서 발급한 인가 코드", required = true, in = ParameterIn.QUERY, example = "abcdef1234567890"),
            }
    )
    @ApiResponse(
            responseCode = "200",
            description = "로그인 성공",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = MemberResponseDto.class))
    )
    @ApiResponse(
            responseCode = "400",
            description = "잘못된 요청 (code가 없거나 유효하지 않음)",
            content = @Content(mediaType = "application/json")
    )
    ResponseEntity<MemberResponseDto> lineCallback(@RequestParam("code") String code, HttpServletResponse response, HttpServletRequest request)throws Exception;


}
