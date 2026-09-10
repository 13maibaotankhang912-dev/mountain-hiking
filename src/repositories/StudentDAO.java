package repositories;

import core.entities.Student;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

public class StudentDAO {

    private static final String FILE_NAME = "registrations.dat";

    private List<Student> students;
    private boolean saved;

    public StudentDAO() {
        this.students = new ArrayList<Student>();
        this.saved = true;
    }

    public void loadStudents() {
        students.clear();
        File file = new File(FILE_NAME);
        if (!file.exists() || file.length() == 0) {
            saved = true;
            return;
        }

        ObjectInputStream input = null;
        try {
            input = new ObjectInputStream(new FileInputStream(file));
            Object data = input.readObject();
            if (data instanceof List<?>) {
                List<?> rawList = (List<?>) data;
                for (int i = 0; i < rawList.size(); i++) {
                    Object item = rawList.get(i);
                    if (item instanceof Student) {
                        students.add((Student) item);
                    }
                }
            }
            saved = true;
        } catch (EOFException ex) {
            students.clear();
            saved = true;
        } catch (IOException ex) {
            students.clear();
            saved = true;
        } catch (ClassNotFoundException ex) {
            students.clear();
            saved = true;
        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (IOException ex) {
                }
            }
        }
    }

    public boolean saveStudents() {
        ObjectOutputStream output = null;
        try {
            output = new ObjectOutputStream(new FileOutputStream(FILE_NAME));
            output.writeObject(students);
            saved = true;
            return true;
        } catch (IOException ex) {
            return false;
        } finally {
            if (output != null) {
                try {
                    output.close();
                } catch (IOException ex) {
                }
            }
        }
    }

    public List<Student> getAll() {
        return new ArrayList<Student>(students);
    }

    public Student getStudentById(String studentId) {
        if (studentId == null) {
            return null;
        }
        String id = studentId.trim();
        for (int i = 0; i < students.size(); i++) {
            Student student = students.get(i);
            if (student.getStudentId() != null
                    && student.getStudentId().equalsIgnoreCase(id)) {
                return student;
            }
        }
        return null;
    }

    public boolean isDuplicatedId(String studentId) {
        return getStudentById(studentId) != null;
    }

    public boolean add(Student student) {
        if (student == null || student.getStudentId() == null
                || isDuplicatedId(student.getStudentId())) {
            return false;
        }
        students.add(student);
        saved = false;
        return true;
    }

    public boolean update(Student updatedStudent) {
        if (updatedStudent == null || updatedStudent.getStudentId() == null) {
            return false;
        }
        for (int i = 0; i < students.size(); i++) {
            Student current = students.get(i);
            if (current.getStudentId().equalsIgnoreCase(updatedStudent.getStudentId())) {
                students.set(i, updatedStudent);
                saved = false;
                return true;
            }
        }
        return false;
    }

    public boolean deleteById(String studentId) {
        Student student = getStudentById(studentId);
        if (student == null) {
            return false;
        }
        students.remove(student);
        saved = false;
        return true;
    }

    public List<Student> searchByName(String keyword) {
        List<Student> result = new ArrayList<Student>();
        if (keyword == null || keyword.trim().isEmpty()) {
            return result;
        }
        String searchKey = keyword.trim().toLowerCase();
        for (int i = 0; i < students.size(); i++) {
            Student student = students.get(i);
            if (student.getName() != null
                    && student.getName().toLowerCase().contains(searchKey)) {
                result.add(student);
            }
        }
        return result;
    }

    public List<Student> filterByCampus(String campusCode) {
        List<Student> result = new ArrayList<Student>();
        if (campusCode == null || campusCode.trim().isEmpty()) {
            return result;
        }
        String campus = campusCode.trim().toUpperCase();
        for (int i = 0; i < students.size(); i++) {
            Student student = students.get(i);
            if (student.getStudentId() != null
                    && student.getStudentId().toUpperCase().startsWith(campus)) {
                result.add(student);
            }
        }
        return result;
    }

    public boolean isEmpty() {
        return students.isEmpty();
    }

    public boolean isSaved() {
        return saved;
    }

    public void setSaved(boolean saved) {
        this.saved = saved;
    }
}
