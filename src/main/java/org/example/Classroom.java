package org.example;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class Classroom {
    private String number;
    private int capacity;
    private List<Lesson> lessons;

    public Classroom(String number, int capacity) {
        this.number = number;
        this.capacity = capacity;
        this.lessons = new ArrayList<>();
    }

    public String getNumber() {
        return number;
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
        return "Classroom{" +
                "number='" + number + '\'' +
                ", capacity=" + capacity +
                '}';
    }
}
