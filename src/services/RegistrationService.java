package services;

import core.entities.StatisticalInfo;
import core.entities.Student;
import repositories.MountainDAO;
import repositories.StudentDAO;
import helpers.Validator;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class RegistrationService {

    public static final double DEFAULT_TUITION_FEE = 10000000;
    public static final double DISCOUNT_RATE = 0.35;

    private StudentDAO studentDAO;
    private MountainDAO mountainDAO;
    private String lastMessage;

    public RegistrationService() {
        this(new StudentDAO(), new MountainDAO());
    }

    public RegistrationService(StudentDAO studentDAO, MountainDAO mountainDAO) {
        this.studentDAO = studentDAO;
        this.mountainDAO = mountainDAO;
        this.lastMessage = "";
    }

    public MountainDAO getMountainDAO() {
        return mountainDAO;
    }

    public void loadData() {
        studentDAO.loadStudents();
        mountainDAO.loadMountains();
        lastMessage = "Data loaded successfully.";
    }

    public double calculateTuitionFee(String phone) {
        if (Validator.isDiscountPhone(phone)) {
            return DEFAULT_TUITION_FEE * (1 - DISCOUNT_RATE);
        }
        return DEFAULT_TUITION_FEE;
    }

    public boolean createStudent(String id, String name, String phone, String email,
            String mountainCode) {
        String normalizedId = Validator.normalizeStudentId(id);
        String normalizedMountainCode = MountainDAO.normalizeMountainCode(mountainCode);

        if (!Validator.isValidStudentId(normalizedId)) {
            lastMessage = "Student ID is invalid.";
            return false;
        }
        if (!Validator.isValidStudentName(name)) {
            lastMessage = "Name must contain only letters and each word must start with a capital letter.";
            return false;
        }
        if (!Validator.isValidPhone(phone)) {
            lastMessage = "Phone number must contain exactly 10 digits.";
            return false;
        }
        if (!Validator.isValidEmail(email)) {
            lastMessage = "Email is invalid.";
            return false;
        }
        if (!Validator.isValidMountainCodeInput(normalizedMountainCode)
                || !mountainDAO.isValidMountainCode(normalizedMountainCode)) {
            lastMessage = "Mountain code does not exist.";
            return false;
        }
        if (studentDAO.isDuplicatedId(normalizedId)) {
            lastMessage = "Student ID already exists.";
            return false;
        }

        Student student = new Student(normalizedId, name.trim(), phone.trim(), email.trim(),
                normalizedMountainCode, calculateTuitionFee(phone));
        boolean added = studentDAO.add(student);
        lastMessage = added ? "New registration added successfully." : "Cannot add registration.";
        return added;
    }

    public boolean updateStudent(String id, String newName, String newPhone,
            String newEmail, String newMountainCode) {
        String normalizedId = Validator.normalizeStudentId(id);
        Student current = studentDAO.getStudentById(normalizedId);
        if (current == null) {
            lastMessage = "This student has not registered yet.";
            return false;
        }

        String finalName = chooseNewValue(newName, current.getName());
        String finalPhone = chooseNewValue(newPhone, current.getPhone());
        String finalEmail = chooseNewValue(newEmail, current.getEmail());
        String finalMountainCode = chooseNewValue(newMountainCode, current.getMountainCode());
        finalMountainCode = MountainDAO.normalizeMountainCode(finalMountainCode);

        if (!Validator.isValidStudentName(finalName)) {
            lastMessage = "Name must contain only letters and each word must start with a capital letter.";
            return false;
        }
        if (!Validator.isValidPhone(finalPhone)) {
            lastMessage = "Phone number must contain exactly 10 digits.";
            return false;
        }
        if (!Validator.isValidEmail(finalEmail)) {
            lastMessage = "Email is invalid.";
            return false;
        }
        if (!mountainDAO.isValidMountainCode(finalMountainCode)) {
            lastMessage = "Mountain code does not exist.";
            return false;
        }

        Student updated = new Student(current.getStudentId(), finalName.trim(), finalPhone.trim(),
                finalEmail.trim(), finalMountainCode, calculateTuitionFee(finalPhone));
        boolean updatedResult = studentDAO.update(updated);
        lastMessage = updatedResult ? "Registration updated successfully." : "Cannot update registration.";
        return updatedResult;
    }

    public boolean deleteStudent(String id) {
        boolean deleted = studentDAO.deleteById(Validator.normalizeStudentId(id));
        lastMessage = deleted ? "Registration deleted successfully." : "This student has not registered yet.";
        return deleted;
    }

    public List<Student> getAllStudents() {
        return studentDAO.getAll();
    }

    public List<Student> searchByName(String keyword) {
        return studentDAO.searchByName(keyword);
    }

    public List<Student> filterByCampus(String campusCode) {
        return studentDAO.filterByCampus(campusCode);
    }

    public List<StatisticalInfo> getStatisticsByMountain() {
        Map<String, StatisticalInfo> statisticsMap = new LinkedHashMap<String, StatisticalInfo>();
        List<Student> students = studentDAO.getAll();
        for (int i = 0; i < students.size(); i++) {
            Student student = students.get(i);
            String code = student.getMountainCode();
            StatisticalInfo info = statisticsMap.get(code);
            if (info == null) {
                info = new StatisticalInfo(code, 0, 0);
                statisticsMap.put(code, info);
            }
            info.addStudent(student.getTuitionFee());
        }
        return new ArrayList<StatisticalInfo>(statisticsMap.values());
    }

    public boolean saveData() {
        boolean saved = studentDAO.saveStudents();
        lastMessage = saved ? "Registration data has been successfully saved to registrations.dat."
                : "Cannot save registration data.";
        return saved;
    }

    public boolean isSaved() {
        return studentDAO.isSaved();
    }

    public boolean isEmpty() {
        return studentDAO.isEmpty();
    }

    public Student getStudentById(String id) {
        return studentDAO.getStudentById(Validator.normalizeStudentId(id));
    }

    public String getLastMessage() {
        return lastMessage;
    }

    private String chooseNewValue(String newValue, String oldValue) {
        if (newValue == null || newValue.trim().isEmpty()) {
            return oldValue;
        }
        return newValue;
    }
}
