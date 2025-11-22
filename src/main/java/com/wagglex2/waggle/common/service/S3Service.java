package com.wagglex2.waggle.common.service;

import org.springframework.web.multipart.MultipartFile;

/**
 * AWS S3 파일 업로드/삭제 서비스 인터페이스.
 */
public interface S3Service {

    /**
     * 이미지 파일을 S3에 업로드하고 URL을 반환한다.
     *
     * @param file       업로드할 이미지 파일, 프론트에서 input type="file"로 전송.
     * @param folderPath S3 내 저장할 폴더 경로
     * @return 업로드된 이미지의 S3 URL
     */
    String uploadImage(MultipartFile file, String folderPath);

    /**
     * S3에서 이미지를 삭제한다.
     *
     * @param imageUrl 삭제할 이미지의 S3 URL
     */
    void deleteImage(String imageUrl);
}