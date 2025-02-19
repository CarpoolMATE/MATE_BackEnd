package MATE.Carpool.domain.carpool.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ReservationCarpoolRequestDTO {

    @NotBlank
    private Long carpoolId;

}
