package vn.hoidanit.jobhunter.controller;

import java.io.IOException;
import java.net.URISyntaxException;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import vn.hoidanit.jobhunter.domain.response.file.ResUploadFile;
import vn.hoidanit.jobhunter.service.FileService;
import vn.hoidanit.jobhunter.util.annotion.ApiMessage;
import vn.hoidanit.jobhunter.util.error.StorageException;

@RestController
@RequestMapping("/api/v1")
public class FileController {
    @Value("${hoidanit.upload-file.base-uri}")
    private String baseURI;
    private final FileService fileService;

    public FileController(FileService fileService) {
        this.fileService = fileService;
    }

    @PostMapping("/files")
    @ApiMessage("Upload singe file")
    public ResponseEntity<ResUploadFile> uploadFile(
            @RequestParam(name = "file", required = false) MultipartFile file,
            @RequestParam("folder") String folder) throws URISyntaxException, IOException, StorageException {

        // check valid trước cái
        if (file == null || file.isEmpty()) {
            throw new StorageException("file is empty ,Please upload a new file");
            // Validate extension
        }
        String fileName = file.getOriginalFilename();
        List<String> allowedExtensions = Arrays.asList("pdf", "jpg", "jpeg", "png", "doc", "docx");
        boolean isValidExtension = allowedExtensions.stream()
                .anyMatch(ext -> fileName.toLowerCase().endsWith("." + ext));
        if (!isValidExtension) {
            throw new StorageException("Invalid file type based on extension.");
        }
        // create file upload
        this.fileService.createDirectory(baseURI + folder);
        // store file upload
        String uploadFile = this.fileService.store(file, folder);
        ResUploadFile res = new ResUploadFile(uploadFile, Instant.now());
        return ResponseEntity.ok().body(res);
    }
}
