package org.example;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

class Teacher {
    private String name;
    private List<String> subjects;
    private List<Lesson> lessons;

    public Teacher(String name, List<String> subjects) {
        this.name = name;
        this.subjects = subjects;
        this.lessons = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public List<String> getSubjects() {
        return subjects;
    }

    public void setSubjects(List<String> subjects) {
        this.subjects = subjects;
    }


    public void addLesson(Lesson lesson) {
        this.lessons.add(lesson);
    }

    public void removeLesson(Lesson lesson) {
        this.lessons.remove(lesson);
    }

    public boolean isAvailable(LocalDate date, LocalTime startTime, LocalTime endTime) {
        for (Lesson lesson : lessons) {
            if (lesson.getDate().equals(date) && !((endTime.isBefore(lesson.getStartTime()) || startTime.isAfter(lesson.getEndTime())))) {
                return false; // Teacher is not available
            }
        }
        return true; // Teacher is available
    }

    @Override
    public String toString() {
        return "Teacher{" +
                "name='" + name + '\'' +
                ", subjects=" + subjects +
                '}';
    }

    public List<Lesson> getLessons() { return lessons; }


}