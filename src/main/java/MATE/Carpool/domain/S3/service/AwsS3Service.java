package MATE.Carpool.domain.S3.service;

import MATE.Carpool.domain.member.service.MemberService;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
@RequiredArgsConstructor
public class AwsS3Service {

    @Value("${cloud.aws.s3.bucket}")
    private String bucketName;

    private final AmazonS3 s3Client;
    private final MemberService memberService;

    // 사용자 프로필 이미지 업로드
    public String uploadProfileImage(MultipartFile file, Long memberId) {
        String key = "member" + memberId; // 프로필 이미지 키
        return uploadFile(file, key);
    }

    // 드라이버 차량 이미지 업로드
    public String uploadDriverCarImage(MultipartFile file, Long memberId) {
        String key = "member" + memberId + "-driver"; // 차량 이미지 키
        return uploadFile(file, key);
    }

    // 파일 업로드 처리 (중복 이름 처리 포함)
    private String uploadFile(MultipartFile file, String key) {
        // 기존 파일이 있으면 삭제
        if (s3Client.doesObjectExist(bucketName, key)) {
            s3Client.deleteObject(bucketName, key);
        }

        // 파일 업로드
        try {
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentLength(file.getSize());
            metadata.setContentType(file.getContentType());
            s3Client.putObject(bucketName, key, file.getInputStream(), metadata);
        } catch (IOException e) {
            throw new RuntimeException("Error while uploading file to S3", e);
        }

        // S3 URL 반환
        return s3Client.getUrl(bucketName, key).toString();

        //업로드 완료 후 멤버 테이블에 주소 넣기

    }

    // 사용자 프로필 이미지 가져오기
    public String getProfileImage(Long memberId) {
        String key = "member" + memberId;
        if (s3Client.doesObjectExist(bucketName, key)) {
            return s3Client.getUrl(bucketName, key).toString();
        } else {
            throw new RuntimeException("Profile image not found for member: " + memberId);
        }
    }

    // 드라이버 차량 이미지 가져오기
    public String getDriverCarImage(Long memberId) {
        String key = "member" + memberId + "-driver";
        if (s3Client.doesObjectExist(bucketName, key)) {
            return s3Client.getUrl(bucketName, key).toString();
        } else {
            throw new RuntimeException("Driver car image not found for member: " + memberId);
        }
    }
}