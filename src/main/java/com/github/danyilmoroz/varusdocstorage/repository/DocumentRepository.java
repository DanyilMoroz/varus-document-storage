package com.github.danyilmoroz.varusdocstorage.repository;

import com.github.danyilmoroz.varusdocstorage.model.Document;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DocumentRepository extends JpaRepository<Document, Long> {

}
