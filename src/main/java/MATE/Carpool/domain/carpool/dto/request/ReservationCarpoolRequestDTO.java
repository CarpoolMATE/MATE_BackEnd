package MATE.Carpool.domain.carpool.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ReservationCarpoolRequestDTO {

    @Schema(description = "카풀 아이디 입니다.", example="1")
    @NotNull
    private Long carpoolId;

}
