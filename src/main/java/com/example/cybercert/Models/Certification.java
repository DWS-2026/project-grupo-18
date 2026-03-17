package com.example.cybercert.Models;

import jakarta.persistence.*;

import java.sql.Blob;
import java.util.List;

@Entity
@Table(name = "certifications")
public class Certification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    @Column(nullable = false)
    private String level;

    @Column(nullable = false)
    private Integer duration;

    @Column(nullable = false)
    private String format;

    @Column(nullable = false)
    private String language;

    @Column(length = 1000)
    private String description;

    @ElementCollection
    @CollectionTable(name = "certification_requirements", joinColumns = @JoinColumn(name = "certification_id"))
    @Column(name = "requirement")
    private List<String> requirements;

    @ElementCollection
    @CollectionTable(name = "certification_contents", joinColumns = @JoinColumn(name = "certification_id"))
    @Column(name = "content")
    private List<String> contents;

    @Column(nullable = false)
    private String imagePath;

    public Certification() {
    }

    public Certification(String name, String level, Integer duration, String format, String language,
            String description, List<String> requirements, List<String> contents, String imagePath) {
        this.name = name;
        this.level = level;
        this.duration = duration;
        this.format = format;
        this.language = language;
        this.description = description;
        this.requirements = requirements;
        this.contents = contents;
        this.imagePath = imagePath;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public Integer getDuration() {
        return duration;
    }

    public void setDuration(Integer duration) {
        this.duration = duration;
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<String> getRequirements() {
        return requirements;
    }

    public void setRequirements(List<String> requirements) {
        this.requirements = requirements;
    }

    public List<String> getContents() {
        return contents;
    }

    public void setContents(List<String> contents) {
        this.contents = contents;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

}
