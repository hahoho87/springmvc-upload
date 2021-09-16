package com.hahoho87.upload.file;

import com.hahoho87.upload.domain.UploadFile;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Component
public class FileStore {

    @Value("${file.dir}")
    private String fileDir;

    public String getFullPath(String filename) {
        return fileDir + filename;
    }

    public List<UploadFile> storedFiles(List<MultipartFile> multipartFiles) throws IOException {
        List<UploadFile> storedFileResult = new ArrayList<>();
        for (MultipartFile multipartFile : multipartFiles) {
            if (!multipartFile.isEmpty()) {
                storedFileResult.add(this.storeFile(multipartFile));
            }
        }
        return storedFileResult;
    }

    public UploadFile storeFile(MultipartFile multipartFile) throws IOException {
        if (multipartFile.isEmpty()) {
            return null;
        }

        String originalFilename = multipartFile.getOriginalFilename();
        String storedFileName = createStoredFileName(originalFilename);
        multipartFile.transferTo(new File(getFullPath(storedFileName)));
        return new UploadFile(originalFilename, storedFileName);
    }

    private String createStoredFileName(String originalFilename) {
        String extension = extractFileExtension(originalFilename);
        // 서버에 저장하는 파일명
        String uuid = UUID.randomUUID().toString();
        String storedFileName = uuid + "." + extension;

        return storedFileName;
    }

    private String extractFileExtension(String originalFilename) {
        int pos = originalFilename.lastIndexOf(".");
        String extension = originalFilename.substring(pos + 1);
        return extension;
    }

}
