package com.github.danyilmoroz.varusdocstorage.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@Entity
@Table(name = "documents")
public class Document {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String name;
    private String author;
    @Column(length = 65535)
    private String description;
    private LocalDateTime creationTime;
    private String filesRelativePath;
    private String filesFolder;
    private String documentFileName;
    @ElementCollection(targetClass = String.class, fetch = FetchType.EAGER)
    private List<String> imageNames;
}
