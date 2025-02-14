package MATE.Carpool.domain.member.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class UpdateMemberDTO {

    @Schema(description = "닉네임 입니다.", example="고구마맛탕")
    private String nickname;
    @Schema(description = "대학교 이름 입니다.", example="서울대학교")
    private String university;
    @Schema(description = "프로필 이미지 입니다.", example="test.img")
    private String profileImage;

}