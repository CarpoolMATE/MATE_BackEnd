package MATE.Carpool.domain.member.dto.response;

import MATE.Carpool.domain.member.entity.Driver;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class DriverResponseDto {
    private String phoneNumber;
    private String carNumber;
    private String carImage;

    public DriverResponseDto(Driver driver) {
        this.phoneNumber = driver.getPhoneNumber();
        this.carNumber = driver.getCarNumber();
        this.carImage = driver.getCarImage();
    }

}
