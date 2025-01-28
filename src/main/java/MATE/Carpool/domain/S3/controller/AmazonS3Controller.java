package MATE.Carpool.domain.S3.controller;

import MATE.Carpool.config.userDetails.CustomUserDetails;
import MATE.Carpool.domain.S3.service.AwsS3Service;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/file")
public class AmazonS3Controller {

    private final AwsS3Service awsS3Service;

    // 사용자 프로필 이미지 업로드
    @PostMapping("/upload")
    public ResponseEntity<Map<String, String>> uploadUserProfile(@RequestParam("file") MultipartFile file) {

        String response = awsS3Service.uploadProfileImage(file);

        Map<String, String> imgURL = new HashMap<>();

        imgURL.put("img", response);

        return ResponseEntity.ok(imgURL);
    }

    @DeleteMapping("/imgDelete")
    public ResponseEntity<String> registerUniversity(@RequestBody Map<String, String> request) {
        String imgKey = request.get("imgKey");
        return awsS3Service.deleteImg(imgKey);
    }

}