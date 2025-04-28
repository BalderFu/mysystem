package com.myhousekeepingservice.controller;

import com.myhousekeepingservice.model.base.Result;
import org.apache.commons.io.FilenameUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

@RestController
@RequestMapping("/fileUpload")
public class FileUploadController {
    private static final String BASE_PACKAGE = System.getProperty("user.dir") + "/uploads/";

    @PostMapping
    public Result<String> uploadFile(@RequestParam("file") MultipartFile file) throws IOException {
        if (!Files.exists(Paths.get(BASE_PACKAGE))) {
            Files.createDirectories(Paths.get(BASE_PACKAGE));
        }
        String extension = FilenameUtils.getExtension(file.getOriginalFilename());
        String fileName = System.currentTimeMillis() + "." + extension;
        Files.copy(file.getInputStream(), Paths.get(BASE_PACKAGE + fileName));
        return Result.success(fileName);
    }
}
