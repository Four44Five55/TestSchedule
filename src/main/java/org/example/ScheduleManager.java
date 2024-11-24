package org.example;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;

public class ScheduleManager {

    private Schedule schedule;
    private ExcelReader excelReader;

    public ScheduleManager() {
        schedule = new Schedule();
        excelReader = new ExcelReader();
    }


    public Schedule createScheduleFromExcel(String teachersDir, String groupsDir, String classroomsDir) throws IOException {
        schedule = excelReader.readScheduleFromExcel(teachersDir, groupsDir, classroomsDir);
        return schedule;
    }

    public void distributeLessons(Map<String, List<String>> lessonsByType) {
        schedule.distributeLessons(lessonsByType);
    }

    public void printSchedule() {
        schedule.printSchedule();
    }

    // Другие методы управления расписанием, например:
    public void addLesson(LocalDate date, int pairNumber, String subject, LocalTime startTime, LocalTime endTime, String teacherName, String groupName, String classroomNumber) {
        schedule.addLesson(date, pairNumber, subject, startTime, endTime, teacherName, groupName, classroomNumber);
    }
    public void removeLesson(LocalDate date, int pairNumber){
        Day day = schedule.findDay(date);

        if (day != null) {

            if (pairNumber > 0 && pairNumber <= day.getMaxPairs()){
                day.removeLesson(pairNumber);
            }
        }
    }

}