package repository;

import model.common.CsvEntity;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;
import java.util.function.Function;

/**
 * AbstractCsvRepository: Lưu trữ T (CsvEntity) ra file CSV.
 * Cần cung cấp:
 * - csvHeader: mảng header cố định cho T
 * - idGetter: lấy id duy nhất của entity
 * - lineParser: parse 1 dòng CSV -> T
 */
public abstract class AbstractCsvRepository<T extends CsvEntity> implements Repository<T> {

    private final Path file;
    private final List<T> data = new ArrayList<>();
    private final String[] csvHeader;
    private final Function<T, String> idGetter;
    private final Function<String, T> lineParser;

    protected AbstractCsvRepository(String filePath,
                                    String[] csvHeader,
                                    Function<T, String> idGetter,
                                    Function<String, T> lineParser) {
        this.file = Paths.get(filePath);
        this.csvHeader = csvHeader;
        this.idGetter = idGetter;
        this.lineParser = lineParser;
    }

    @Override
    public List<T> findAll() {
        return Collections.unmodifiableList(data);
    }

    @Override
    public Optional<T> findById(String id) {
        if (id == null || id.trim().isEmpty()) return Optional.empty();
        return data.stream().filter(x -> id.equals(idGetter.apply(x))).findFirst();
    }

    @Override
    public void upsert(T entity) {
        String id = idGetter.apply(entity);
        Optional<T> opt = findById(id);
        if (opt.isPresent()) {
            T existed = opt.get();
            int idx = data.indexOf(existed);
            data.set(idx, entity);
        } else {
            data.add(entity);
        }
    }

    @Override
    public void upsertAll(List<T> entities) {
        if (entities == null) return;
        for (T e : entities) upsert(e);
    }

    @Override
    public boolean deleteById(String id) {
        Optional<T> opt = findById(id);
        opt.ifPresent(data::remove);
        return opt.isPresent();
    }

    @Override
    public void load() throws Exception {
        data.clear();
        if (!Files.exists(file)) return;

        try (BufferedReader br = Files.newBufferedReader(file, StandardCharsets.UTF_8)) {
            String first = br.readLine();
            // Bỏ header nếu trùng
            if (first != null) {
                if (first.startsWith("\ufeff")) {
                    first = first.substring(1);
                }
                String expected = String.join(",", csvHeader);
                if (!first.equals(expected)) {
                    // Dòng đầu không phải header -> parse luôn
                    T t = parseLineSafe(first);
                    if (t != null) data.add(t);
                }
            }
            String line;
            while ((line = br.readLine()) != null) {
                if (line.startsWith("\ufeff")) {
                    line = line.substring(1);
                }
                T t = parseLineSafe(line);
                if (t != null) data.add(t);
            }
        }
    }

    private T parseLineSafe(String line) {
        try {
            return lineParser.apply(line);
        } catch (Exception e) {
            // Log lỗi thay vì bỏ qua im lặng
            System.err.println("⚠️  Không thể parse dòng CSV: " + line);
            System.err.println("    Lỗi: " + e.getMessage());
            return null;
        }
    }

    @Override
    public void persist() throws Exception {
        Files.createDirectories(file.getParent());
        try (BufferedWriter bw = Files.newBufferedWriter(file, StandardCharsets.UTF_8,
                StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING)) {
            // header
            bw.write(String.join(",", csvHeader));
            bw.newLine();
            // rows
            for (T t : data) {
                bw.write(t.toCsvLine());
                bw.newLine();
            }
        }
    }
}