package MATE.Carpool.domain.S3.controller;

import MATE.Carpool.common.Message;
import MATE.Carpool.common.swagger.S3Api;
import MATE.Carpool.domain.S3.service.AwsS3Service;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


@RestController
@RequiredArgsConstructor
@RequestMapping("/file")
public class AmazonS3Controller implements S3Api {

    private final AwsS3Service awsS3Service;

    // 사용자 프로필 이미지 업로드
    @PostMapping("/upload")
    public ResponseEntity<Message<String>> uploadUserProfile(@RequestParam("file") MultipartFile file) {

        return awsS3Service.uploadProfileImage(file);

    }

}