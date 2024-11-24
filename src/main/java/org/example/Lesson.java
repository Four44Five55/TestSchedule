package org.example;

import java.time.LocalDate;
import java.time.LocalTime;

public class Lesson {
    private String subject;
    private LocalTime startTime;
    private LocalTime endTime;
    private Teacher teacher;
    private Group group;
    private Classroom classroom;
    private LocalDate date;

    public Lesson(String subject, LocalTime startTime, LocalTime endTime, Teacher teacher, Group group, Classroom classroom, LocalDate date) {
        this.subject = subject;
        this.startTime = startTime;
        this.endTime = endTime;
        this.teacher = teacher;
        this.group = group;
        this.classroom = classroom;
        this.date = date;
    }

    public String getSubject() {
        return subject;
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public LocalTime getEndTime() {
        return endTime;
    }

    public Teacher getTeacher() {
        return teacher;
    }

    public Group getGroup() {
        return group;
    }

    public Classroom getClassroom() {
        return classroom;
    }

    public LocalDate getDate() {
        return date;
    }


    @Override
    public String toString() {
        return subject + " (" + startTime + "-" + endTime + "), "
                + teacher.getName() + ", " + group.getName() + ", ауд. " + classroom.getNumber();
    }
}
