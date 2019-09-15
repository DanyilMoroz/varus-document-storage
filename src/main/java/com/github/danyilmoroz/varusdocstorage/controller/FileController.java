package com.github.danyilmoroz.varusdocstorage.controller;

import com.github.danyilmoroz.varusdocstorage.model.Document;
import com.github.danyilmoroz.varusdocstorage.repository.DocumentRepository;
import com.github.danyilmoroz.varusdocstorage.service.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.persistence.EntityNotFoundException;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Controller
@RequestMapping("files")
@CrossOrigin
public class FileController {

    @Value("${files.upload.uploadPath}")
    private String uploadPath;

    @Autowired
    private DocumentRepository documentRepository;

    @Autowired
    private FileService fileService;

    @GetMapping("{folder}/{name}")
    public ResponseEntity<Resource> download(@PathVariable String folder, @PathVariable String name) throws IOException {

        File file = new File(uploadPath + "/" + "documents/" + folder + "/document/" + name);
        Path p = Paths.get(file.getAbsolutePath());
        ByteArrayResource resource = new ByteArrayResource(Files.readAllBytes(p));
        HttpHeaders headers = new HttpHeaders();
        headers.set("content-disposition", "inline; filename=\"" + file.getName() + "\"");
        headers.set("content-length", String.valueOf(file.length()));

        return ResponseEntity.ok()
                .headers(headers)
                .contentLength(file.length())
                .contentType(MediaType.parseMediaType("application/octet-stream"))
                .body(resource);
    }

    @GetMapping("{id}/delete/{name}")
    public ResponseEntity deleteImage(@PathVariable Long id, @PathVariable String name) {
        Document document = documentRepository.findById(id).orElseThrow(EntityNotFoundException::new);
        document = fileService.deleteImage(document, name);
        documentRepository.save(document);
        return ResponseEntity.ok().build();
    }

}
