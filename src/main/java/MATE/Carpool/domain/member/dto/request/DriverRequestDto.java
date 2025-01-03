package MATE.Carpool.domain.member.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class DriverRequestDto {

    @Schema(description = "사용자의 고유 아이디(로그인후 응답받은 id 값을 사용합니다)", example="6ON0wxzeqSGYSwWCGtCoPg")
    private String memberId;
    @Schema(description = "차량번호 입니다.", example="12가 3456")
    private String carNumber;
    @Schema(description = "드라이버의 전화번호 입니다.", example="010-1234-5678")
    private String phoneNumber;
    @Schema(description = "등록될 차량의 사진입니다.", example="test.img")
    private String carImage;

}
