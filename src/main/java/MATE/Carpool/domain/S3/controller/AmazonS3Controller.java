package MATE.Carpool.domain.S3.controller;

import MATE.Carpool.domain.S3.service.AwsS3Service;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/file")
public class AmazonS3Controller {

    private final AwsS3Service awsS3Service;

    // 사용자 프로필 이미지 업로드
    @PostMapping("/{memberId}/profile/upload")
    public ResponseEntity<String> uploadUserProfile(@PathVariable Long memberId, @RequestParam("file") MultipartFile file) {
        String response = awsS3Service.uploadProfileImage(file, memberId);
        return ResponseEntity.ok(response);
    }

    // 드라이버 차량 사진 업로드
    @PostMapping("/{memberId}/driver/upload")
    public ResponseEntity<String> uploadDriverCarImage(@PathVariable Long memberId, @RequestParam("file") MultipartFile file) {
        String response = awsS3Service.uploadDriverCarImage(file, memberId);
        return ResponseEntity.ok(response);
    }

    // 사용자 프로필 이미지 가져오기
    @GetMapping("/{memberId}/profile")
    public ResponseEntity<String> getUserProfile(@PathVariable Long memberId) {
        String fileUrl = awsS3Service.getProfileImage(memberId);
        return ResponseEntity.ok(fileUrl);
    }

    // 드라이버 차량 이미지 가져오기
    @GetMapping("/{memberId}/driver")
    public ResponseEntity<String> getDriverCarImage(@PathVariable Long memberId) {
        String fileUrl = awsS3Service.getDriverCarImage(memberId);
        return ResponseEntity.ok(fileUrl);
    }
}