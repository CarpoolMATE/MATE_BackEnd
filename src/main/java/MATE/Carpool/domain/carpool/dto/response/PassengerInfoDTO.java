package MATE.Carpool.domain.carpool.dto.response;

import MATE.Carpool.domain.member.entity.Member;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PassengerInfoDTO {

    private String passengerName;

    private String passengerImg;

    public PassengerInfoDTO(Member member){
        this.passengerName=member.getNickname();
        this.passengerImg=member.getProfileImage();
    }

}
