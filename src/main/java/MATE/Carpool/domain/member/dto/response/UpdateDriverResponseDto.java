package MATE.Carpool.domain.member.dto.response;

import MATE.Carpool.domain.member.entity.Member;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateDriverResponseDto {

    private String carNumber;
    private String phoneNumber;
    private String carImage;

    public UpdateDriverResponseDto(Member member){
        this.carNumber = member.getCarNumber();
        this.phoneNumber = member.getPhoneNumber();
        this.carImage = member.getCarImage();
    }
}
