## Описание программы и классов

Эта программа предназначена для автоматического создания расписания занятий на основе данных из файлов Excel. Она учитывает различные типы занятий (лекции, практики, лабораторные работы), доступность преподавателей, групп и аудиторий, а также возможность проведения занятий в составе нескольких групп.  Программа равномерно распределяет занятия по дням и парам, а затем сохраняет результат в виде нового Excel файла.

### Классы:

1.  **`Teacher` (Преподаватель):**
    *   `name` (String): Имя преподавателя.
    *   `subjects` (List\<String>): Список предметов, которые ведет преподаватель.
    *   `lessons` (List\<Lesson>): Список занятий преподавателя.
    *   Методы: `getName()`, `getSubjects()`, `setSubjects()`, `addLesson()`, `removeLesson()`, `isAvailable()`, `toString()`, `getLessons()`

2.  **`Group` (Группа):**
    *   `name` (String): Название группы.
    *   `students` (List\<String>): Список студентов в группе (не используется в текущей реализации).
    *   `lessons` (List\<Lesson>): Список занятий группы.
    *   Методы: `getName()`, `addLesson()`, `removeLesson()`, `isAvailable()`, `toString()`

3.  **`Classroom` (Аудитория):**
    *   `number` (String): Номер аудитории.
    *   `capacity` (int): Вместимость аудитории (не используется в текущей реализации).
    *   `lessons` (List\<Lesson>): Список занятий в аудитории.
    *   Методы: `getNumber()`, `addLesson()`, `removeLesson()`, `isAvailable()`, `toString()`

4.  **`Lesson` (Занятие):**
    *   `subject` (String): Название предмета.
    *   `startTime` (LocalTime): Время начала занятия.
    *   `endTime` (LocalTime): Время окончания занятия.
    *   `teacher` (Teacher): Преподаватель, проводящий занятие.
    *   `group` (Group): Группа, у которой занятие.
    *   `classroom` (Classroom): Аудитория, в которой проходит занятие.
    *   `date` (LocalDate): Дата занятия.
    *   Методы: `getSubject()`, `getStartTime()`, `getEndTime()`, `getTeacher()`, `getGroup()`, `getClassroom()`, `getDate()`, `toString()`


5.  **`Day` (День):**
    *   `date` (LocalDate): Дата.
    *   `lessons` (Lesson[]): Массив занятий в этот день.
    *   `maxPairs` (int): Максимальное количество пар в день.
    *   Методы: `getDate()`, `getLesson()`, `addLesson()`, `removeLesson()`, `getMaxPairs()`, `toString()`

6.  **`Schedule` (Расписание):**
    *   `days` (List\<Day>): Список дней.
    *   `teachers` (List\<Teacher>): Список преподавателей.
    *   `groups` (List\<Group>): Список групп.
    *   `classrooms` (List\<Classroom>): Список аудиторий.
    *   Методы: `addTeacher()`, `addGroup()`, `addClassroom()`, `addDay()`, `addLesson()`, `findDay()`, `findTeacherByName()`, `findGroupByName()`, `findClassroomByNumber()`, `isTimeSlotAvailable()`, `distributeLessons()`, `scheduleLecture()`, `scheduleOtherLessonType()`, `getDays()`, `getTeachers()`, `printSchedule()`

7.  **`ExcelReader` (Чтение данных из Excel):**
    *   Методы: `readScheduleFromExcel()`, `readTeachers()`, `readGroups()`, `readClassrooms()`, `readSheet()`,  `extractSubjectsForGroup()`, `extractSubjectsForClassroom()`, `getTeacherForSubject()`

8.  **`Main` (Главный класс):**
    *   `main()` :  Точка входа в программу.


## Инструкция по использованию программы

1.  **Подготовка данных:** Создайте три директории: `teachers`, `groups` и `classrooms`.  В каждой директории разместите файлы Excel, соответствующие преподавателям, группам и аудиториям.  Формат файлов Excel должен соответствовать описанию, приведенному в предыдущих ответах.  Каждая строка в Excel файле описывает один день, а каждый столбец -- неделю.
2.  **Запуск программы:**  Скомпилируйте и запустите программу.  Программа прочитает данные из Excel файлов, распределит занятия и выведет расписание на консоль.
3.  **Настройка параметров:**  Вы можете изменить параметры расписания, такие как время начала занятий (`START_TIME`), продолжительность пары (`LESSON_DURATION_MINUTES`), формат даты (`DATE_FORMATTER`), в коде программы.
4.  **Проверка результатов:** Проверьте выведенное расписание на консоли на наличие ошибок или конфликтов.
5.  **Изменение входных данных:**  При необходимости измените данные в Excel файлах и перезапустите программу.



**Пример структуры файла `lessonsByType` в `Main`:**

```java
Map<String, List<String>> lessonsByType = new HashMap<>();
lessonsByType.put("Лекция", Arrays.asList("Математика", "Физика"));
lessonsByType.put("Практика", Arrays.asList("Программирование", "Алгебра"));
lessonsByType.put("Лабораторная работа", Arrays.asList("Химия", "Физика"));
```

Этот код создает отображение типов занятий на списки предметов.  Например, лекции проводятся по математике и физике, практики -- по программированию и алгебре, и так далее.  Вы можете изменить этот код, чтобы добавить другие типы занятий или предметы.

**Важно:**  Убедитесь, что названия предметов в `lessonsByType` соответствуют названиям предметов в файлах Excel для преподавателей, групп и аудиторий.  Также убедитесь, что названия типов занятий ("Лекция", "Практика", и т.д.) используются согласованно во всех частях программы.