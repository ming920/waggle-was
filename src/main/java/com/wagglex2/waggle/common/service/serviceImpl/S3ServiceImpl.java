package com.wagglex2.waggle.common.service.serviceImpl;

import com.wagglex2.waggle.common.error.ErrorCode;
import com.wagglex2.waggle.common.exception.BusinessException;
import com.wagglex2.waggle.common.service.S3Service;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class S3ServiceImpl implements S3Service {
    // private static final List<String> ALLOWED_EXTENSIONS = Arrays.asList("jpg", "jpeg", "png");
    // private static final long MAX_FILE_SIZE = 5 * 1024 * 1024; // 5MB 제한
    private final S3Client s3Client;

    @Value("${aws.s3.bucket}")
    private String bucketName;

    @Value("${aws.region}")
    private String region;

    @Override
    public String uploadImage(MultipartFile file, String folderPath) {
        if (file == null || file.isEmpty()) {
            throw new BusinessException(ErrorCode.FILE_NOT_UPLOADED);
        }
        // 경로 : waggle-image-bucket/user-profile-images/{username}/{uuid}.확장자
        String extension = getFileExtension(file.getOriginalFilename());
        String fileName = UUID.randomUUID().toString() + "." + extension;
        String s3Key = folderPath + "/" + fileName;

        try {
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(s3Key)
                    .contentType(file.getContentType())
                    .build();

            s3Client.putObject(
                    putObjectRequest,
                    RequestBody.fromInputStream(file.getInputStream(),
                            file.getSize())
            );

            String fileUrl = String.format(
                    "https://%s.s3.%s.amazonaws.com/%s",
                    bucketName,
                    region,
                    s3Key
            );
            log.info("S3 업로드 성공: {}", fileUrl);
            return fileUrl;

        } catch (IOException e) {
            log.error("S3 업로드 실패", e);
            throw new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public void deleteImage(String imageUrl) {
        if (imageUrl == null || imageUrl.isBlank()) {
            log.warn("삭제 요청된 이미지 URL이 비어 있습니다.");
            return;
        }

        try {
            // 삭제할 S3 경로 추출: "https://bucket.s3.region.amazonaws.com/" 이후의 경로임
            String keyPrefix = String.format("https://%s.s3.%s.amazonaws.com/", bucketName, region);
            String s3Key = imageUrl.replace(keyPrefix, "");

            // Delete 요청 생성
            s3Client.deleteObject(builder -> builder
                    .bucket(bucketName)
                    .key(s3Key)
                    .build());

            log.info("S3 이미지 삭제 성공: {}", s3Key);

        } catch (Exception e) {
            log.error("S3 이미지 삭제 실패: {}", imageUrl, e);
            throw new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

    private String getFileExtension(String filename) {
        return filename.substring(filename.lastIndexOf('.') + 1);
    }
}
