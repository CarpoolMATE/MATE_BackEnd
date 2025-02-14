package MATE.Carpool.domain.member.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class DriverRequestDto {

    @Schema(description = "차량번호 입니다.", example="12가 3456")
    @NotBlank
    private String carNumber;
    @Schema(description = "드라이버의 전화번호 입니다.", example="010-1234-5678")
    @NotBlank
    private String phoneNumber;
    @NotBlank
    @Schema(description = "등록될 차량의 사진입니다.", example="test.img")
    private String carImage;

}
