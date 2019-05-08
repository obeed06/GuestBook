package com.tm.interview.guestbook;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
class FileStoreService {

    private static String uploadDirectory = System.getProperty("user.dir")+"/uploads";

    void deleteFile(String filePath) throws IOException {
        if(filePath == null) return;

        Path fileToDeletePath = Paths.get(filePath);
        if(fileToDeletePath != null) {
            Files.delete(fileToDeletePath);
        }
    }

    String saveFile(MultipartFile image) throws IOException {
        if(image == null || image.isEmpty()) return null;

        Path fileNameAndPath = Paths.get(uploadDirectory, image.getOriginalFilename());
        Files.write(fileNameAndPath, image.getBytes());

        return fileNameAndPath.toAbsolutePath().toString();
    }
}
