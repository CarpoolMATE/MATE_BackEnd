package MATE.Carpool.domain.carpool.dto.response;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class BlockStartDTO {

    private LocalDateTime daySet;

    public BlockStartDTO(LocalDateTime daySet) {
        this.daySet = daySet;
    }
}
