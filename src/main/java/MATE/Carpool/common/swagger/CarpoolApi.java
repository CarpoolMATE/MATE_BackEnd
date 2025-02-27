package MATE.Carpool.common.swagger;

import MATE.Carpool.common.Message;
import MATE.Carpool.config.userDetails.CustomUserDetails;
import MATE.Carpool.domain.carpool.dto.request.CarpoolRequestDTO;
import MATE.Carpool.domain.carpool.dto.request.ReservationCarpoolRequestDTO;
import MATE.Carpool.domain.carpool.dto.response.CarpoolHistoryResponseDTO;
import MATE.Carpool.domain.carpool.dto.response.CarpoolResponseDTO;
import MATE.Carpool.domain.carpool.dto.response.CarpoolWithPassengersDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@Tag(name = "카풀 API")
public interface CarpoolApi {

    //TODO: OK
    @Operation(summary = "카풀 목록 조회", description = "기본 카풀 목록 조회, 최신순으로 카풀 정보 제공")
    @ApiResponse(responseCode = "200", content = @Content(mediaType = "application/json", examples = {
            @ExampleObject(name = "카풀 조회 성공", value = """
                    {
                        "message": "카풀 조회 성공",
                        "status": "OK",
                        "data": [
                            {
                                "carpoolId": 1,
                                "driverName": "닉네임112",
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
                            },
                            {
                                "carpoolId": 2,
                                "driverName": "닉네임11",
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
                        ]
                    }
                    """),
            @ExampleObject(name = "카풀 조회 성공(empty)", value = """
                               {
                         "message": "카풀 조회 성공",
                         "status": "OK",
                         "data": []
                     }
                     """),

    }))
    public ResponseEntity<Message<List<CarpoolResponseDTO>>> carpoolList();

    //TODO: OK
    @Operation(summary = "카풀 생성", description = "드라이버가 카풀을 생성합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", content = @Content(mediaType = "application/json", examples = {
                    @ExampleObject(name = "카풀 생성 성공", value = """
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
                    @ExampleObject(name = "카풀 생성 실패 - 드라이버만 생성 가능", value = """
                            {
                                 "status": 400,
                                 "name": "ONLY_POSSIBLE_DRIVER",
                                 "code": "MATE-006",
                                 "message": "드라이버만 카풀을 등록할 수 있습니다."
                             }
            """),
                    @ExampleObject(name = "카풀 생성 실패 - 카풀 생성 수 제한", value = """
                            {
                                 "status": 400,
                                 "name": "ALREADY_CARPOOL_EXIST",
                                 "code": "MATE-005",
                                 "message": "카풀은 하나만 등록할 수 있습니다."
                             }
            """),
                    @ExampleObject(name = "카풀 생성 실패 - 생성에 필요한 파라미터가 부족한 경우", value = """
                            {
                                 "status": 400,
                                 "name": "BAD_REQUEST",
                                 "code": "입력값 오류",
                                 "message": "해당 오류를 출력함"
                             }
            """)
            })),
    })
    public ResponseEntity<Message<CarpoolResponseDTO>> makeCarpool(@AuthenticationPrincipal CustomUserDetails userDetails, @RequestBody CarpoolRequestDTO carpoolRequestDTO);

    //TODO: OK
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
                    @ExampleObject(name = "카풀 삭제 실패 - 생성된 카풀이 없을 경우", value = """
                {
                      "status": 404,
                      "name": "CARPOOL_NOT_FOUND",
                      "code": "MATE-001",
                      "message": "카풀 정보를 찾을 수 없습니다."
                }
            """), @ExampleObject(name = "카풀 삭제 실패 - 드라이버가 아님", value = """
                            {
                                "status": 400,
                                "name": "ONLY_DELETE_DRIVER",
                                "code": "MATE-007",
                                "message": "등록한 드라이버만 카풀을 삭제할 수 있습니다."
                            }
                """)
            }))
    })
    public ResponseEntity<Message<String>> deleteCarpool(@AuthenticationPrincipal CustomUserDetails userDetails);

    //TODO: OK
    @Operation(summary = "모집 중인 카풀 리스트", description = "모집 중인 카풀 보가")
    @ApiResponses({
            @ApiResponse(responseCode = "200", content = @Content(mediaType = "application/json", examples = {
                    @ExampleObject(name = "카풀 조회 성공", value = """
                            {
                                "message": "모집중인 카풀만 조화",
                                "status": "OK",
                                "data": [
                                    {
                                        "carpoolId": 1,
                                        "driverName": "닉네임112",
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
                                ]
                            }
                """)
            }))
    })
    public ResponseEntity<Message<List<CarpoolResponseDTO>>> onlyActiveCarpoolList();

    //TODO: OK
    @Operation(summary = "빠른 시간 카풀 조회", description = "출발 시간이 빠른 시간 순으로 카풀 조회")
    @ApiResponses({
            @ApiResponse(responseCode = "200", content = @Content(mediaType = "application/json", examples = {
                    @ExampleObject(name = "카풀 조회 성공", value = """
                            {
                                  "message": "모집중인 카풀만 조화",
                                  "status": "OK",
                                  "data": [
                                      {
                                          "carpoolId": 1,
                                          "driverName": "닉네임112",
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
                                      },
                                      {
                                          "carpoolId": 3,
                                          "driverName": "닉네임11",
                                          "driverImg": "https://carool-s3.s3.ap-northeast-2.amazonaws.com/profileImgS3.png",
                                          "carImg": "test.img",
                                          "carNumber": "12가 3456",
                                          "departureCoordinate": "경기도 수원시 권선구 오목천동",
                                          "latitude": null,
                                          "longitude": null,
                                          "departureTime": "2025-02-23T11:06:29.288",
                                          "chatLink": "kakao.com",
                                          "capacity": 4,
                                          "cost": 2000,
                                          "reservationCount": 0
                                      }
                                  ]
                              }
                """)
            }))
    })
    public ResponseEntity<Message<List<CarpoolResponseDTO>>> fastCarpoolList();

    //TODO: OK
    @Operation(summary = "낮은 가격 카풀 조회", description = "낮은 가격순으로 카풀 조회")
    @ApiResponses({
            @ApiResponse(responseCode = "200", content = @Content(mediaType = "application/json", examples = {
                    @ExampleObject(name = "카풀 조회 성공", value = """
                            {
                                "message": "낮은 가격순 카풀 조회",
                                "status": "OK",
                                "data": [
                                    {
                                        "carpoolId": 1,
                                        "driverName": "닉네임112",
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
                                    },
                                    {
                                        "carpoolId": 4,
                                        "driverName": "닉네임11",
                                        "driverImg": "https://carool-s3.s3.ap-northeast-2.amazonaws.com/profileImgS3.png",
                                        "carImg": "test.img",
                                        "carNumber": "12가 3456",
                                        "departureCoordinate": "경기도 수원시 권선구 오목천동",
                                        "latitude": null,
                                        "longitude": null,
                                        "departureTime": "2025-02-23T11:06:29.288",
                                        "chatLink": "kakao.com",
                                        "capacity": 4,
                                        "cost": 3000,
                                        "reservationCount": 0
                                    }
                                ]
                            }
                """)
            }))
    })
    public ResponseEntity<Message<List<CarpoolResponseDTO>>> lowCostCarpoolList();

    //TODO: OK
    @Operation(summary = "카풀 상세 조회", description = "자신이 예약하거나 생성한 카풀을 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", content = @Content(mediaType = "application/json", examples = {
                    @ExampleObject(name = "카풀 상세 조회 성공 - 운전자", value = """
                            {
                                "message": "내 카풀 조회",
                                "status": "OK",
                                "data": {
                                    "carpoolInfo": {
                                        "carpoolId": 1,
                                        "driverName": "닉네임112",
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
                                        "reservationCount": 1
                                    },
                                    "passengers": [
                                        {
                                            "passengerName": "닉네임11",
                                            "passengerImg": "https://carool-s3.s3.ap-northeast-2.amazonaws.com/profileImgS3.png"
                                        }
                                    ],
                                    "amIDriver": true
                                }
                            }
                """),
                    @ExampleObject(name = "카풀 상세 조회 성공 - 탑승자", value = """
                            {
                                "message": "내 카풀 조회",
                                "status": "OK",
                                "data": {
                                    "carpoolInfo": {
                                        "carpoolId": 1,
                                        "driverName": "닉네임112",
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
                                        "reservationCount": 1
                                    },
                                    "passengers": [
                                        {
                                            "passengerName": "닉네임11",
                                            "passengerImg": "https://carool-s3.s3.ap-northeast-2.amazonaws.com/profileImgS3.png"
                                        }
                                    ],
                                    "amIDriver": false
                                }
                            }
                """)
            })),
            @ApiResponse(responseCode = "404", content = @Content(mediaType = "application/json", examples = {
                    @ExampleObject(name = "예약 혹은 생성된 카풀이 없음 ", value = """
                            {
                                "status": 404,
                                "name": "CARPOOL_NOT_FOUND",
                                "code": "MATE-001",
                                "message": "카풀 정보를 찾을 수 없습니다."
                            }
                """)
            }))
    })
    public ResponseEntity<Message<CarpoolWithPassengersDTO>> myCarpool(@AuthenticationPrincipal CustomUserDetails userDetails);

    //TODO: OK
    @Operation(summary = "카풀 예약", description = "카풀 예약을 합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", content = @Content(mediaType = "application/json", examples = {
                    @ExampleObject(name = "카풀 예약 성공", value = """
                            {
                                "message": "카풀 예약 성공",
                                "status": "OK",
                                "data": 1
                            }
                """)
            })),
            @ApiResponse(responseCode = "400", content = @Content(mediaType = "application/json", examples = {
                    @ExampleObject(name = "예약 실패 - 중복", value = """
                            {
                                "status": 400,
                                "name": "CARPOOL_ALREADY_RESERVATIONS",
                                "code": "MATE-003",
                                "message": "예약된 카풀이 있습니다."
                            }
                """)
            }))
    })
    public ResponseEntity<Message<Long>> reservationCarpool(@AuthenticationPrincipal CustomUserDetails userDetails, @RequestBody @Valid ReservationCarpoolRequestDTO requestDTO);

    //TODO: OK
    @Operation(summary = "카풀 취소", description = "예약한 카풀을 취소합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", content = @Content(mediaType = "application/json", examples = {
                    @ExampleObject(name = "카풀 취소 성공", value = """
                            {
                                "message": "카풀 취소 성공",
                                "status": "OK",
                                "data": "성공"
                            }
                """)
            })),
            @ApiResponse(responseCode = "404", content = @Content(mediaType = "application/json", examples = {
                    @ExampleObject(name = "카풀 취소 실패 - 예약된 카풀이 없음", value = """
                            {
                                "status": 404,
                                "name": "CARPOOL_NOT_FOUND",
                                "code": "MATE-001",
                                "message": "카풀 정보를 찾을 수 없습니다."
                            }
                """)
            }))
    })
    public ResponseEntity<Message<String>> cancelCarpool(@AuthenticationPrincipal CustomUserDetails userDetails);

    //TODO: OK
    @Operation(summary = "예약 목록 조회", description = "지금까지 예약한 카풀 목록을 조회합니다..")
    @ApiResponses({
            @ApiResponse(responseCode = "200", content = @Content(mediaType = "application/json", examples = {
                    @ExampleObject(name = "예약 목록 조회 성공", value = """
                            {
                                "message": "탑승 목록 조회 성공",
                                "status": "OK",
                                "data": [
                                    {
                                        "carpoolId": 1
                                        "driverImg": "https://carool-s3.s3.ap-northeast-2.amazonaws.com/profileImgS3.png",
                                        "departureCoordinate": "경기도 수원시 권선구 오목천동",
                                        "departureTime": "2025-02-23T11:05:29.288",
                                        "capacity": 4,
                                        "cost": 2000,
                                        "reservationCount": 1,
                                        "createAt": "2025-02-24T03:02:29.93437"
                                    }
                                ]
                            }
                """),
                    @ExampleObject(name = "예약 목록 조회 성공 - 없을 경우", value = """
                            {
                                "message": "탑승 목록 조회 성공",
                                "status": "OK",
                                "data": []
                            }
                """)
            }))
    })
    public ResponseEntity<Message<List<CarpoolHistoryResponseDTO>>> getCarpoolHistory(@AuthenticationPrincipal CustomUserDetails userDetails);

    //TODO: OK
    @Operation(summary = "운행 목록 조회", description = "지금까지 운행한 목록 조회")
    @ApiResponses({
            @ApiResponse(responseCode = "200", content = @Content(mediaType = "application/json", examples = {
                    @ExampleObject(name = "운행 목록 조회 성공", value = """
                            {
                                            "message": "운행 목록 조회 성공",
                                            "status": "OK",
                                            "data": [
                                                {
                                                    "carpoolId": 1,
                                                    "driverImg": "https://carool-s3.s3.ap-northeast-2.amazonaws.com/profileImgS3.png",
                                                    "departureCoordinate": "경기도 수원시 권선구 오목천동",
                                                    "departureTime": "2025-02-23T11:05:29.288",
                                                    "capacity": 4,
                                                    "cost": 2000,
                                                    "reservationCount": 0,
                                                    "createAt": "2025-02-28T00:57:15.992722"
                                                }
                                            ]
                                        }
                """),@ExampleObject(name = "운행 목록 조회 성공 - 없을경우", value = """
                            {
                                    "message": "운행 목록 조회 성공",
                                    "status": "OK",
                                    "data": []
                            }
                """)
            })),
            @ApiResponse(responseCode = "401", content = @Content(mediaType = "application/json", examples = {
                    @ExampleObject(name = "조회 실패 - 드라이버가 아닌 경우", value = """
                            {
                                "status": 401,
                                "name": "DRIVER_NOT_FOUND",
                                "code": "ACCOUNT-007",
                                "message": "드라이버가 아닙니다."
                            }
                """)
            }))
    })
    public ResponseEntity<Message<List<CarpoolHistoryResponseDTO>>> getDriverHistory(@AuthenticationPrincipal CustomUserDetails userDetails);
}