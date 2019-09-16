package com.github.danyilmoroz.varusdocstorage.service;

import com.github.danyilmoroz.varusdocstorage.model.Document;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class FileService {

    @Value("${files.upload.uploadPath}")
    private String uploadPath;


    public Document saveFiles(Document document, MultipartFile documentFile, List<MultipartFile> album) throws IOException {
        File uploadDir = new File(uploadPath);
        if (!uploadDir.exists()) {
            uploadDir.mkdir();
        }

        String docUUID = "doc_" + UUID.randomUUID().toString();
        document.setFilesFolder(docUUID);
        File documentFilesFolders = new File(uploadDir.getAbsolutePath() + "/documents/" + docUUID);
        documentFilesFolders.mkdirs();
        document.setFilesRelativePath("/documents/" + docUUID);

        if (documentFile != null && !documentFile.isEmpty()) {
            File documentFolder = new File(documentFilesFolders.getAbsolutePath() + "/document");
            documentFolder.mkdir();
            document.setDocumentFileName(documentFile.getOriginalFilename());
            documentFile.transferTo(new File(documentFolder.getAbsolutePath() + "/" + documentFile.getOriginalFilename()));
        }

        if (!isAlbumEmpty(album)) {
            File imagesFolder = new File(documentFilesFolders.getAbsolutePath() + "/images");
            imagesFolder.mkdir();
            List<String> imgNames = new ArrayList<>();
            for (MultipartFile image : album) {
                imgNames.add(image.getOriginalFilename());
                image.transferTo(new File(imagesFolder.getAbsolutePath() + "/" + image.getOriginalFilename()));
            }
            document.setImageNames(imgNames);
        }
        return document;
    }

    public Document update(Document document, MultipartFile documentFile, List<MultipartFile> album) throws IOException {

        File upload = new File(uploadPath);
        if (document.getFilesRelativePath().isEmpty()) {
            String docUUID = "doc_" + UUID.randomUUID().toString();
            document.setFilesFolder(docUUID);
            document.setFilesRelativePath("/documents/" + docUUID);
        }
        File documentPath = new File(upload.getAbsolutePath() + document.getFilesRelativePath() + "/document");
        if (!documentPath.exists()) {
            documentPath.mkdirs();
        }
        if (documentFile != null && !documentFile.isEmpty()) {
            File oldDocument = new File(documentPath.getAbsolutePath() + "/" + document.getDocumentFileName());
            oldDocument.delete();
            documentPath.mkdirs();
            document.setDocumentFileName(documentFile.getOriginalFilename());
            documentFile.transferTo(new File(documentPath.getAbsolutePath() + "/" + documentFile.getOriginalFilename()));
        }

        File albumPath = new File(upload.getAbsolutePath() + document.getFilesRelativePath() + "/images");
        if (!albumPath.exists()) {
            albumPath.mkdirs();
        }
        if (!isAlbumEmpty(album)) {
            for (MultipartFile image : album) {
                document.getImageNames().add(image.getOriginalFilename());
                image.transferTo(new File(albumPath.getAbsolutePath() + "/" + image.getOriginalFilename()));
            }
        }
        return document;
    }

    public void deleteFiles(Document document) throws IOException {
        if (document.getFilesRelativePath() != null) {
            File upload = new File(uploadPath);
            File filesToDelete = new File(upload.getAbsolutePath() + document.getFilesRelativePath());
            FileUtils.deleteDirectory(filesToDelete);
        }
    }

    public Document deleteImage(Document document, String imageName) {
        File upload = new File(uploadPath);
        File imageToDelete = new File(upload.getAbsolutePath() + document.getFilesRelativePath() + "/images/" + imageName);

        if (imageToDelete.exists()) {
            imageToDelete.delete();
        }
        document.getImageNames().remove(imageName);
        return document;
    }

    public boolean isAlbumEmpty(List<MultipartFile> multipartFiles) {
        boolean isEmpty = true;
        for (MultipartFile f : multipartFiles) {
            isEmpty = f.isEmpty();
        }
        return isEmpty;
    }

    public String uploadPath() {
        File file = new File(uploadPath);
        return file.getAbsolutePath();
    }
}
