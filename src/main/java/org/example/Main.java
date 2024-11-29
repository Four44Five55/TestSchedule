package org.example;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Main {

    public static void main(String[] args) throws IOException {
        ScheduleManager manager = new ScheduleManager();
        ExcelReader reader = new ExcelReader();

        readTeachers:
        System.out.println("Рабочая директория: " + System.getProperty("user.dir"));
        String teachersDir = "C:\\Users\\1\\IdeaProjects\\TestSchedule\\src\\main\\resources\\teachers";
        String groupsDir = "C:\\Users\\1\\IdeaProjects\\TestSchedule\\src\\main\\resources\\groups";
        String classroomsDir = "C:\\Users\\1\\IdeaProjects\\TestSchedule\\src\\main\\resources\\classrooms";

        try {
            Schedule schedule = manager.createScheduleFromExcel(teachersDir, groupsDir, classroomsDir);

            /*Map<String, List<String>> lessonsByType = new HashMap<>();
            lessonsByType.put("Лекция", Arrays.asList("Математика", "Физика"));
            lessonsByType.put("Практика", Arrays.asList("Программирование", "Алгебра"));
            lessonsByType.put("Лабораторная работа", Arrays.asList("Химия", "Физика"));

            manager.distributeLessons(lessonsByType);
            manager.printSchedule();*/

            // Пример использования: вывод расписания из файла группы
            reader.printExcelSchedule("C:\\Users\\1\\IdeaProjects\\TestSchedule\\src\\main\\resources\\groups\\921.xlsx");


            //schedule.printSchedule();


        } catch (IOException e) {
            System.err.println("Ошибка при чтении файлов Excel: " + e.getMessage());
        }

    }
}

