package com.language.subject.entity;

import com.language.category.entity.Category;
import com.language.common.enums.Language;
import com.language.lesson.entity.Lesson;
import com.language.vocab.entity.Vocab;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "subject")
public class Subject {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String subjectName;
    @OneToMany(mappedBy = "subject")
    private List<Vocab> vocabs = new ArrayList<>();
    @OneToOne(mappedBy = "subject")
    private Lesson lesson;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Language language;
    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;

    public Lesson getLesson() {
        return lesson;
    }

    public void setLesson(Lesson lesson) {
        this.lesson = lesson;
    }

    public Subject() {
    }

    public Long getId() {
        return id;
    }

    public String getSubjectName() {
        return subjectName;
    }

    public void setSubjectName(String subjectName) {
        this.subjectName = subjectName;
    }

    public List<Vocab> getVocabs() {
        return vocabs;
    }

    public void addVocab(Vocab vocab) {
        vocabs.add(vocab);
        vocab.setSubject(this);
    }

    public Language getLanguage() {
        return language;
    }

    public void setLanguage(Language language) {
        this.language = language;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }
}
