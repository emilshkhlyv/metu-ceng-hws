import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.function.BinaryOperator;
import java.util.function.Function;

import static java.lang.Integer.parseInt;
import static java.util.Map.entry;
import static java.util.Map.ofEntries;
import static java.util.stream.Collectors.*;

public class SIS {
    private static String fileSep = File.separator;
    private static String lineSep = System.lineSeparator();
    private static String space   = " ";

    private List<Student> studentList = new ArrayList<>();

    public SIS(){ processOptics(); }

    private void processOptics(){
        try (Stream<Path> paths = Files.list(Paths.get("input"))) {
            paths.forEach(a -> {
                try {
                    List<List<String>> array = Files.lines(a).map(line -> Arrays.asList(line.split(space))).collect(Collectors.toList());

                    // get name studentID and surname of Student
                    List<String> Firstline = array.get(0);
                    String[] name = Firstline.stream().limit(Firstline.size()-2).toArray(String[]::new);
                    int studentID = parseInt(Firstline.get(Firstline.size()-1));
                    String surname = Firstline.get(Firstline.size()-2);

                    // get year, courseCode and courseCredit
                    List<String> SecondLine = array.get(1);
                    int year = parseInt(SecondLine.get(0));
                    int courseCode = parseInt(SecondLine.get(1));
                    int courseCredit = parseInt(SecondLine.get(2));

                    // get exampType
                    List<String> ThirdLine = array.get(2);
                    String examType = ThirdLine.get(0);

                    // get answers and calculate corresponding grade
                    List<String> FourthLine = array.get(3);
                    List<List<String>> anses = FourthLine.stream().map(character -> Arrays.asList(character.split(""))).collect(Collectors.toList());
                    List<String> answer = anses.get(0);
                    long allAnswers = answer.size();
                    long trueAnswers = answer.stream().filter(c -> c.equals("T")).count();
                    double grade = (double) trueAnswers / (double) allAnswers * 100.0;

                    // if there exist the student don't add the same student
                    boolean deri = studentList.stream().anyMatch(e -> e.getStudentID() == studentID && e.getSurname().equals(surname) && Arrays.equals(e.getNames(), name));
                    if(!deri) {
                        Student student = new Student(name, surname, studentID);
                        studentList.add(student);
                    }

                    // add course information to takenCourses
                    Course course = new Course(courseCode, year, examType, courseCredit, grade);
                    List<Student> star = studentList.stream().filter(e -> e.getStudentID() == studentID && e.getSurname().equals(surname) && Arrays.equals(e.getNames(), name)).collect(Collectors.toList());
                    star.get(0).getTakenCourses().add(course);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public double getGrade(int studentID, int courseCode, int year){
        //retrieve student with filtering studentList
        List<Student> temp = studentList.stream().filter(e -> e.getStudentID() == studentID).collect(Collectors.toList());
        Student student = temp.get(0);

        // took and filter courses for finding corresponding course
        List<Course> courses = student.getTakenCourses().stream().filter(p -> p.getCourseCode() == courseCode).filter(p -> p.getYear() == year).collect(Collectors.toList());

        // retrieve midterms from course
        List<Course> midterms = courses.stream().filter(p -> p.getExamType().startsWith("Midterm")).collect(Collectors.toList());

        // retrieve final from course
        List<Course> final1 = courses.stream().filter(p -> p.getExamType().equals("Final")).collect(Collectors.toList());

        // return the grade
        return midterms.get(0).getGrade()*0.25+midterms.get(1).getGrade()*0.25+final1.get(0).getGrade()*0.5;
    }

    public void updateExam(int studentID, int courseCode, String examType, double newGrade){
        // get student
        Student student = studentList.stream().filter(c -> c.getStudentID() == studentID).findFirst().orElse(null);
        if(student != null) {
            // filter course
            List<Course> course = student.getTakenCourses().stream().filter(c -> c.getCourseCode() == courseCode).collect(Collectors.toList());

            // set the new grade
            course.stream().filter(p -> p.getExamType().equals(examType)).max(Comparator.comparing(Course::getYear)).ifPresent(course1 -> course1.setGrade(newGrade));
        }
    }

    public void createTranscript(int studentID){
        Student student = studentList.stream().filter(c -> c.getStudentID() == studentID).findFirst().orElse(null);
        if(student != null){
            List<Course> courses = student.getTakenCourses();
            // Map for letter grades
            Map<Integer, String> letterGrades = new TreeMap<>();
            letterGrades.put(0, "FF");   letterGrades.put(5, "FF"); letterGrades.put(10, "FF"); letterGrades.put(15, "FF"); letterGrades.put(20, "FF"); letterGrades.put(25, "FF"); letterGrades.put(30, "FF"); letterGrades.put(35, "FF"); letterGrades.put(40, "FF"); letterGrades.put(45, "FF"); letterGrades.put(50, "FD"); letterGrades.put(55, "FD"); letterGrades.put(60, "DD"); letterGrades.put(65, "DC"); letterGrades.put(70, "CC"); letterGrades.put(75, "CB"); letterGrades.put(80, "BB"); letterGrades.put(85, "BA"); letterGrades.put(90, "AA"); letterGrades.put(95, "AA"); letterGrades.put(100, "AA");

            // Map for letter credits
            Map<String, Double> letterCredits = new TreeMap<>();
            letterCredits.put("AA", 4.00); letterCredits.put("BA", 3.50); letterCredits.put("BB", 3.00); letterCredits.put("CB", 2.50); letterCredits.put("CC", 2.00); letterCredits.put("DC", 1.50); letterCredits.put("DD", 1.00); letterCredits.put("FD", 0.50); letterCredits.put("FF", 0.00);

            // filter courses with Midterm1 due to simplify operations such as division
            courses = courses.stream().filter(s -> s.getExamType().equals("Midterm1")).collect(Collectors.toList());

            // group courses with year and course code
            Map<Integer, Map<Integer, List<Course>>> course = courses.stream().collect(groupingBy(Course::getYear, groupingBy(Course::getCourseCode)));
            course = course.entrySet().stream().sorted(Map.Entry.comparingByKey()).collect(toMap(Map.Entry::getKey, e -> e.getValue().entrySet().stream().sorted(Map.Entry.comparingByKey()).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e2, LinkedHashMap::new)), (e1, e2) -> e2, LinkedHashMap::new));

            // print the transcript
            course.forEach((key, value) -> {
                System.out.println(key);
                value.forEach((key1, value1) -> System.out.println(key1 + " " + letterGrades.get((int) Math.round(getGrade(studentID, key1, key))/ 5 * 5)));
            });

            // Map for calculating cgpa
            Map<Integer, Course> coursera = courses.stream().collect(toMap(Course::getCourseCode, Function.identity(), BinaryOperator.maxBy(Comparator.comparing(Course::getYear))));

            // total weighted credit which student has
            double a = coursera.values().stream().mapToDouble(v -> letterCredits.get(letterGrades.get((int) Math.round(getGrade(studentID, v.getCourseCode(), v.getYear())) / 5 * 5))*v.getCredit()).sum();

            // total credits which the courses have
            double b = coursera.values().stream().mapToDouble(Course::getCredit).sum();

            // print cgpa
            System.out.printf(Locale.US, "CGPA: %.2f%n", a/b);
        }
    }

    public void findCourse(int courseCode){
        // get all courses information from studentList
        List<List<Course>> courses = studentList.stream().map(Student::getTakenCourses).collect(Collectors.toList());

        List<Course> courses1 = new ArrayList<>();

        // put all courses information in a List
        courses.forEach(courses1::addAll);

        // I retrieved only Midterm1 information due to not divide result by 3
        List<Course> courses2 = courses1.stream().filter(s -> s.getExamType().equals("Midterm1")).collect(Collectors.toList());

        // Filter courses by given Course Code
        List<Course> courses3 = courses2.stream().filter(s -> s.getCourseCode() == courseCode).collect(Collectors.toList());

        // Sort courses with year
        List<Course> courses4 = courses3.stream().sorted(Comparator.comparingInt(Course::getYear)).collect(Collectors.toList());

        // Group courses with with year and count them
        Map<Integer, Long> map = courses4.stream().collect(groupingBy(Course::getYear, counting()));

        // Print information with comparing by key, which is year information
        map.entrySet().stream().sorted(Map.Entry.comparingByKey()).forEach(course -> System.out.println(course.getKey() + " " + course.getValue()));
    }

    public void createHistogram(int courseCode, int year){
        List<Double> grades = new ArrayList<>();
        studentList.forEach(k -> {
            // filter the courses which student have taken
            List<Course> courses = k.getTakenCourses().stream().filter(p -> p.getCourseCode() == courseCode && p.getYear() == year).collect(Collectors.toList());

            // if student took the course, which is courseCode and year corresponds calculate the grade
            if(courses.size() != 0){
                grades.add(getGrade(k.getStudentID(), courseCode, year));
            }
        });
        // Initial map for histogram
        Map<Integer, Long> map1 = new HashMap<>(ofEntries(entry(0, 0L), entry(10, 0L), entry(20, 0L), entry(30, 0L), entry(40, 0L), entry(50, 0L), entry(60, 0L), entry(70, 0L), entry(80, 0L), entry(90, 0L)));

        // Count and group process of grades
        Map<Integer, Long> map = grades.stream().collect(groupingBy(s -> {
            if(s == 100.0) {
                return 90;
            }
            return ((int) (Math.floor(s)/10) * 10);
        }, counting()));

        // Put the second map to first map
        map1.putAll(map);

        // Print map with comparingByKey
        map1.entrySet().stream().sorted(Map.Entry.comparingByKey()).forEach(s -> System.out.println(s.getKey() + "-" + (s.getKey()+10) + " " + (s.getValue())));
    }
}