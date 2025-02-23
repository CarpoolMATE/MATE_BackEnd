package MATE.Carpool.domain.S3.service;

import MATE.Carpool.common.Message;
import MATE.Carpool.common.exception.CustomException;
import MATE.Carpool.common.exception.ErrorCode;
import MATE.Carpool.domain.member.repository.MemberRepository;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AwsS3Service {

    private final MemberRepository memberRepository;

    @Value("${cloud.aws.s3.bucket}")
    private String bucketName;

    @Value("${default.profile.image.url}")
    private String defaultProfileImageKey;

    // 기본 드라이버 차량 이미지 URL 반환
    public String getDefaultProfileImageUrl() {
        return defaultProfileImageKey;
    }

    private final AmazonS3 s3Client;

    // 사용자 프로필 이미지 업로드
    public ResponseEntity<Message<String>> uploadProfileImage(MultipartFile file) {

        //String key = userDetails.getMember().getMemberId();
        String key = UUID.randomUUID().toString()+(file.getOriginalFilename()).substring(1);

        // 프로필 이미지 키
        return ResponseEntity.ok(new Message<>("이미지 업로드 성공", HttpStatus.OK, uploadFile(file, key)));
    }

    // S3 파일 삭제 메서드
    public ResponseEntity<String> deleteImg(String imgKey) {
        try {
            // 파일명 추출 및 디코딩 적용
            String fileName = extractFileName(imgKey);

            // 기본 프로필 이미지인지 확인
            if (fileName.equals(defaultProfileImageKey)) {
                return ResponseEntity.ok("기본 이미지는 삭제할 수 없습니다.");
            }

            // 존재 여부 확인 없이 삭제 시도
            s3Client.deleteObject(bucketName, fileName);
            return ResponseEntity.ok("이미지가 삭제되었습니다: " + fileName);
        } catch (Exception e) {
            throw new RuntimeException("이미지 삭제 중 오류가 발생했습니다.", e);
        }
    }



    private String uploadFile(MultipartFile file, String key) {

        // 파일 업로드
        try {
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentLength(file.getSize());
            metadata.setContentType(file.getContentType());
            s3Client.putObject(bucketName, key, file.getInputStream(), metadata);
        } catch (IOException e) {
            throw new RuntimeException("업로드 중 오류가 발생했습니다.: ", e);
        }

        // S3 URL 반환
        return s3Client.getUrl(bucketName, key).toString();
    }

    private String extractFileName(String imgUrl) {
        String fileName = imgUrl.substring(imgUrl.lastIndexOf("/") + 1);
        return URLDecoder.decode(fileName, StandardCharsets.UTF_8); // URL 디코딩 적용
    }

}