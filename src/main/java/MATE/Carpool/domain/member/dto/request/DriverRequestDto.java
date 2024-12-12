package MATE.Carpool.domain.member.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class DriverRequestDto {

    private String memberId;
    private String carNumber;
    private String phoneNumber;
    private String carImage;

}
