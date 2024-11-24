package org.example;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class Group {
    private String name;
    private List<String> students;
    private List<Lesson> lessons;


    public Group(String name, List<String> students) {
        this.name = name;
        this.students = students;
        this.lessons = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public void addLesson(Lesson lesson) {
        lessons.add(lesson);
    }

    public void removeLesson(Lesson lesson) {
        lessons.remove(lesson);
    }

    public boolean isAvailable(LocalDate date, LocalTime startTime, LocalTime endTime) {
        return lessons.stream().noneMatch(lesson ->
                lesson.getDate().equals(date) &&
                        !(endTime.isBefore(lesson.getStartTime()) || startTime.isAfter(lesson.getEndTime()))
        );
    }

    @Override
    public String toString() {
        return "Group{" +
                "name='" + name + '\'' +
                ", students=" + students +
                '}';
    }
}
