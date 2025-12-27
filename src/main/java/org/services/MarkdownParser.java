package org.services;

import org.dto.ApiEntry;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MarkdownParser {
    private static final Logger LOGGER = Logger.getLogger(MarkdownParser.class.getName());

    // Regex pattern для парсинга строк таблицы API
    // Формат: | [Name](link) | Description | Auth | HTTPS | CORS |
    private static final Pattern API_PATTERN = Pattern.compile(
        "\\|\\s*\\[([^\\]]+)\\]\\(([^)]+)\\)\\s*\\|\\s*([^|]+)\\s*\\|\\s*([^|]+)\\s*\\|\\s*([^|]+)\\s*\\|\\s*([^|]+)\\s*\\|"
    );

    private static final Pattern CATEGORY_PATTERN = Pattern.compile("^###\\s+(.+)$");

    private final String filePath;

    public MarkdownParser(String filePath) {
        this.filePath = filePath;
    }

    public List<ApiEntry> parseApis() throws IOException {
        List<ApiEntry> apis = new ArrayList<>();
        String currentCategory = "Unknown";

        File file = new File(filePath);
        if (!file.exists()) {
            LOGGER.log(Level.SEVERE, "Файл не найден: " + filePath);
            throw new IOException("Файл не найден: " + filePath);
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            boolean inApiSection = false;

            while ((line = reader.readLine()) != null) {
                line = line.trim();

                Matcher categoryMatcher = CATEGORY_PATTERN.matcher(line);
                if (categoryMatcher.matches()) {
                    currentCategory = categoryMatcher.group(1).trim();
                    inApiSection = true;
                    LOGGER.log(Level.INFO, "Найдена категория: " + currentCategory);
                    continue;
                }

                if (line.startsWith("|:---") || line.startsWith("| API |") ||
                    line.startsWith("API | Description")) {
                    continue;
                }

                if (inApiSection && line.startsWith("|")) {
                    Matcher apiMatcher = API_PATTERN.matcher(line);
                    if (apiMatcher.find()) {
                        try {
                            String name = apiMatcher.group(1).trim();
                            String link = apiMatcher.group(2).trim();
                            String description = apiMatcher.group(3).trim();
                            String auth = apiMatcher.group(4).trim();
                            String httpsStr = apiMatcher.group(5).trim();
                            String cors = apiMatcher.group(6).trim();

                            boolean https = httpsStr.equalsIgnoreCase("Yes");

                            ApiEntry api = new ApiEntry(name, description, auth, https, cors, link, currentCategory);
                            apis.add(api);

                        } catch (Exception e) {
                            LOGGER.log(Level.WARNING, "Ошибка парсинга строки: " + line, e);
                        }
                    }
                }

                if (line.contains("Back to Index")) {
                    inApiSection = false;
                }
            }
        }

        LOGGER.log(Level.INFO, "Всего распарсено API: " + apis.size());
        return apis;
    }

    public Map<String, List<ApiEntry>> parseApisByCategory() throws IOException {
        List<ApiEntry> allApis = parseApis();
        Map<String, List<ApiEntry>> categorizedApis = new HashMap<>();

        for (ApiEntry api : allApis) {
            categorizedApis
                .computeIfAbsent(api.getCategory(), k -> new ArrayList<>())
                .add(api);
        }

        return categorizedApis;
    }

    public List<String> getCategories() throws IOException {
        Map<String, List<ApiEntry>> categorized = parseApisByCategory();
        return new ArrayList<>(categorized.keySet());
    }

    public List<ApiEntry> filterApis(List<ApiEntry> apis, String category,
                                      Boolean onlyHttps, Boolean noAuth,
                                      Boolean withCors, String searchQuery) {
        List<ApiEntry> filtered = new ArrayList<>();

        for (ApiEntry api : apis) {
            if (category != null && !category.isEmpty() && !category.equals("All")
                && !api.getCategory().equalsIgnoreCase(category)) {
                continue;
            }

            if (onlyHttps != null && onlyHttps && !api.isHttps()) {
                continue;
            }

            if (noAuth != null && noAuth && api.hasAuth()) {
                continue;
            }

            if (withCors != null && withCors &&
                (api.getCors() == null || !api.getCors().equalsIgnoreCase("Yes"))) {
                continue;
            }

            if (searchQuery != null && !searchQuery.trim().isEmpty()) {
                String query = searchQuery.toLowerCase();
                boolean matchesSearch =
                    (api.getName() != null && api.getName().toLowerCase().contains(query)) ||
                    (api.getDescription() != null && api.getDescription().toLowerCase().contains(query));
                if (!matchesSearch) {
                    continue;
                }
            }

            filtered.add(api);
        }

        return filtered;
    }

    public Map<String, Integer> getCategoryCounts() throws IOException {
        Map<String, List<ApiEntry>> categorized = parseApisByCategory();
        Map<String, Integer> counts = new HashMap<>();

        for (Map.Entry<String, List<ApiEntry>> entry : categorized.entrySet()) {
            counts.put(entry.getKey(), entry.getValue().size());
        }

        return counts;
    }
}

