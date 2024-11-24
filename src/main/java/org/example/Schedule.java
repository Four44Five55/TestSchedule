package org.example;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.*;

class Schedule {
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy");
    private static final LocalTime START_TIME = LocalTime.of(9, 0);
    private static final int LESSON_DURATION_MINUTES = 90;
    private List<Day> days;
    private List<Teacher> teachers;
    private List<Group> groups;
    private List<Classroom> classrooms;


    public Schedule() {
        days = new ArrayList<>();
        teachers = new ArrayList<>();
        groups = new ArrayList<>();
        classrooms = new ArrayList<>();

        // Инициализируем дни недели (пн-сб)
        LocalDate startDate = LocalDate.now().with(TemporalAdjusters.nextOrSame(DayOfWeek.MONDAY));
        for (int i = 0; i < 6; i++) {
            days.add(new Day(startDate.plusDays(i), i == 5 ? 3 : 4));
        }
    }

    public void addTeacher(Teacher teacher) {
        if (teachers.stream().noneMatch(t -> t.getName().equals(teacher.getName()))) {
            teachers.add(teacher);
        }
    }

    public void addGroup(Group group) {
        if (groups.stream().noneMatch(g -> g.getName().equals(group.getName()))) {
            groups.add(group);
        }
    }

    public void addClassroom(Classroom classroom) {
        if (classrooms.stream().noneMatch(c -> c.getNumber().equals(classroom.getNumber()))) {
            classrooms.add(classroom);
        }
    }

    public void addDay(Day day) {
        if (days.stream().noneMatch(d -> d.getDate().equals(day.getDate()))) {
            days.add(day);
        }
    }

    public void addLesson(LocalDate date, int pairNumber, String subject, LocalTime startTime, LocalTime endTime,
                          String teacherName, String groupName, String classroomNumber) {

        Day day = findDay(date);
        Teacher teacher = findTeacherByName(teacherName);
        Group group = findGroupByName(groupName);
        Classroom classroom = findClassroomByNumber(classroomNumber);

        if (day != null && teacher != null && group != null && classroom != null) {
            if (pairNumber >= 1 && pairNumber <= day.getMaxPairs()) {
                if (isTimeSlotAvailable(teacher, date, startTime, endTime) &&
                        isTimeSlotAvailable(group, date, startTime, endTime) &&
                        classroom.isAvailable(date, startTime, endTime)) {

                    Lesson lesson = new Lesson(subject, startTime, endTime, teacher, group, classroom, date);
                    day.addLesson(lesson, pairNumber);
                    teacher.addLesson(lesson);
                    group.addLesson(lesson);
                    classroom.addLesson(lesson);
                }
            }
        }
    }


    public Day findDay(LocalDate date) {
        return days.stream()
                .filter(day -> day.getDate().equals(date))
                .findFirst()
                .orElse(null);
    }

    public Teacher findTeacherByName(String teacherName) {
        return teachers.stream()
                .filter(teacher -> teacher.getName().equals(teacherName))
                .findFirst()
                .orElse(null);
    }

    public Group findGroupByName(String groupName) {
        return groups.stream()
                .filter(group -> group.getName().equals(groupName))
                .findFirst()
                .orElse(null);
    }

    public Classroom findClassroomByNumber(String classroomNumber) {
        return classrooms.stream()
                .filter(classroom -> classroom.getNumber().equals(classroomNumber))
                .findFirst()
                .orElse(null);
    }

    private boolean isTimeSlotAvailable(Teacher teacher, LocalDate date, LocalTime startTime, LocalTime endTime) {
        return teacher.isAvailable(date, startTime, endTime);
    }

    private boolean isTimeSlotAvailable(Group group, LocalDate date, LocalTime startTime, LocalTime endTime) {
        return group.isAvailable(date, startTime, endTime);
    }


    private boolean isTimeSlotAvailableForGroups(LocalDate date, LocalTime startTime, LocalTime endTime) {
        for (Group group : groups) {
            if (!isTimeSlotAvailable(group, date, startTime, endTime)) {
                return false;
            }

        }
        return true;
    }


    private LocalTime getStartLessonTime(int pairNumber) {
        return START_TIME.plusMinutes((pairNumber - 1) * LESSON_DURATION_MINUTES);

    }

    private LocalTime getEndLessonTime(int pairNumber) {

        return START_TIME.plusMinutes(pairNumber * LESSON_DURATION_MINUTES);
    }


    public void distributeLessons(Map<String, List<String>> lessonsByType) {

        for (Map.Entry<String, List<String>> entry : lessonsByType.entrySet()) {
            String lessonType = entry.getKey();
            List<String> subjects = entry.getValue();

            for (String subject : subjects) {
                if (lessonType.equals("Лекция")) {
                    scheduleLecture(subject);
                } else {
                    scheduleOtherLessonType(lessonType, subject);

                }
            }
        }
    }

    private void scheduleLecture(String subject) {
        for (Day day : days) {
            for (int pair = 1; pair <= day.getMaxPairs(); pair++) {
                for (Classroom classroom : classrooms) {
                    for (Teacher teacher : teachers) {
                        if (teacher.getSubjects().contains(subject)) {
                            if (isTimeSlotAvailable(teacher, day.getDate(), getStartLessonTime(pair), getEndLessonTime(pair)) &&
                                    isTimeSlotAvailableForGroups(day.getDate(), getStartLessonTime(pair), getEndLessonTime(pair)) &&
                                    classroom.isAvailable(day.getDate(), getStartLessonTime(pair), getEndLessonTime(pair))) {

                                for (Group group : groups) {
                                    addLesson(day.getDate(), pair, subject, getStartLessonTime(pair), getEndLessonTime(pair), teacher.getName(), group.getName(), classroom.getNumber());
                                }

                                return; // Занятие запланировано, выходим из циклов


                            }

                        }

                    }
                }
            }


        }


    }

    private void scheduleOtherLessonType(String lessonType, String subject) {
        for (Group group : groups) {
            for (Day day : days) {
                for (int pair = 1; pair <= day.getMaxPairs(); pair++) {
                    for (Classroom classroom : classrooms) {
                        for (Teacher teacher : teachers) {
                            if (teacher.getSubjects().contains(subject)) {

                                if (isTimeSlotAvailable(teacher, day.getDate(), getStartLessonTime(pair), getEndLessonTime(pair)) &&
                                        isTimeSlotAvailable(group, day.getDate(), getStartLessonTime(pair), getEndLessonTime(pair)) &&
                                        classroom.isAvailable(day.getDate(), getStartLessonTime(pair), getEndLessonTime(pair))) {

                                    addLesson(day.getDate(), pair, subject, getStartLessonTime(pair), getEndLessonTime(pair), teacher.getName(), group.getName(), classroom.getNumber());

                                    return;


                                }


                            }

                        }

                    }

                }

            }

        }
    }


    public List<Day> getDays() {
        return days;
    }

    public List<Teacher> getTeachers() {
        return teachers;
    }

    public void printSchedule() {
        Collections.sort(days, Comparator.comparing(Day::getDate));

        for (Day day : days) {
            System.out.println("Дата: " + day.getDate().format(DATE_FORMATTER));
            for (int i = 0; i < day.getMaxPairs(); i++) {
                Lesson lesson = day.getLesson(i + 1);
                if (lesson != null) {
                    System.out.print("  Пара " + (i + 1) + ": " + lesson.getSubject() +
                            ", Преподаватель: " + lesson.getTeacher().getName() +
                            ", Группа: " + lesson.getGroup().getName() +
                            ", Аудитория: " + lesson.getClassroom().getNumber());
                    System.out.println(", Время: " + lesson.getStartTime() + "-" + lesson.getEndTime());
                } else {
                    System.out.println("  Пара " + (i + 1) + ": Свободно");
                }
            }
            System.out.println();
        }
    }


}
