package com.evgenwwh.pacdoomguy.stats;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class RecordsRepositoryTest {

    @TempDir
    Path dir;

    @Test
    void savesAndLoadsSortedByScoreThenTime() throws IOException {
        RecordsRepository repo = new RecordsRepository(dir.resolve("nested/records.txt"));
        assertTrue(repo.loadAll().isEmpty());

        repo.save(new Record("Bob", "Mars", 50, 120, false, LocalDate.of(2026, 1, 1)));
        repo.save(new Record("Ann", "Hell", 90, 100, true, LocalDate.of(2026, 1, 2)));
        repo.save(new Record("Cid", "Mars", 50, 80, false, LocalDate.of(2026, 1, 3)));

        List<Record> all = repo.loadAll();
        assertEquals(List.of("Ann", "Cid", "Bob"), all.stream().map(Record::playerName).toList());
        assertEquals(2, repo.top(2).size());
        assertEquals("01:40", all.get(0).formattedTime());
    }

    @Test
    void sanitizesNamesAndSkipsGarbage() throws IOException {
        Path file = dir.resolve("records.txt");
        RecordsRepository repo = new RecordsRepository(file);
        repo.save(new Record("  ", "Mars", 1, 1, false, LocalDate.of(2026, 1, 1)));
        repo.save(new Record("a;b", "Mars", 2, 1, false, LocalDate.of(2026, 1, 1)));
        Files.writeString(file, Files.readString(file) + "this is not a record\n");

        List<Record> all = repo.loadAll();
        assertEquals(2, all.size());
        assertEquals("a,b", all.get(0).playerName());
        assertEquals("Anonymous", all.get(1).playerName());
    }
}
