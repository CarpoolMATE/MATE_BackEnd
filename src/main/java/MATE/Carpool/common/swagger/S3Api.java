package MATE.Carpool.common.swagger;

import MATE.Carpool.common.Message;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

@Tag(name = "AWS S3 API")
public interface S3Api {

    @Operation(
            summary = "AWS S3 이미지 업로드",
            description = "사용자가 프로필 수정을 위해 이미지를 선택할 때 사용되는 API입니다. S3에 이미지를 업로드한 후 URL을 반환합니다."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "업로드 성공",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    name = "업로드 성공",
                                    value = """
                    {
                        "message": "이미지 업로드 성공",
                        "status": "OK",
                        "data": "https://carool-s3.s3.ap-northeast-2.amazonaws.com/8b3ecb72-e911-40cd-9417-202352732d4550.jpeg"
                    }
                """
                            )
                    )
            )
    })
    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Message<String>> uploadUserProfile(@RequestPart(name = "file") MultipartFile file);
}


