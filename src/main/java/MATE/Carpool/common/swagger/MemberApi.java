package MATE.Carpool.common.swagger;


import MATE.Carpool.common.Message;
import MATE.Carpool.config.userDetails.CustomUserDetails;
import MATE.Carpool.domain.member.dto.request.*;
import MATE.Carpool.domain.member.dto.response.MemberResponseDto;
import MATE.Carpool.domain.member.dto.response.UpdateDriverResponseDto;
import MATE.Carpool.domain.member.dto.response.UpdateMemberResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;


@Tag(name = "유저 API")
public interface MemberApi {

    @Operation(summary = "회원가입", description = "사용자의 정보를 입력하여 회원가입을 진행합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", content = @Content(mediaType = "application/json", examples = {
                    @ExampleObject(name = "가입성공", value = """
                                        {
                                                      "message": "회원가입 성공",
                                                      "status": "CREATED",
                                                      "data": true
                                                    }
                """)
            })),
            @ApiResponse(responseCode = "400", content = @Content(mediaType = "application/json", examples = {
                    @ExampleObject(name = "검증 실패 - 비밀번호 유효성 검사", value = """
            {
                "status": 400,
                "name": "BAD_REQUEST",
                "code": "입력값 오류",
                "message": "비밀번호는 3~20자 영문과 숫자를 포함해야 합니다."
            }
            """),
                    @ExampleObject(name = "검증 실패 - 이메일 유효성 검사", value = """
            {
                "status": 400,
                "name": "BAD_REQUEST",
                "code": "입력값 오류",
                "message": "올바른 형식의 이메일을 입력해주세요."
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
    ResponseEntity<Message<Boolean>> signUp(SignupRequestDto requestDto);


    @Operation(summary = "회원 정보 조회", description = "로그인한 사용자의 회원 정보를 조회합니다.(토큰조회)")
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
    }))
    ResponseEntity<Message<MemberResponseDto>> getMember(CustomUserDetails userDetails);



    @Operation(summary = "로그인", description = "가입하신 아이디와 비밀번호를 통해서 로그인을 요청합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", content = @Content(mediaType = "application/json", examples = {
                    @ExampleObject(name = "로그인 성공", value = """
                               {
                                     "message": "로그인",
                                     "requestMethod": "success",
                                     "data": {
                                         "memberId": "member",
                                         "nickname": "닉네임",
                                         "email": "member@test.com",
                                         "profileImage": "basic image",
                                         "memberType": "STANDARD",
                                         "providerType": "MATE",
                                         "createDate": "2025-02-14T16:53:27.047779",
                                         "updateDate": "2025-02-14T16:53:27.047779",
                                         "reservation": false,
                                         "isBanned": false,
                                         "isDriver": false,
                                         "carpoolCount": 0
                                     }
                                 }
                            """)
            })),
            @ApiResponse(responseCode = "400", content = @Content(mediaType = "application/json", examples = {
                    @ExampleObject(name = "로그인 실패 - 아이디 오류", value = """
                               {
                                      "status": 404,
                                      "name": "USER_NOT_FOUND",
                                      "code": "ACCOUNT-001",
                                      "message": "사용자를 찾을 수 없습니다."
                                  }
                            """),
                    @ExampleObject(name = "로그인 실패 - 비밀번호 오류", value = """
                               {
                                       "status": 400,
                                       "name": "BAE REQUEST",
                                       "code": "ACCOUNT-004",
                                       "message": "사용자 정보가 일치하지않습니다"
                                   }
                            """)
            }))
    })
    ResponseEntity<Message<Object>> signIn(SignInRequestDto requestDto, HttpServletResponse response, HttpServletRequest request);


    @Operation(summary = "로그아웃", description = "로그인한 사용자를 로그아웃 시킵니다.(쿠키삭제)")
    @ApiResponse(responseCode = "200", content = @Content(mediaType = "application/json", examples = {
            @ExampleObject(name = "로그아웃 성공", value = """
                   {
                           "message": "닉닉네임 회원 로그아웃 완료",
                           "status": "OK",
                           "data": ""
                         }
                """)
    }))
    ResponseEntity<Message<String>> signOut(CustomUserDetails userDetails, HttpServletResponse response, HttpServletRequest request);


    @Operation(summary = "회원정보 중복검사 - 이메일 중복검사", description = "입력한값으로 이미 존재하는 값인지 검사합니다")
    @ApiResponses({
            @ApiResponse(responseCode = "200", content = @Content(mediaType = "application/json", examples = {
                    @ExampleObject(name = "검사성공", value = """
                                  {
                                "message": "이메일 체크",
                                "status": "OK",
                                "data": true
                              }
                              """)})),
            @ApiResponse(responseCode = "400", content = @Content(mediaType = "application/json", examples = {
                    @ExampleObject(name = "검증 실패 - 이메일중복", value = """
                            {
                                 "status": 400,
                                 "name": "DUPLICATE_EMAIL",
                                 "code": "ACCOUNT-002",
                                 "message": "존재하는 이메일입니다."
                             }
            """)
            })),

    })
    ResponseEntity<Message<Boolean>> checkEmail(@Valid @RequestBody GetMemberInfo.DuplicateEmail email);
    
    
    @Operation(summary = "회원정보 중복검사 - 닉네임 중복검사", description = "입력한값으로 이미 존재하는 값인지 검사합니다")
    @ApiResponses({
            @ApiResponse(responseCode = "200", content = @Content(mediaType = "application/json", examples = {
                    @ExampleObject(name = "검사성공", value = """
                    {
                                  "message": "닉네임 체크",
                                  "status": "OK",
                                  "data": true
                                }
                    """)})),
            @ApiResponse(responseCode = "400", content = @Content(mediaType = "application/json", examples = {
                    @ExampleObject(name = "검증 실패 - 닉네임중복", value = """
                            {
                                 "status": 400,
                                 "name": "DUPLICATE_NICKNAME",
                                 "code": "ACCOUNT-004",
                                 "message": "존재하는 닉네임입니다."
                             }
            """)
            })),

    })
    ResponseEntity<Message<Boolean>> checkNickname(@Valid @RequestBody GetMemberInfo.DuplicateNickname nickname);
    
    @Operation(summary = "회원정보 중복검사 - 아이디 중복검사", description = "입력한값으로 이미 존재하는 값인지 검사합니다")
    @ApiResponses({
            @ApiResponse(responseCode = "200", content = @Content(mediaType = "application/json", examples = {
                    @ExampleObject(name = "검사성공", value = """
                    {
                                  "message": "아이디 체크",
                                  "status": "OK",
                                  "data": true
                                }
                    """)})),
            @ApiResponse(responseCode = "400", content = @Content(mediaType = "application/json", examples = {
                    @ExampleObject(name = "검증 실패 - 아이디중복", value = """
                            {
                                 "status": 400,
                                 "name": "DUPLICATE_MEMBER_ID",
                                 "code": "ACCOUNT-003",
                                 "message": "존재하는 아이디입니다."
                             }
            """)
            })),

    })
    ResponseEntity<Message<Boolean>> checkMemberId(@Valid @RequestBody GetMemberInfo.DuplicateMemberId memberId);





    @Operation(summary = "회원아이디 찾기 ", description = "가입하신정보로 회원의 비밀번호를 찾습니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", content = @Content(mediaType = "application/json", examples = {
                    @ExampleObject(name = "성공", value = """
                            {
                              "message": "멤버아이디 조회 성공",
                              "status": "OK",
                              "data": "memberId"
                            }
                            """)})),
            @ApiResponse(responseCode = "400", content = @Content(mediaType = "application/json", examples = {
                    @ExampleObject(name = "조회 실패 - 회원정보 불일치", value = """
                                {
                                 "status": 400,
                                 "name": "USER_NOT_FOUND",
                                 "code": "ACCOUNT-001",
                                 "message": "사용자를 찾을 수 없습니다."
                             }
                            """)

            })),
    })
    ResponseEntity<Message<String>> findMemberId( FindMemberIdRequestDto findMemberIdRequestDto);


    @Operation(summary = "드라이버 등록", description = "드라이버를 등록합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", content = @Content(mediaType = "application/json", examples = {
                    @ExampleObject(name = "등록 성공", value = """
                                {
                                      "memberId": "member",
                                      "nickname": "닉네임1",
                                      "email": "membe1r@test.com",
                                      "university": "서울대학교",
                                      "profileImage": "basic image",
                                      "memberType": "STANDARD",
                                      "providerType": "MATE",
                                      "createDate": "2025-02-14T17:41:25.947018",
                                      "updateDate": "2025-02-14T17:41:40.602877",
                                      "reservation": false,
                                      "isBanned": false,
                                      "carNumber": "12가 3456",
                                      "phoneNumber": "010-1234-5678",
                                      "carImage": "car.img",
                                      "driverRegistrationDate": "2025-02-14T17:45:35.1729515",
                                      "isDriver": true,
                                      "carpoolCount": 0
                                  }
                            """)})),
            @ApiResponse(responseCode = "400", content = @Content(mediaType = "application/json", examples = {
                    @ExampleObject(name = "등록 실패 - 파라미터값 오류", value = """
                                {
                                  "status": 400,
                                  "name": "BAD_REQUEST",
                                  "code": "입력값 오류",
                                  "message": "공백일 수 없습니다"
                              }
                            """),
                    @ExampleObject(name = "등록 실패 - 중복등록", value = """
                                {
                                   "status": 400,
                                   "name": "ALREADY_IS_DRIVER",
                                   "code": "ACCOUNT-011",
                                   "message": "이미 등록한 아이디입니다."
                               }
                            """)

            })),
    })
    ResponseEntity<Message<MemberResponseDto>> registerDriver(DriverRequestDto driverRequestDto, CustomUserDetails userDetails);


    @Operation(summary = "회원 정보 수정 (닉네임, 프로필이미지)", description = "회원정보를 수정합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", content = @Content(mediaType = "application/json", examples = {
                    @ExampleObject(name = "수정 성공", value = """
                            {
                              "message": "프로필 수정",
                              "status": "OK",
                              "data": {
                                "nickname": "고구마맛탕탕",
                                "profileImage": "test.img"
                                }
                            }
                            """)})),
            @ApiResponse(responseCode = "400", content = @Content(mediaType = "application/json", examples = {
                    @ExampleObject(name = "등록 실패 - 파라미터값 오류", value = """
                                {
                                  "status": 400,
                                  "name": "BAD_REQUEST",
                                  "code": "입력값 오류",
                                  "message": "공백일 수 없습니다"
                              }
                            """)

            })),
    })
    ResponseEntity<Message<UpdateMemberResponseDto>> updateUser(UpdateMemberDTO updateMemberDTO, CustomUserDetails userDetails);


    @Operation(summary = "드라이버 정보 수정", description = "드라이버 정보를 수정합니다.(차 번호, 핸드폰번호, 차 이미지)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", content = @Content(mediaType = "application/json", examples = {
                    @ExampleObject(name = "수정 성공", value = """
                            {
                              "message": "드라이버 수정",
                              "status": "OK",
                              "data": {
                                "carNumber": "12가 3456",
                                "phoneNumber": "010-1234-5678",
                                "carImage": "test.img"
                                    }
                            }
                            """)})),
            @ApiResponse(responseCode = "400", content = @Content(mediaType = "application/json", examples = {
                    @ExampleObject(name = "등록 실패 - 파라미터값 오류", value = """
                                {
                                  "status": 400,
                                  "name": "BAD_REQUEST",
                                  "code": "입력값 오류",
                                  "message": "공백일 수 없습니다"
                              }
                            """)
            })),
    })
    ResponseEntity<Message<UpdateDriverResponseDto>> updateDriver(DriverRequestDto driverRequestDto, CustomUserDetails userDetails);

    @Operation(summary = "비밀번호 찾기 - 이메일전송", description = "가입하신정보로 회원의 비밀번호를 찾습니다.(이메일전송)"

    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", content = @Content(mediaType = "application/json", examples = {
                    @ExampleObject(name = "전송 성공", value = """
                                {
                                   "message": "이메일이 발송되었습니다.",
                                   "status": "OK",
                                   "data": true
                                 }
                            """)})),


            @ApiResponse(responseCode = "400", content = @Content(mediaType = "application/json", examples = {
                    @ExampleObject(name = "전송 실패 - 회원정보 불일치", value = """
                                {
                                 "status": 400,
                                 "name": "USER_NOT_FOUND",
                                 "code": "ACCOUNT-001",
                                 "message": "사용자를 찾을 수 없습니다."
                             }
                            """)
                    ,@ExampleObject(name = "전송 실패 - 이메일 불일치", value = """
                                {
                                 "status": 400,
                                 "name": "NOT_EQUALS_MEMBER_INFO",
                                 "code": "ACCOUNT-005",
                                 "message": "잘못된 접근입니다."
                             }
                            """)
            })),

    })
    ResponseEntity<Message<Boolean>> findPassword(@RequestBody GetMemberInfo.ForgotPassword memberInfo) throws MessagingException;

    ;



    @Operation(summary = "비밀번호 변경 - 비밀번호 변경", description = "비밀번호 변경"

    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", content = @Content(mediaType = "application/json", examples = {
                    @ExampleObject(name = "비밀번호 변경성공", value = """
                                {
                                   "message": "비밀번호가 변경되었습니다.",
                                   "status": "OK",
                                   "data": true
                                 }
                            """)})),

    })
    ResponseEntity<Message<Boolean>> changePassword(CustomUserDetails userDetails,@RequestBody GetMemberInfo.Password password );



    @Operation(summary = "비밀번호 확인", description = "기존 비밀번호를 확인합니다."

    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", content = @Content(mediaType = "application/json", examples = {
                    @ExampleObject(name = "비밀번호 변경성공", value = """
                                {
                                    "message": "비밀번호 확인 완료",
                                    "status": "OK",
                                    "data": true
                                  }
                            """)})),

    })
    ResponseEntity<Message<Boolean>> checkPassword(CustomUserDetails userDetails, GetMemberInfo.Password password);

}
