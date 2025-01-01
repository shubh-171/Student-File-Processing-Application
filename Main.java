import java.io.*;
import java.util.*;
import java.util.regex.*;

class Student {
    private String id;
    private String name;
    private String course;
    private double finalGrade;

    public Student(String id, String name, String course, double finalGrade) {
        this.id = id;
        this.name = name;
        this.course = course;
        this.finalGrade = finalGrade;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCourse() {
        return course;
    }

    public void setCourse(String course) {
        this.course = course;
    }

    public double getFinalGrade() {
        return finalGrade;
    }

    public void setFinalGrade(double finalGrade) {
        this.finalGrade = finalGrade;
    }

    public double calculateFinalGrade(double t1, double t2, double t3, double finalExam) {
        return (t1 * 0.2) + (t2 * 0.2) + (t3 * 0.2) + (finalExam * 0.4);
    }

    public static Comparator<Student> compareById() {
        return Comparator.comparing(Student::getId);
    }
}

class FileHandler {
    public static List<String> readLines(String filePath) throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            List<String> lines = new ArrayList<>();
            String line;
            while ((line = reader.readLine()) != null) {
                lines.add(line);
            }
            return lines;
        }
    }

    public static void writeLines(String filePath, List<String> lines) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            for (String line : lines) {
                writer.write(line);
                writer.newLine();
            }
        }
    }
}

class Validator {
    public static void validateStudentId(String id) {
        if (!id.matches("\\d{9}")) {
            throw new IllegalArgumentException("Invalid Student ID: " + id);
        }
    }

    public static void validateCourseCode(String course) {
        if (!course.matches("[A-Z]{2}\\d{3}")) {
            throw new IllegalArgumentException("Invalid Course Code: " + course);
        }
    }

    public static void validateGrade(String grade) {
        if (!grade.matches("\\d{1,2}\\.\\d")) {
            throw new IllegalArgumentException("Invalid Grade: " + grade);
        }
    }

    public static void validateStudentName(String name) {
        if (!name.matches("^[A-Za-z ]+$")) {
            throw new IllegalArgumentException("Invalid Student Name: " + name);
        }
    }
}

public class Main {

    public static String trim(String str) {
        return str == null ? "" : str.trim();
    }

    public static void main(String[] args) {
        Map<String, String> studentNames = new HashMap<>();
        List<Student> students = new ArrayList<>();

        try {
            // Read NameFile.txt
            List<String> nameLines = FileHandler.readLines("NameFile.txt");
            for (String line : nameLines) {
                String[] parts = line.split(",");
                if (parts.length < 2) continue;

                String id = trim(parts[0]);
                String name = trim(parts[1]);

                Validator.validateStudentId(id);
                Validator.validateStudentName(name);

                studentNames.put(id, name);
            }

            System.out.println("Successfully loaded student names.");

            // Read CourseFile.txt
            List<String> courseLines = FileHandler.readLines("CourseFile.txt");
            for (String line : courseLines) {
                try {
                    String[] parts = line.split(",");
                    if (parts.length < 6) continue;

                    String id = trim(parts[0]);
                    String course = trim(parts[1]);
                    double t1 = Double.parseDouble(trim(parts[2]));
                    double t2 = Double.parseDouble(trim(parts[3]));
                    double t3 = Double.parseDouble(trim(parts[4]));
                    double finalExam = Double.parseDouble(trim(parts[5]));

                    Validator.validateStudentId(id);
                    Validator.validateCourseCode(course);

                    if (!studentNames.containsKey(id)) {
                        System.err.println("Student ID not found in NameFile: " + id);
                        continue;
                    }

                    String name = studentNames.get(id);
                    Student student = new Student(id, name, course, 0.0);
                    double finalGrade = student.calculateFinalGrade(t1, t2, t3, finalExam);
                    student.setFinalGrade(finalGrade);
                    students.add(student);

                } catch (Exception e) {
                    System.err.println("Error processing line: " + line + " -> " + e.getMessage());
                }
            }

            if (students.isEmpty()) {
                System.err.println("No valid students found. Check input files and validations.");
                return;
            }

            // Sort students by ID
            students.sort(Student.compareById());

            // Write to OutputFile.txt
            List<String> outputLines = new ArrayList<>();
            for (Student student : students) {
                outputLines.add(String.format("%s, %s, %s, %.1f", student.getId(), student.getName(), student.getCourse(), student.getFinalGrade()));
            }
            FileHandler.writeLines("OutputFile.txt", outputLines);

            System.out.println("Processing complete. Check OutputFile.txt for results.");

        } catch (IOException e) {
            System.err.println("File error: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Unexpected error: " + e.getMessage());
        }
    }
}
