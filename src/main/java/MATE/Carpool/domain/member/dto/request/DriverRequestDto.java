package MATE.Carpool.domain.member.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class DriverRequestDto {

    @Schema(description = "차량번호 입니다.", example="12가 3456")
    private String carNumber;
    @Schema(description = "드라이버의 전화번호 입니다.", example="010-1234-5678")
    private String phoneNumber;
    @Schema(description = "등록될 차량의 사진입니다.", example="test.img")
    private MultipartFile carImage;

}
