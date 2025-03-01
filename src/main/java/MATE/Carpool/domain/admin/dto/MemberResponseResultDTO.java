package MATE.Carpool.domain.admin.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class MemberResponseResultDTO {
    List<MemberResponseDTO> members;
    Long totalCount;
    int totalPage;

}
