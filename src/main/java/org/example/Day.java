package org.example;

import java.time.DayOfWeek;
import java.time.LocalDate;

public class Day {
    private LocalDate date;
    private Lesson[] lessons;
    private int maxPairs;

    public Day(LocalDate date) {
        this.date = date;
        this.maxPairs = 4; // По умолчанию 4 пары
        this.lessons = new Lesson[maxPairs];
    }


    public Day(LocalDate date, int maxPairs) {
        this.date = date;
        this.maxPairs = maxPairs;
        this.lessons = new Lesson[maxPairs];
    }

    public LocalDate getDate() {
        return date;
    }

    public Lesson getLesson(int pairNumber) {
        if (pairNumber >= 1 && pairNumber <= maxPairs) {
            return lessons[pairNumber - 1];
        }
        return null;
    }


    public void addLesson(Lesson lesson, int pairNumber) {
        if (pairNumber >= 1 && pairNumber <= maxPairs) {
            if (lessons[pairNumber - 1] == null) {
                lessons[pairNumber - 1] = lesson;
            } else {
                System.out.println("На это время (" + pairNumber + "-я пара) уже назначено занятие.");
            }
        } else {
            System.out.println("Неверный номер пары. Должен быть от 1 до " + maxPairs);
        }
    }

    public void removeLesson(int pairNumber) {
        if (
                pairNumber >= 1 && pairNumber <= maxPairs) {
            if (lessons[pairNumber - 1] != null) {
                lessons[pairNumber - 1] = null;
            } else {
                System.out.println("На это время (" + pairNumber + "-я пара) нет занятия.");
            }
        } else {
            System.out.println("Неверный номер пары. Должен быть от 1 до " + maxPairs);
        }
    }

    public int getMaxPairs() {
        return maxPairs;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Расписание на ").append(date).append(":\n");
        for (int i = 0; i < maxPairs; i++) {
            sb.append("  Пара ").append(i + 1).append(": ");
            if (lessons[i] != null) {
                sb.append(lessons[i]);
            } else {
                sb.append("Свободно");
            }
            sb.append("\n");
        }
        return sb.toString();
    }

}
