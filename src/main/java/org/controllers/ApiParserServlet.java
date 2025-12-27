package org.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import org.dto.ApiEntry;
import org.services.MarkdownParser;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

import java.io.File;

public class ApiParserServlet extends HttpServlet {
    private static final Logger LOGGER = Logger.getLogger(ApiParserServlet.class.getName());
    private MarkdownParser parser;
    private Configuration freemarkerConfig;
    private ObjectMapper objectMapper;

    @Override
    public void init() throws ServletException {
        super.init();

        String markdownPath = findProjectsMarkdownFile();

        if (markdownPath == null) {
            String error = "Файл projects.md не найден! Проверьте, что файл находится в корне проекта.";
            LOGGER.log(Level.SEVERE, error);
            throw new ServletException(error);
        }

        LOGGER.log(Level.INFO, "Найден markdown файл: " + markdownPath);
        parser = new MarkdownParser(markdownPath);

        freemarkerConfig = new Configuration(Configuration.VERSION_2_3_31);
        try {
            freemarkerConfig.setServletContextForTemplateLoading(getServletContext(), "/WEB-INF/templates");
            freemarkerConfig.setDefaultEncoding("UTF-8");
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Ошибка инициализации FreeMarker", e);
            throw new ServletException("Не удалось инициализировать FreeMarker", e);
        }

        objectMapper = new ObjectMapper();

        LOGGER.log(Level.INFO, "ApiParserServlet инициализирован успешно");
    }

    private String findProjectsMarkdownFile() {
        String contextPath = getServletContext().getRealPath("/");
        LOGGER.log(Level.INFO, "Context path: " + contextPath);

        String path;
        File contextFile = new File(contextPath);
        path = new File(contextFile, "WEB-INF/projects.md").getAbsolutePath();

        if (new File(path).exists()) {
            return path;
        }

        LOGGER.log(Level.SEVERE, "Файл projects.md не найден ни в одном из путей!");
        return null;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String action = req.getParameter("action");

        if ("getApis".equals(action)) {
            handleGetApis(req, resp);
        } else if ("getCategories".equals(action)) {
            handleGetCategories(req, resp);
        } else if ("getCategoryCounts".equals(action)) {
            handleGetCategoryCounts(req, resp);
        } else if ("randomApi".equals(action)) {
            handleRandomApi(req, resp);
        } else {
            handlePageView(req, resp);
        }
    }

    private void handleGetApis(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String category = req.getParameter("category");
        String onlyHttpsParam = req.getParameter("onlyHttps");
        String noAuthParam = req.getParameter("noAuth");
        String withCorsParam = req.getParameter("withCors");
        String searchQuery = req.getParameter("search");
        String sortBy = req.getParameter("sort");

        Boolean onlyHttps = "true".equals(onlyHttpsParam);
        Boolean noAuth = "true".equals(noAuthParam);
        Boolean withCors = "true".equals(withCorsParam);

        try {
            List<ApiEntry> allApis = parser.parseApis();

            List<ApiEntry> filteredApis = parser.filterApis(allApis, category, onlyHttps, noAuth, withCors, searchQuery);

            if ("name".equals(sortBy)) {
                filteredApis.sort(Comparator.comparing(ApiEntry::getName, String.CASE_INSENSITIVE_ORDER));
            }

            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            LOGGER.log(Level.INFO, String.format(
                "[%s] Фильтрация: категория='%s', только HTTPS=%s, без Auth=%s, с CORS=%s, поиск='%s', найдено API=%d",
                timestamp,
                category != null ? category : "All",
                onlyHttps,
                noAuth,
                withCors,
                searchQuery != null ? searchQuery : "",
                filteredApis.size()
            ));

            resp.setContentType("application/json");
            resp.setCharacterEncoding("UTF-8");
            objectMapper.writeValue(resp.getWriter(), filteredApis);

        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Ошибка при получении списка API", e);
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write("{\"error\": \"Ошибка при загрузке данных\"}");
        }
    }

    private void handleGetCategories(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            List<String> categories = parser.getCategories();
            categories.sort(String.CASE_INSENSITIVE_ORDER);

            resp.setContentType("application/json");
            resp.setCharacterEncoding("UTF-8");
            objectMapper.writeValue(resp.getWriter(), categories);

        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Ошибка при получении категорий", e);
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    private void handleGetCategoryCounts(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            Map<String, Integer> counts = parser.getCategoryCounts();

            resp.setContentType("application/json");
            resp.setCharacterEncoding("UTF-8");
            objectMapper.writeValue(resp.getWriter(), counts);

        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Ошибка при получении счетчиков категорий", e);
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    private void handleRandomApi(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            List<ApiEntry> allApis = parser.parseApis();
            if (!allApis.isEmpty()) {
                Random random = new Random();
                ApiEntry randomApi = allApis.get(random.nextInt(allApis.size()));

                LOGGER.log(Level.INFO, "Выбран случайный API: " + randomApi.getName());

                resp.sendRedirect(randomApi.getLink());
            } else {
                resp.sendError(HttpServletResponse.SC_NOT_FOUND, "API не найдены");
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Ошибка при выборе случайного API", e);
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    private void handlePageView(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            List<String> categories = parser.getCategories();
            categories.sort(String.CASE_INSENSITIVE_ORDER);

            Map<String, Integer> categoryCounts = parser.getCategoryCounts();

            Map<String, Object> dataModel = new HashMap<>();
            dataModel.put("categories", categories);
            dataModel.put("categoryCounts", categoryCounts);

            LOGGER.log(Level.INFO, "Подготовка данных для шаблона: категорий=" + categories.size() + ", счетчиков=" + categoryCounts.size());

            Template template = freemarkerConfig.getTemplate("api-list.ftl");
            resp.setContentType("text/html; charset=UTF-8");
            resp.setCharacterEncoding("UTF-8");

            PrintWriter writer = resp.getWriter();
            template.process(dataModel, writer);

            LOGGER.log(Level.INFO, "Шаблон успешно отрендерен");

        } catch (TemplateException e) {
            LOGGER.log(Level.SEVERE, "Ошибка рендеринга FreeMarker шаблона: " + e.getMessage(), e);
            resp.setContentType("text/html; charset=UTF-8");
            resp.getWriter().write("<html><body><h1>Ошибка шаблона</h1><pre>" + e.getMessage() + "</pre></body></html>");
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Ошибка при отображении страницы: " + e.getMessage(), e);
            throw new IOException("Ошибка при загрузке страницы", e);
        }
    }
}
