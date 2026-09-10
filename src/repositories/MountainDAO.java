package repositories;

import core.entities.Mountain;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MountainDAO {

    private static final String FILE_NAME = "MountainList.csv";

    private List<Mountain> mountains;

    public MountainDAO() {
        this.mountains = new ArrayList<Mountain>();
    }

    public void loadMountains() {
        mountains.clear();
        File file = new File(FILE_NAME);
        if (!file.exists()) {
            return;
        }

        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(file));
            String line;
            boolean firstLine = true;
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) {
                    continue;
                }
                if (firstLine && isHeaderLine(line)) {
                    firstLine = false;
                    continue;
                }
                firstLine = false;
                List<String> columns = parseCsvLine(line);
                if (columns.size() < 4) {
                    continue;
                }
                String code = normalizeMountainCode(columns.get(0));
                String mountain = columns.get(1).trim();
                String province = columns.get(2).trim();
                String description = joinDescriptionColumns(columns, 3);
                if (!code.isEmpty()) {
                    mountains.add(new Mountain(code, mountain, province, description));
                }
            }
        } catch (IOException ex) {
            mountains.clear();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException ex) {
                }
            }
        }
    }

    public List<Mountain> getAll() {
        return new ArrayList<Mountain>(mountains);
    }

    public Mountain getMountainByCode(String mountainCode) {
        String normalizedCode = normalizeMountainCode(mountainCode);
        for (int i = 0; i < mountains.size(); i++) {
            Mountain mountain = mountains.get(i);
            if (mountain.getMountainCode().equalsIgnoreCase(normalizedCode)) {
                return mountain;
            }
        }
        return null;
    }

    public boolean isValidMountainCode(String mountainCode) {
        return getMountainByCode(mountainCode) != null;
    }

    public void displayMountainList() {
        System.out.println("\nAvailable Mountains:");
        for (Mountain mountain : mountains) {
            System.out.printf("%-10s | %s\n",
                    mountain.getMountainCode(),
                    mountain.getMountain());
        }
        System.out.println();
    }

    public static String normalizeMountainCode(String mountainCode) {
        if (mountainCode == null) {
            return "";
        }
        String code = mountainCode.trim().toUpperCase();
        if (code.isEmpty()) {
            return "";
        }
        if (code.startsWith("MT")) {
            String numberPart = code.substring(2);
            if (isDigits(numberPart)) {
                return formatMountainCode(numberPart);
            }
            return code;
        }
        if (isDigits(code)) {
            return formatMountainCode(code);
        }
        return code;
    }

    private List<String> parseCsvLine(String line) {
        List<String> result = new ArrayList<String>();
        StringBuilder current = new StringBuilder();
        boolean inQuotes = false;
        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);
            if (c == '"') {
                inQuotes = !inQuotes;
            } else if (c == ',' && !inQuotes) {
                result.add(current.toString().trim());
                current.setLength(0);
            } else {
                current.append(c);
            }
        }
        result.add(current.toString().trim());
        return result;
    }

    private boolean isHeaderLine(String line) {
        String lower = line.toLowerCase();
        return lower.contains("code") && lower.contains("mountain") && lower.contains("province");
    }

    private String joinDescriptionColumns(List<String> columns, int startIndex) {
        StringBuilder description = new StringBuilder();
        for (int i = startIndex; i < columns.size(); i++) {
            if (i > startIndex) {
                description.append(", ");
            }
            description.append(columns.get(i).trim());
        }
        return description.toString();
    }

    private static boolean isDigits(String value) {
        return value != null && value.matches("\\d+");
    }

    private static String formatMountainCode(String numberText) {
        int number = Integer.parseInt(numberText);
        return String.format("MT%02d", number);
    }
}
