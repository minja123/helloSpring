package com.contect.hello.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

@Component
public class FileStore {

    @Value("${file.upload.path}")
    private String fileDir;

    public String getFullPath(String fileName) {
        return fileDir + fileName;
    }

    public String storeFile(MultipartFile multipartFile) throws IOException {
        if (multipartFile == null || multipartFile.isEmpty()) {
            return null;
        }

        String originalFileName = multipartFile.getOriginalFilename();
        String storedFileName = createStoreFileName(originalFileName);

        File dir = new File(fileDir);
        if(!dir.exists()) dir.mkdirs();

        multipartFile.transferTo(new File(getFullPath(storedFileName)));
        return storedFileName;
    }

    public void deleteFile(String storeFileName) {
        File file = new File(getFullPath(storeFileName));
        if (file.exists()) {
            file.delete();
        }
    }

    // UUID를 이용해 서버에 저장할 고유한 파일명 생성
    private String createStoreFileName(String originalFileName) {
        String ext = extractExt(originalFileName);
        String uuid = UUID.randomUUID().toString();
        return uuid + "." + ext;
    }

    // 확장자 추출
    private String extractExt(String originalFileName) {
        return originalFileName.substring(originalFileName.lastIndexOf(".") + 1);
    }

}
