package MATE.Carpool.domain.admin.dto;

import MATE.Carpool.domain.carpool.dto.response.CarpoolResponseDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class CarpoolResponseResultDTO {
    List<CarpoolResponseDTO> carpools;
    Long totalCount;
    int totalPage;
}
