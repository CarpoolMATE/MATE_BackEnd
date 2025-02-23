package MATE.Carpool.common.swagger;

import MATE.Carpool.common.Message;
import MATE.Carpool.config.userDetails.CustomUserDetails;
import MATE.Carpool.domain.carpool.dto.response.CarpoolResponseDTO;
import MATE.Carpool.domain.carpool.dto.response.CarpoolWithPassengersDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;

import java.util.List;

@Tag(name = "카풀 API")
public interface CarpoolApi {

    @Operation(summary = "카풀 목록 조회", description = "기본 카풀 목록 조회, 최신순으로 카풀 정보 제공")
    @ApiResponse(responseCode = "200", content = @Content(mediaType = "application/json", examples = {
            @ExampleObject(name = "카풀 조회 성공(empty)", value = """
                               {
                         "message": "카풀 조회 성공",
                         "status": "OK",
                         "data": []
                     }
                     """),
            @ExampleObject(name = "카풀 조회 성공", value = """
                               {
                         "message": "카풀 조회 성공",
                         "status": "OK",
                         "data": []
                     }
                     """),

    }))
    public ResponseEntity<Message<List<CarpoolResponseDTO>>> carpoolList();

    @Operation(summary = "카풀 생성", description = "드라이버가 카풀을 생성합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", content = @Content(mediaType = "application/json", examples = {
                    @ExampleObject(name = "카풀생성 성공", value = """
        {
                "message": "카풀 생성 성공",
                "status": "OK",
                "data": {
                    "carpoolId": 1,
                    "driverName": "닉네임",
                    "driverImg": "https://carool-s3.s3.ap-northeast-2.amazonaws.com/profileImgS3.png",
                    "carImg": "test.img",
                    "carNumber": "12가 3456",
                    "departureCoordinate": "경기도 수원시 권선구 오목천동",
                    "latitude": null,
                    "longitude": null,
                    "departureTime": "2025-02-23T11:05:29.288",
                    "chatLink": "kakao.com",
                    "capacity": 4,
                    "cost": 2000,
                    "reservationCount": 0
        }
    }
    """)
            })),
            @ApiResponse(responseCode = "400", content = @Content(mediaType = "application/json", examples = {
                    @ExampleObject(name = "생성 실패 - 드라이버만 생성 가능", value = """
                            {
                                 "status": 400,
                                 "name": "ONLY_POSSIBLE_DRIVER",
                                 "code": "MATE-006",
                                 "message": "드라이버만 카풀을 등록할 수 있습니다."
                             }
            """),
                    @ExampleObject(name = "생성 실패 - 카풀 생성 수 제한", value = """
                            {
                                 "status": 400,
                                 "name": "ALREADY_CARPOOL_EXIST",
                                 "code": "MATE-005",
                                 "message": "카풀은 하나만 등록할 수 있습니다."
                             }
            """),
                    @ExampleObject(name = "검증 실패 - 중복 아이디", value = """
                            {
                                 "status": 400,
                                 "name": "DUPLICATE_MEMBER_ID",
                                 "code": "ACCOUNT-003",
                                 "message": "존재하는 아이디 입니다."
                             }
            """),
                    @ExampleObject(name = "검증 실패 - 중복 이메일", value = """
                            {
                                  "status": 400,
                                  "name": "DUPLICATE_EMAIL",
                                  "code": "ACCOUNT-002",
                                  "message": "존재하는 이메일입니다."
                              }
            """),
                    @ExampleObject(name = "검증 실패 - 중복 이메일", value = """
                           {
                               "status": 400,
                               "name": "DUPLICATE_NICKNAME",
                               "code": "ACCOUNT-004",
                               "message": "존재하는 닉네임 입니다."
                           }
            """)
            })),

    })
    public ResponseEntity<Message<CarpoolWithPassengersDTO>> myCarpool(@AuthenticationPrincipal CustomUserDetails userDetails);

    @Operation(summary = "카풀 삭제", description = "드라이버가 생성한 카풀 삭제")
    @ApiResponses({
            @ApiResponse(responseCode = "200", content = @Content(mediaType = "application/json", examples = {
                    @ExampleObject(name = "카풀 삭제 성공", value = """
                {
                     "message": "카풀 삭제 성공",
                     "status": "OK",
                     "data": "성공"
                 }
                """)
            })),
            @ApiResponse(responseCode = "400", content = @Content(mediaType = "application/json", examples = {
                    @ExampleObject(name = "생성 실패 - 드라이버만 생성 가능", value = """
                {
                     "status": 400,
                     "name": "ONLY_POSSIBLE_DRIVER",
                     "code": "MATE-006",
                     "message": "드라이버만 카풀을 등록할 수 있습니다."
                 }
                """),
                    @ExampleObject(name = "삭제 실패 - 생성된 카풀이 없을 경우", value = """
                {
                      "status": 400,
                      "name": "BAD_REQUEST",
                      "code": "",
                      "message": "잘못된 요청입니다."
                }
            """),
                    @ExampleObject(name = "삭제 실패 - 생성된 카풀이 없을 경우", value = """
                {
                      "status": 400,
                      "name": "BAD_REQUEST",
                      "code": "",
                      "message": "잘못된 요청입니다."
                }
            """)
            }))
    })
    public ResponseEntity<Message<String>> deleteCarpool(@AuthenticationPrincipal CustomUserDetails userDetails);
}