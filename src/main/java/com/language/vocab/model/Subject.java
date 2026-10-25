package com.language.vocab.model;

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
}
