package MATE.Carpool.domain.member.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class DriverRequestDto {

    private String memberId;
    private String carNumber;
    private String phoneNumber;
    private String carImage;

}
