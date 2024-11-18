package com.fouj;

import java.io.Serializable;

public class Student implements Serializable {

    private String id;
    private String name;
    private String major;
    private String language;
    private double gpa;

    public Student() {
        this("No id", "No name", "No language", "No major", 0);
    }

    public Student(String id, String name, String language, String major, double gpa) {
        this.id = id;
        this.name = name;
        this.language = language;
        this.major = major;
        this.gpa = gpa;
    }

    public String getName() {
        return name;
    }

    public Student setName(String name) {
        this.name = name;
        return this;
    }

    public String getId() {
        return id;
    }

    public Student setId(String id) {
        this.id = id;
        return this;
    }

    public String getMajor() {
        return major;
    }

    public Student setMajor(String major) {
        this.major = major;
        return this;
    }

    public String getLanguage() {
        return language;
    }

    public Student setLanguage(String language) {
        this.language = language;
        return this;
    }

    public double getGpa() {
        return gpa;
    }

    public Student setGpa(double gpa) {
        this.gpa = gpa;
        return this;
    }

    @Override
    public String toString() {
        return String.format("%-20s %-10s %-15s %-10s %-5.2f", name, id, major, language, gpa);
    }
}
