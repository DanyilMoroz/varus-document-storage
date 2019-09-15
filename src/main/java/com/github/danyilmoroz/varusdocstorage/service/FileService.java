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
        File documentFilesFolders = new File(uploadPath + "/documents/" + docUUID);
        documentFilesFolders.mkdirs();
        document.setFilesAbsolutePath(documentFilesFolders.getAbsolutePath());

        if (documentFile != null && !documentFile.isEmpty()) {
            File documentFolder = new File(documentFilesFolders.getAbsolutePath() + "/document");
            documentFolder.mkdir();
            documentFile.transferTo(new File(documentFolder.getAbsolutePath() + "/" + documentFile.getOriginalFilename()));
            document.setDocumentFileName(documentFile.getOriginalFilename());
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

    public void deleteFiles(Document document) throws IOException {
        if(document.getFilesAbsolutePath() != null){
            File filesToDelete = new File(document.getFilesAbsolutePath());
            FileUtils.deleteDirectory(filesToDelete);
        }
    }

    public Document deleteImage(Document document, String imageName) {
        File imageToDelete = new File(document.getFilesAbsolutePath() + "/images/" + imageName);

        if (imageToDelete.exists()) {
            imageToDelete.delete();
        }
        document.getImageNames().remove(imageName);
        return document;
    }

    public Document update(Document document, MultipartFile documentFile, List<MultipartFile> album) throws IOException {

        String documentFolder = document.getFilesAbsolutePath() + "/document";
        String albumFolder = document.getFilesAbsolutePath() + "/images";

        if (documentFile != null && !documentFile.isEmpty()) {
            File oldDocument = new File(documentFolder + "/" + document.getDocumentFileName());
            if (oldDocument.exists()) {
                oldDocument.delete();
            }
            documentFile.transferTo(new File(documentFolder + "/" + documentFile.getOriginalFilename()));
        }

        if (!isAlbumEmpty(album)) {
            File images = new File(albumFolder);
            for (MultipartFile image : album) {
                document.getImageNames().add(image.getOriginalFilename());
                image.transferTo(new File(images.getAbsolutePath() + "/" + image.getOriginalFilename()));
            }
        }
        return document;
    }

    public boolean isAlbumEmpty(List<MultipartFile> multipartFiles) {
        boolean isEmpty = true;
        for (MultipartFile f : multipartFiles) {
            isEmpty = f.isEmpty();
        }
        return isEmpty;
    }
}
