package MATE.Carpool.common.swagger;

import MATE.Carpool.common.Message;
import MATE.Carpool.config.userDetails.CustomUserDetails;
import MATE.Carpool.domain.admin.dto.CarpoolResponseResultDTO;
import MATE.Carpool.domain.admin.dto.MemberResponseDTO;
import MATE.Carpool.domain.admin.dto.PageResponseResultDTO;
import MATE.Carpool.domain.carpool.dto.response.AdminCarpoolInfoDTO;
import MATE.Carpool.domain.member.dto.response.MemberResponseDto;
import MATE.Carpool.domain.report.dto.ReportResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.util.List;

public interface AdminApi {


    @Operation(summary = "회원 정보 조회", description = "회원 ID를 통해서 해당회원의 정보를 조회합니다")
    @ApiResponses({
        @ApiResponse(responseCode = "200", content = @Content(mediaType = "application/json", examples = {
                @ExampleObject(name = "회원정보 조회 성공", value = """
                                   {
                                "message": "조회성공",
                                "status": "OK",
                                "data": {
                                  "memberId": "member",
                                  "nickname": "닉네임",
                                  "email": "member@test.com",
                                  "profileImage": "basic image",
                                  "memberType": "STANDARD",
                                  "providerType": "MATE",
                                  "createDate": "2025-02-14T16:53:27.047779",
                                  "updateDate": "2025-02-14T17:39:09.3289",
                                  "reservation": false,
                                  "isBanned": false,
                                  "driverRegistrationDate": "2025-02-14T17:39:09.2809",
                                  "isDriver": true,
                                  "carpoolCount": 0
                                }
                              }
                              """)

        })),
    @ApiResponse(responseCode = "400", content = @Content(mediaType = "application/json", examples = {
            @ExampleObject(name = "조회 실패 - 아이디 오류", value = """
                               {
                                      "status": 404,
                                      "name": "USER_NOT_FOUND",
                                      "code": "ACCOUNT-001",
                                      "message": "사용자를 찾을 수 없습니다."
                                  }
                            """)

    }))
    })
    ResponseEntity<Message<MemberResponseDto>> readOneMember(@Parameter(description = "회원 ID") Long memberId, CustomUserDetails userDetails);



    @Operation(summary = "회원 전체 조회 - 모든 회원", description = "회원 전체 목록을 조회합니다.")
    @ApiResponse(responseCode = "200", content = @Content(mediaType = "application/json", examples = {
            @ExampleObject(name = "회원정보 조회 성공", value = """
                              {
                        "message": "전체 회원 조회 성공",
                        "status": "OK",
                        "data": {
                            "members": [
                                {
                                    "id": 152,
                                    "memberId": "admin",
                                    "isBanned": false,
                                    "nickname": "admin",
                                    "carpoolCount": 0,
                                    "carNumber": null,
                                    "phoneNumber": null
                                },
                                {
                                    "id": 102,
                                    "memberId": "3922307232",
                                    "isBanned": false,
                                    "nickname": "Hj2",
                                    "carpoolCount": 0,
                                    "carNumber": null,
                                    "phoneNumber": null
                                }
                            ],
                            "totalCount": 4,
                            "totalPage": 2
                        }
                    }
                    """)
    }))
    ResponseEntity<Message<PageResponseResultDTO<MemberResponseDTO>>> readAllMember(@RequestParam("size") int size, @RequestParam("page") int page);

    @Operation(summary = "회원 전체 조회 - 드라이버", description = "드라이버 전체 목록을 조회합니다.")
    @ApiResponse(responseCode = "200", content = @Content(mediaType = "application/json", examples = {
            @ExampleObject(name = "회원정보 조회 성공", value = """
                              {
                        "message": "전체 회원 조회 성공",
                        "status": "OK",
                        "data": {
                            "members": [
                                {
                                    "id": 152,
                                    "memberId": "admin",
                                    "isBanned": false,
                                    "nickname": "admin",
                                    "carpoolCount": 0,
                                    "carNumber": null,
                                    "phoneNumber": null
                                },
                                {
                                    "id": 102,
                                    "memberId": "3922307232",
                                    "isBanned": false,
                                    "nickname": "Hj2",
                                    "carpoolCount": 0,
                                    "carNumber": null,
                                    "phoneNumber": null
                                }
                            ],
                            "totalCount": 4,
                            "totalPage": 2
                        }
                    }
                    """)
    }))
    ResponseEntity<Message<PageResponseResultDTO<MemberResponseDTO>>> readAllDriver(@RequestParam("size") int size, @RequestParam("page") int page);




    @Operation(summary = "회원 정지 및 정지해제 ", description = "회원을 정지처리 및 정지 해제 처리합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", content = @Content(mediaType = "application/json", examples = {
                    @ExampleObject(name = "정지 해제 처리 성공", value = """
                              {
                                    "message": "회원 정지 처리 완료",
                                    "status": "OK",
                                    "data": false
                                }
                              """)

    })),
            @ApiResponse(responseCode = "404", content = @Content(mediaType = "application/json", examples = {
                    @ExampleObject(name = "조회 실패 - 아이디 오류", value = """
                               {
                                      "status": 404,
                                      "name": "USER_NOT_FOUND",
                                      "code": "ACCOUNT-001",
                                      "message": "사용자를 찾을 수 없습니다."
                                  }
                            """)
            })),
            @ApiResponse(responseCode = "400", content = @Content(mediaType = "application/json", examples = {
                    @ExampleObject(name = "정지 실패 - 자기자신 ", value = """
                       {
                              "status": 400,
                              "name": "CAN_NOT_BAN_ONESELF",
                              "code": "ACCOUNT-010",
                              "message": "자기 자신을 정지시킬 수 없습니다."
                          }
                    """)

            }))
    })
    ResponseEntity<Message<Boolean>> isBanned(@AuthenticationPrincipal CustomUserDetails userDetails, @PathVariable("memberId") Long memberId);


    @Operation(
            summary = "카풀 목록조회 페이징",
            description = "카풀 전체 목록을 조회합니다 (페이징 및 날짜 필터링 지원). 예: carpools?page=2&size=5&startDate=2025-02-01&endDate=2025-02-05"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", content = @Content(mediaType = "application/json", examples = {
                    @ExampleObject(name = "카풀 목록 조회 성공", value = """
                                 {
                                 "message": "카풀 목록 조회 성공",
                                 "status": "OK",
                                 "data": {
                                     "carpools": [
                                         {
                                             "carpoolId": 15,
                                             "driverName": "tester29",
                                             "driverImg": "https://carool-s3.s3.ap-northeast-2.amazonaws.com/profileImgS3.png",
                                             "carImg": "https://carool-s3.s3.ap-northeast-2.amazonaws.com/profileImgS3.png",
                                             "carNumber": "29가 1234",
                                             "departureCoordinate": "37.075150, 126.841093",
                                             "latitude": null,
                                             "longitude": null,
                                             "departureTime": "2025-03-02T08:20:00",
                                             "chatLink": "kakao.com/539",
                                             "capacity": 1,
                                             "cost": 4000,
                                             "reservationCount": 0
                                         },
                                         {
                                             "carpoolId": 14,
                                             "driverName": "tester27",
                                             "driverImg": "https://carool-s3.s3.ap-northeast-2.amazonaws.com/profileImgS3.png",
                                             "carImg": "https://carool-s3.s3.ap-northeast-2.amazonaws.com/profileImgS3.png",
                                             "carNumber": "27가 1234",
                                             "departureCoordinate": "37.012702, 126.697995",
                                             "latitude": null,
                                             "longitude": null,
                                             "departureTime": "2025-03-02T07:00:00",
                                             "chatLink": "kakao.com/471",
                                             "capacity": 2,
                                             "cost": 1500,
                                             "reservationCount": 0
                                         },
                                         {
                                             "carpoolId": 13,
                                             "driverName": "tester25",
                                             "driverImg": "https://carool-s3.s3.ap-northeast-2.amazonaws.com/profileImgS3.png",
                                             "carImg": "https://carool-s3.s3.ap-northeast-2.amazonaws.com/profileImgS3.png",
                                             "carNumber": "25가 1234",
                                             "departureCoordinate": "37.040305, 126.995209",
                                             "latitude": null,
                                             "longitude": null,
                                             "departureTime": "2025-03-02T07:50:00",
                                             "chatLink": "kakao.com/18",
                                             "capacity": 4,
                                             "cost": 3000,
                                             "reservationCount": 0
                                         }
                                     ],
                                     "totalCount": 15,
                                     "totalPage": 5
                                 }
                             }
                         """)

            }))
    })
    ResponseEntity<Message<CarpoolResponseResultDTO>> readAllCarpool(
            @RequestParam("size") int size,
            @RequestParam("page") int page,
            @RequestParam(value = "startDate", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @RequestParam(value = "endDate", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate
    );




    @Operation(
            summary = "카풀 조회 - 단일",
            description = "카풀을 조회합니다"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", content = @Content(mediaType = "application/json", examples = {
                    @ExampleObject(name = "카풀 조회 성공", value = """
                                 {
                                         "message": "카풀 단일 조회 성공",
                                         "status": "OK",
                                         "data": {
                                             "carpoolInfo": {
                                                 "carpoolId": 1,
                                                 "driverName": "tester1",
                                                 "driverImg": "https://carool-s3.s3.ap-northeast-2.amazonaws.com/profileImgS3.png",
                                                 "carImg": "https://carool-s3.s3.ap-northeast-2.amazonaws.com/profileImgS3.png",
                                                 "carNumber": "1가 1234",
                                                 "departureCoordinate": "37.101756, 126.091950",
                                                 "latitude": null,
                                                 "longitude": null,
                                                 "departureTime": "2025-03-02T07:50:00",
                                                 "chatLink": "kakao.com/962",
                                                 "capacity": 4,
                                                 "cost": 4000,
                                                 "reservationCount": 4
                                             },
                                             "passengerInfo": [
                                                 {
                                                     "passengerName": "tester12",
                                                     "passengerImg": "https://carool-s3.s3.ap-northeast-2.amazonaws.com/profileImgS3.png"
                                                 },
                                                 {
                                                     "passengerName": "tester15",
                                                     "passengerImg": "https://carool-s3.s3.ap-northeast-2.amazonaws.com/profileImgS3.png"
                                                 },
                                                 {
                                                     "passengerName": "tester20",
                                                     "passengerImg": "https://carool-s3.s3.ap-northeast-2.amazonaws.com/profileImgS3.png"
                                                 },
                                                 {
                                                     "passengerName": "tester35",
                                                     "passengerImg": "https://carool-s3.s3.ap-northeast-2.amazonaws.com/profileImgS3.png"
                                                 }
                                             ],
                                             "report": false
                                         }
                                     }
                         """)

            }))
            ,@ApiResponse(responseCode = "400", content = @Content(mediaType = "application/json", examples = {
                    @ExampleObject(name = "조회실패 - 정보없음", value = """
                                {
                                         "status": 404,
                                         "name": "CARPOOL_NOT_FOUND",
                                         "code": "MATE-001",
                                         "message": "카풀 정보를 찾을 수 없습니다."
                                     }
                         """)

            }))
    })
    ResponseEntity<Message<AdminCarpoolInfoDTO>> readOneCarpool(@PathVariable("id")Long id);


    @Operation(
            summary = "카풀 신고 목록 조회",
            description = "카풀에 신고된 목록을 전체조회합니다"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", content = @Content(mediaType = "application/json", examples = {
                    @ExampleObject(name = "신고 조회 성공", value = """
                                 {
                                         "message": "신고목록 조회 완료",
                                         "status": "OK",
                                         "data": []
                                     }
                         """)

            }))

    })
    ResponseEntity<Message<PageResponseResultDTO<ReportResponseDto>>> readAllByCarpool(@PathVariable("id") Long carpoolId, int size, int page);


    @Operation(
            summary = "전체 신고 목록 조회 ",
            description = "전체 신고된 목록을 조회합니다"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", content = @Content(mediaType = "application/json", examples = {
                    @ExampleObject(name = "신고 조회 성공", value = """
                                 {
                                         "message": "신고목록 조회 완료",
                                         "status": "OK",
                                         "data": [
                                                    {
                                                      "reportId": 1,
                                                      "memberId": 123,
                                                      "memberNickname": "testuser1",
                                                      "createAt": "2025-03-23T10:15:30",
                                                      "carpoolId": 456,
                                                      "reportTitle": "카풀 불편 사항",
                                                      "reportContent": "차량이 너무 더러워요.",
                                                      "isProcessed": false
                                                    },
                                                    {
                                                      "reportId": 2,
                                                      "memberId": 124,
                                                      "memberNickname": "testuser2",
                                                      "createAt": "2025-03-22T08:45:10",
                                                      "carpoolId": 457,
                                                      "reportTitle": "카풀 시간 변경 요청",
                                                      "reportContent": "출발 시간이 너무 늦습니다.",
                                                      "isProcessed": true
                                                    }
                                              ]
                                     }
                         """)

            }))

    })
    ResponseEntity<Message<PageResponseResultDTO<ReportResponseDto>>> readAllReports(int size, int page);

    
    @Operation(
            summary = "신고 목록 조회 - 단일",
            description = "신고된 목록을 조회합니다"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", content = @Content(mediaType = "application/json", examples = {
                    @ExampleObject(name = "신고 조회 성공", value = """
                                 {
                                         "message": "신고목록 조회 완료",
                                         "status": "OK",
                                         "data":   {
                                                  "reportId": 2,
                                                  "memberId": 124,
                                                  "memberNickname": "testuser2",
                                                  "createAt": "2025-03-22T08:45:10",
                                                  "carpoolId": 457,
                                                  "reportTitle": "카풀 시간 변경 요청",
                                                  "reportContent": "출발 시간이 너무 늦습니다.",
                                                  "isProcessed": true
                                                }
                                     }
                         """)

            })),
            @ApiResponse(responseCode = "404", content = @Content(mediaType = "application/json", examples = {
                    @ExampleObject(name = "조회 실패 - 목록없음", value = """
                                  {
                                         "status": 404,
                                         "name": "REPORT_NOT_FOUND",
                                         "code": "REPORT-001",
                                         "message": "신고 정보를 찾을 수 없습니다."
                                 }
                         """)

            }))

    })
    ResponseEntity<Message<ReportResponseDto>> readReport(@PathVariable("id") Long reportId);


    @Operation(
            summary = "신고 처리 여부",
            description = "신고목록의 처리여부를 변경 합니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", content = @Content(mediaType = "application/json", examples = {
                    @ExampleObject(name = "신고 처리 성공", value = """
                               {
                                                  "message": "신고 처리 완료",
                                                  "status": "OK",
                                                  "data": true
                                                }
                         
                         """)

            }))
    })
    ResponseEntity<Message<Boolean>> processReport(@PathVariable("id") Long reportId);



}
