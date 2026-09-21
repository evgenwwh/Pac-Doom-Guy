package com.evgenwwh.pacdoomguy.stats;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Stores records as one line per game in a plain text file:
 * {@code name;level;score;seconds;won;date}. Field separators inside names are replaced.
 */
public final class RecordsRepository {
    private static final String SEPARATOR = ";";

    private final Path file;

    public RecordsRepository(Path file) {
        this.file = file;
    }

    /** Default location: {@code ~/.pac-doom-guy/records.txt}. */
    public static RecordsRepository defaultRepository() {
        return new RecordsRepository(Path.of(System.getProperty("user.home"), ".pac-doom-guy", "records.txt"));
    }

    public synchronized void save(Record record) throws IOException {
        Files.createDirectories(file.getParent());
        String line = String.join(SEPARATOR,
                sanitize(record.playerName()),
                sanitize(record.levelName()),
                Integer.toString(record.score()),
                Integer.toString(record.timeSeconds()),
                Boolean.toString(record.won()),
                record.date().toString()) + System.lineSeparator();
        Files.writeString(file, line, StandardCharsets.UTF_8,
                java.nio.file.StandardOpenOption.CREATE, java.nio.file.StandardOpenOption.APPEND);
    }

    /** All records, best score first (ties: shorter time first). Malformed lines are skipped. */
    public synchronized List<Record> loadAll() {
        List<Record> records = new ArrayList<>();
        if (!Files.exists(file)) {
            return records;
        }
        try {
            for (String line : Files.readAllLines(file, StandardCharsets.UTF_8)) {
                Record r = parse(line);
                if (r != null) {
                    records.add(r);
                }
            }
        } catch (IOException e) {
            System.err.println("Could not read records: " + e.getMessage());
        }
        records.sort(Comparator.comparingInt(Record::score).reversed().thenComparingInt(Record::timeSeconds));
        return records;
    }

    public List<Record> top(int n) {
        List<Record> all = loadAll();
        return all.subList(0, Math.min(n, all.size()));
    }

    private static Record parse(String line) {
        String[] parts = line.split(SEPARATOR, -1);
        if (parts.length != 6) {
            return null;
        }
        try {
            return new Record(parts[0], parts[1], Integer.parseInt(parts[2]), Integer.parseInt(parts[3]),
                    Boolean.parseBoolean(parts[4]), LocalDate.parse(parts[5]));
        } catch (RuntimeException e) {
            return null;
        }
    }

    private static String sanitize(String s) {
        String trimmed = s == null ? "" : s.trim().replace(SEPARATOR, ",").replace("\n", " ");
        return trimmed.isEmpty() ? "Anonymous" : trimmed;
    }
}
