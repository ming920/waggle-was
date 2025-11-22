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
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class S3ServiceImpl implements S3Service {
    private static final List<String> ALLOWED_EXTENSIONS = Arrays.asList("jpg", "jpeg", "png");
    private static final List<String> ALLOWED_CONTENT_TYPES = Arrays.asList(
            "image/jpeg",
            "image/jpg",
            "image/png"
    );
    private static final byte[] JPEG_SIGNATURE = {(byte) 0xFF, (byte) 0xD8, (byte) 0xFF};
    private static final byte[] PNG_SIGNATURE = {(byte) 0x89, 0x50, 0x4E, 0x47};
    private static final long MAX_FILE_SIZE = 5 * 1024 * 1024; // 5MB 제한
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

        // 파일 유효성 검증
        validateFile(file);

        // folderPath 검증 및 정규화
        String normalizedFolderPath = validateAndNormalizeFolderPath(folderPath);

        // 경로 : waggle-image-bucket/user-profile-images/{username}/{uuid}.확장자
        String extension = getFileExtension(file.getOriginalFilename());
        String fileName = UUID.randomUUID().toString() + "." + extension;
        String s3Key = normalizedFolderPath + "/" + fileName;

        try {
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(s3Key)
                    .contentType(file.getContentType())
                    .build();

            s3Client.putObject(
                    putObjectRequest,
                    RequestBody.fromInputStream(
                            file.getInputStream(),
                            file.getSize()
                    )
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

    private void validateFile(MultipartFile file) {
        // 파일 크기 검증
        if (file.getSize() > MAX_FILE_SIZE) {
            throw new BusinessException(ErrorCode.FILE_SIZE_TOO_LARGE);
        }

        // 파일명 검증
        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null) {
            throw new BusinessException(ErrorCode.INVALID_FILE_NAME);
        }

        // 확장자 검증
        String extension = getFileExtension(originalFilename).toLowerCase();
        if (!ALLOWED_EXTENSIONS.contains(extension)) {
            throw new BusinessException(ErrorCode.INVALID_FILE_FORMAT);
        }

        // Content-Type 검증
        String contentType = file.getContentType();
        if (contentType == null || !ALLOWED_CONTENT_TYPES.contains(contentType.toLowerCase())) {
            throw new BusinessException(ErrorCode.INVALID_FILE_FORMAT);
        }

        // Magic Byte 검증
        validateMagicByte(file, extension);
    }

    private void validateMagicByte(MultipartFile file, String extension) {
        try {
            byte[] header = new byte[4];
            int bytesRead = file.getInputStream().read(header);
            
            if (bytesRead < 3) {
                throw new BusinessException(ErrorCode.INVALID_FILE_FORMAT);
            }
            
            boolean isValid = false;
            if (extension.equals("jpg") || extension.equals("jpeg")) {
                isValid = header[0] == JPEG_SIGNATURE[0] && 
                         header[1] == JPEG_SIGNATURE[1] && 
                         header[2] == JPEG_SIGNATURE[2];
            } else if (extension.equals("png")) {
                if (bytesRead < 4) {
                    throw new BusinessException(ErrorCode.INVALID_FILE_FORMAT);
                }
                isValid = header[0] == PNG_SIGNATURE[0] && 
                         header[1] == PNG_SIGNATURE[1] && 
                         header[2] == PNG_SIGNATURE[2] && 
                         header[3] == PNG_SIGNATURE[3];
            }
            
            if (!isValid) {
                throw new BusinessException(ErrorCode.INVALID_FILE_FORMAT);
            }
        } catch (IOException e) {
            throw new BusinessException(ErrorCode.INVALID_FILE_FORMAT);
        }
    }

    private String validateAndNormalizeFolderPath(String folderPath) {
        if (folderPath == null || folderPath.isBlank()) {
            throw new BusinessException(ErrorCode.INVALID_REQUEST);
        }
    
        // 경로 탐색 공격 방지
        if (folderPath.contains("..")) {
            throw new BusinessException(ErrorCode.INVALID_REQUEST);
        }
    
        // 연속된 슬래시를 하나로 통합
        String normalized = folderPath.replaceAll("/+", "/");
        
        // 앞뒤 슬래시 제거
        if (normalized.startsWith("/")) {
            normalized = normalized.substring(1);
        }
        if (normalized.endsWith("/")) {
            normalized = normalized.substring(0, normalized.length() - 1);
        }
    
        return normalized;
    }
}
