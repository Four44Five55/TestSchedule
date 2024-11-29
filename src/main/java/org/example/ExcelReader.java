package org.example;


import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;

class ExcelReader {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy");
    private static final LocalTime START_TIME = LocalTime.of(9, 0);
    private static final int LESSON_DURATION_MINUTES = 90;


    public Schedule readScheduleFromExcel(String teachersDir, String groupsDir, String classroomsDir) throws IOException {
        Schedule schedule = new Schedule();
        List<Teacher> teachers = readTeachers(teachersDir, schedule);
        Map<String, Set<String>> subjectsByGroup = new HashMap<>();

        teachers.forEach(schedule::addTeacher);
        readGroups(groupsDir, schedule, subjectsByGroup);
        readClassrooms(classroomsDir, schedule, subjectsByGroup);

        // Gather all unique subjects
        Set<String> allSubjects = new HashSet<>();
        for (Set<String> groupSubjects : subjectsByGroup.values()) {
            allSubjects.addAll(groupSubjects);
        }

        // Update each teacher's subject list with all unique subjects
        for (Teacher teacher : teachers) {
            teacher.setSubjects(new ArrayList<>(allSubjects));
        }

        return schedule;
    }


    private List<Teacher> readTeachers(String directory, Schedule schedule) throws IOException {
        List<Teacher> teachers = new ArrayList<>();
        File[] files = new File(directory).listFiles((dir, name) -> name.toLowerCase().endsWith(".xlsx"));
        if (files != null) {
            for (File file : files) {
                try (FileInputStream fis = new FileInputStream(file);
                     Workbook workbook = new XSSFWorkbook(fis)) {

                    String teacherName = file.getName().replace(".xlsx", "");
                    teachers.add(new Teacher(teacherName, new ArrayList<>()));
                    Sheet sheet = workbook.getSheetAt(0);
                    readSheet(sheet, teacherName, schedule);
                }
            }
        }
        return teachers;
    }

    private void readGroups(String directory, Schedule schedule, Map<String, Set<String>> subjectsByGroup) throws IOException {
        File[] files = new File(directory).listFiles((dir, name) -> name.toLowerCase().endsWith(".xlsx"));
        if (files != null) {
            for (File file : files) {
                try (FileInputStream fis = new FileInputStream(file);
                     Workbook workbook = new XSSFWorkbook(fis)) {
                    String groupName = file.getName().replace(".xlsx", "");

                    if (schedule.findGroupByName(groupName) == null) {
                        schedule.addGroup(new Group(groupName, new ArrayList<>()));
                    }

                    Sheet sheet = workbook.getSheetAt(0);
                    readSheet(sheet, groupName, schedule);
                    extractSubjectsForGroup(sheet, groupName, subjectsByGroup);
                }
            }
        }
    }

    private void readClassrooms(String directory, Schedule schedule, Map<String, Set<String>> subjectsByGroup) throws IOException {
        File[] files = new File(directory).listFiles((dir, name) -> name.toLowerCase().endsWith(".xlsx"));
        if (files != null) {
            for (File file : files) {
                try (FileInputStream fis = new FileInputStream(file);
                     Workbook workbook = new XSSFWorkbook(fis)) {

                    String classroomName = file.getName().replace(".xlsx", "");
                    if (schedule.findClassroomByNumber(classroomName) == null) {
                        schedule.addClassroom(new Classroom(classroomName, 30));
                    }

                    Sheet sheet = workbook.getSheetAt(0);
                    readSheet(sheet, classroomName, schedule);
                    extractSubjectsForClassroom(sheet, classroomName, subjectsByGroup);
                }
            }
        }
    }


    private void readSheet(Sheet sheet, String entityName, Schedule schedule) {
        int rowsPerWeek = 75;
        int rowsPerDay = 13; // Строк на каждый день (Пн-Пт)
        int rowsPerDaySat = 10; // строк на субботу

        int headerRows = 8;
        int startColumn = 3; // Начало данных с 4-го столбца (индекс 3)

        for (int week = 0; week < 30; week++) {
            for (int day = 0; day < 6; day++) { // 6 дней (пн-сб)
                int dayOffset = day < 5 ? day * rowsPerDay : 5 * rowsPerDay + (day - 5) * rowsPerDaySat;
                int rowOffset = headerRows;
                Row currentRow = sheet.getRow(rowOffset + dayOffset);
                if (currentRow == null) continue;
                int currentCol = week + startColumn; // Добавляем startColumn


                //Row currentRow = sheet.getRow(headerRows);
                if (currentRow == null) continue;

                Cell dateCell = currentRow.getCell(currentCol);

                if (isValidCell(dateCell) && dateCell.getCellType() == CellType.NUMERIC && DateUtil.isCellDateFormatted(dateCell)) {
                    // if (isValidCell(dateCell)) {
                    try {
                        LocalDate date = dateCell.getLocalDateTimeCellValue().toLocalDate();


                        Day daySchedule = schedule.findDay(date);
                        if (daySchedule == null) {
                            daySchedule = new Day(date, day == 5 ? 3 : 4);
                            schedule.addDay(daySchedule);
                        }

                        int maxPairs = (day == 5) ? 3 : 4;
                        for (int pair = 0; pair < maxPairs; pair++) {
                            int pairOffset = pair * 3;

                            Cell typeCell = sheet.getRow(headerRows + 1 + pairOffset).getCell(currentCol);
                            Cell entityCell = sheet.getRow(headerRows + 2 + pairOffset).getCell(currentCol);
                            Cell subjectOrClassroomCell = sheet.getRow(headerRows + 3 + pairOffset).getCell(currentCol);

                            if (isValidCell(typeCell) && isValidCell(entityCell) && isValidCell(subjectOrClassroomCell)) {
                                String subjectName = "";
                                String classroomName = "";
                                String groupName = "";
                                String teacherName = "";
                                String lessonType = typeCell.getStringCellValue();

                                if (entityName.startsWith("Classroom")) {
                                    groupName = entityCell.getStringCellValue();
                                    subjectName = subjectOrClassroomCell.getStringCellValue();
                                    classroomName = entityName;
                                    Teacher teacher = getTeacherForSubject(schedule, groupName, date, pair + 1, subjectName);
                                    if (teacher != null) {
                                        teacherName = teacher.getName();
                                    }
                                } else if (entityName.startsWith("Group")) {
                                    subjectName = entityCell.getStringCellValue();
                                    classroomName = subjectOrClassroomCell.getStringCellValue();
                                    groupName = entityName;
                                    Teacher teacher = getTeacherForSubject(schedule, groupName, date, pair + 1, subjectName);
                                    if (teacher != null) {
                                        teacherName = teacher.getName();
                                    }
                                } else {
                                    subjectName = typeCell.getStringCellValue();
                                    groupName = entityCell.getStringCellValue();
                                    classroomName = subjectOrClassroomCell.getStringCellValue();
                                    teacherName = entityName;
                                }

                                if (!teacherName.isEmpty() && schedule.findGroupByName(groupName) != null && schedule.findClassroomByNumber(classroomName) != null) {
                                    LocalTime startTime = START_TIME.plusMinutes(pair * LESSON_DURATION_MINUTES);
                                    LocalTime endTime = startTime.plusMinutes(LESSON_DURATION_MINUTES);
                                    schedule.addLesson(date, pair + 1, subjectName, startTime, endTime, teacherName, groupName, classroomName);
                                }
                            }
                        }
                    } catch (DateTimeParseException e) {
                        System.err.println("Ошибка парсинга даты в ячейке: строка " + (headerRows + 1) + ", столбец " + (currentCol + 1) + ", лист: " + sheet.getSheetName());
                    }

                }


            }

        }


    }


    private boolean isValidCell(Cell cell) {
        return cell != null && cell.getCellType() == CellType.STRING && !cell.getStringCellValue().isEmpty();
    }

    private void extractSubjectsForGroup(Sheet sheet, String groupName, Map<String, Set<String>> subjectsByGroup) {
        int rowsPerWeek = 75;
        int rowsPerDay = 13; // Строк на каждый день (Пн-Пт)
        int rowsPerDaySat = 10; // строк на субботу

        int headerRows = 8;
        int startColumn = 3; // Начало данных с 4-го столбца (индекс 3)


        /*for (int week = 0; week < 30; week++) {
            int weekOffset = week * (colsPerDay * 5 + colsPerDaySat); // Смещение для недели

            for (int day = 0; day < 6; day++) {
                int dayOffset = (day < 5) ? day * colsPerDay : 5 * colsPerDay + (day - 5) * colsPerDaySat; // Смещение для дня
                int currentCol = weekOffset + dayOffset;*/
        for (int week = 0; week < 30; week++) {
            //int weekOffset = week * (rowsPerDay * 5 + rowsPerDaySat); // week * 75 because 5 days with rowsPerDay and 1 day with colsPerDaySaturday = 75
            for (int day = 0; day < 6; day++) { // 6 дней (пн-сб)
                int dayOffset = day < 5 ? day * rowsPerDay : 5 * rowsPerDay + (day - 5) * rowsPerDaySat;
                int rowOffset = headerRows;
                Row currentRow = sheet.getRow(rowOffset + dayOffset);
                if (currentRow == null) continue;
                int currentCol = week + startColumn; // Добавляем startColumn
                int maxPairs = day == 5 ? 3 : 4;
                for (int pair = 0; pair < maxPairs; pair++) {
                    int pairOffset = pair * 3;
                    Cell subjectCell = sheet.getRow(headerRows + 2 + pairOffset).getCell(currentCol);

                    if (isValidCell(subjectCell)) {
                        subjectsByGroup.computeIfAbsent(groupName, k -> new HashSet<>()).add(subjectCell.getStringCellValue());
                    }
                }
            }
        }


    }

    private void extractSubjectsForClassroom(Sheet sheet, String classroomName, Map<String, Set<String>> subjectsByGroup) {
        int rowsPerWeek = 75;
        int rowsPerDay = 13; // Строк на каждый день (Пн-Пт)
        int rowsPerDaySat = 10; // строк на субботу

        int headerRows = 8;
        int startColumn = 3; // Начало данных с 4-го столбца (индекс 3)


/*
        for (int week = 0; week < 30; week++) {
            int weekOffset = week * (colsPerDay * 5 + colsPerDaySat); // Смещение для недели

            for (int day = 0; day < 6; day++) {
                int dayOffset = (day < 5) ? day * colsPerDay : 5 * colsPerDay + (day - 5) * colsPerDaySat; // Смещение для дня
                int currentCol = weekOffset + dayOffset;
*/
        for (int week = 0; week < 30; week++) {
            //int weekOffset = week * (rowsPerDay * 5 + rowsPerDaySat); // week * 75 because 5 days with rowsPerDay and 1 day with colsPerDaySaturday = 75
            for (int day = 0; day < 6; day++) { // 6 дней (пн-сб)
                int dayOffset = day < 5 ? day * rowsPerDay : 5 * rowsPerDay + (day - 5) * rowsPerDaySat;
                int rowOffset = headerRows;
                Row currentRow = sheet.getRow(rowOffset + dayOffset);
                if (currentRow == null) continue;
                int currentCol = week + startColumn; // Добавляем startColumn

                int maxPairs = day == 5 ? 3 : 4;
                for (int pair = 0; pair < maxPairs; pair++) {
                    int pairOffset = pair * 3;
                    Cell subjectCell = sheet.getRow(headerRows + 3 + pairOffset).getCell(currentCol);

                    if (isValidCell(subjectCell)) {
                        subjectsByGroup.computeIfAbsent(classroomName, k -> new HashSet<>()).add(subjectCell.getStringCellValue());
                    }
                }

            }

        }


    }


    private Teacher getTeacherForSubject(Schedule schedule, String groupName, LocalDate date, int pairNumber, String subjectName) {
        for (Teacher teacher : schedule.getTeachers()) {
            if (teacher.getSubjects().contains(subjectName)) {
                LocalTime startTime = START_TIME.plusMinutes((pairNumber - 1) * LESSON_DURATION_MINUTES);
                LocalTime endTime = startTime.plusMinutes(LESSON_DURATION_MINUTES);

                if (teacher.isAvailable(date, startTime, endTime) &&
                        schedule.findGroupByName(groupName).isAvailable(date, startTime, endTime)) {
                    return teacher;
                }
            }
        }
        return null;
    }

    public void printExcelSchedule(String filePath) throws IOException {


        try (FileInputStream fis = new FileInputStream(filePath);
             Workbook workbook = new XSSFWorkbook(fis)) {


            Sheet sheet = workbook.getSheetAt(0); // Получаем первый лист


            int rowsPerWeek = 75;
            int rowsPerDay = 13; // Строк на каждый день (Пн-Пт)
            int rowsPerDaySat = 10; // строк на субботу

            int headerRows = 8;
            int startColumn = 3; // Начало данных с 4-го столбца (индекс 3)


            for (int week = 0; week < 30; week++) {
                //int weekOffset = week * (rowsPerDay * 5 + rowsPerDaySat); // week * 75 because 5 days with rowsPerDay and 1 day with colsPerDaySaturday = 75
                for (int day = 0; day < 6; day++) { // 6 дней (пн-сб)
                    int dayOffset = day < 5 ? day * rowsPerDay : 5 * rowsPerDay + (day - 5) * rowsPerDaySat;
                    int rowOffset = headerRows;
                    Row currentRow = sheet.getRow(rowOffset + dayOffset);
                    if (currentRow == null) continue;
                    int currentCol = week + startColumn; // Добавляем startColumn
                    Cell dateCell = currentRow.getCell(currentCol);
                    if (dateCell != null && dateCell.getCellType() == CellType.NUMERIC && DateUtil.isCellDateFormatted(dateCell)) {
                        LocalDate date = dateCell.getLocalDateTimeCellValue().toLocalDate();

                        System.out.println("Неделя " + (week + 1) + ", " + date.format(DATE_FORMATTER));


                        for (int pair = 0; pair < (day == 5 ? 3 : 4); pair++) {

                            int pairOffset = pair * 3;

                            Cell typeCell = sheet.getRow(rowOffset + 1 + pairOffset).getCell(currentCol);
                            Cell entityCell = sheet.getRow(rowOffset + 2 + pairOffset).getCell(currentCol);
                            Cell subjectOrClassroomCell = sheet.getRow(rowOffset + 3 + pairOffset).getCell(currentCol);


                            if (isValidCell(typeCell) && isValidCell(entityCell) && isValidCell(subjectOrClassroomCell)) {
                                String type = typeCell.getStringCellValue();
                                String entity = entityCell.getStringCellValue();
                                String subjectOrClassroom = subjectOrClassroomCell.getStringCellValue();


                                System.out.println("  Пара " + (pair + 1) + ": " + type + " - " + entity + " - " + subjectOrClassroom);

                            } else {
                                System.out.println("  Пара " + (pair + 1) + ": -");
                            }


                        }
                        System.out.println();


                    }


                }
                System.out.println("--------------------"); // Разделитель недель
            }


        }


    }
}
