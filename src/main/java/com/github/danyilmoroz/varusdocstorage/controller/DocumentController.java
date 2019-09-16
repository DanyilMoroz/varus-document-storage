package com.github.danyilmoroz.varusdocstorage.controller;

import com.github.danyilmoroz.varusdocstorage.model.Document;
import com.github.danyilmoroz.varusdocstorage.model.User;
import com.github.danyilmoroz.varusdocstorage.repository.DocumentRepository;
import com.github.danyilmoroz.varusdocstorage.service.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.EntityNotFoundException;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

@Controller
@RequestMapping("/")
public class DocumentController {

    @Autowired
    private DocumentRepository documentRepository;

    @Autowired
    private FileService fileService;

    @Value("${files.upload.uploadPath}")
    private String uploadPath;

    @GetMapping
    public String index(Model model) {
        model.addAttribute("documents", documentRepository.findAll());
        return "index";
    }

    @GetMapping("document/{id}")
    public String getDocument(@PathVariable Long id, Model model) {
        Document document = documentRepository.findById(id).orElseThrow(EntityNotFoundException::new);
        model.addAttribute("uploadPath", fileService.uploadPath());
        model.addAttribute("document", document);
        return "document";
    }

    @GetMapping("add")
    public String addForm(Model model) {
        model.addAttribute("newDocument", new Document());
        return "add";
    }

    @PostMapping("add")
    public String addDocument(@ModelAttribute("newDocument") Document document,
                              @RequestParam(value = "document", required = false) MultipartFile documentFile,
                              @RequestParam(value = "album", required = false) List<MultipartFile> album,
                              @AuthenticationPrincipal User user, Model model) throws IOException {
        Document newDocument = document;

        if (document != null) {
            newDocument.setCreationTime(LocalDateTime.now());
            newDocument.setAuthor(user.getUsername());
        }

        if (!documentFile.isEmpty() || !fileService.isAlbumEmpty(album)) {
            newDocument = fileService.saveFiles(document, documentFile, album);
        }

        documentRepository.save(newDocument);
        model.addAttribute("document", documentRepository.findAll());
        return "redirect:/";
    }

    @GetMapping("edit/{id}")
    public String updateDocumentForm(@PathVariable Long id, Model model) {
        model.addAttribute("uploadPath", fileService.uploadPath());
        model.addAttribute("document", documentRepository.findById(id).orElseThrow(EntityNotFoundException::new));
        return "update";
    }

    @PostMapping("edit")
    public String updateDocument(@ModelAttribute Document document,
                                 @RequestParam(value = "document", required = false) MultipartFile documentFile,
                                 @RequestParam(value = "album", required = false) List<MultipartFile> album,
                                 Model model) throws IOException {

        Document updatedDocument = document;

        if (!documentFile.isEmpty() || !fileService.isAlbumEmpty(album)) {
            updatedDocument = fileService.update(document, documentFile, album);
        }
        documentRepository.save(updatedDocument);
        model.addAttribute("documents", documentRepository.findAll());
        return "redirect:/";
    }

    @GetMapping("delete/{id}")
    public String deleteDocument(@PathVariable Long id, Model model) throws IOException {
        Document document = documentRepository.findById(id).orElseThrow(EntityNotFoundException::new);
        fileService.deleteFiles(document);
        documentRepository.deleteById(id);
        model.addAttribute("documents", documentRepository.findAll());
        return "redirect:/";
    }
}
