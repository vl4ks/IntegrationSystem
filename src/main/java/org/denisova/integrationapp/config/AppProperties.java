package org.denisova.integrationapp.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Конфигурация.
 * Источники: application.properties
 */
@ConfigurationProperties(prefix = "app")
public class AppProperties {
    private Integer studentId;
    private Cms cms = new Cms();
    private Report report = new Report();

    public Integer getStudentId() {
        return studentId;
    }

    public void setStudentId(Integer studentId) {
        this.studentId = studentId;
    }

    public Cms getCms() {
        return cms;
    }

    public Report getReport() {
        return report;
    }

    /**
     * Настройки CMS-источника: базовый URL и размер страницы.
     */
    public static class Cms {
        private String baseUrl;
        private Integer pageSize = 10;

        public String getBaseUrl() {
            return baseUrl;
        }

        public void setBaseUrl(String baseUrl) {
            this.baseUrl = baseUrl;
        }

        public Integer getPageSize() {
            return pageSize;
        }

        public void setPageSize(Integer pageSize) {
            this.pageSize = pageSize;
        }
    }

    /**
     * Настройки Report API.
     */
    public static class Report {
        private String baseUrl;

        public String getBaseUrl() {
            return baseUrl;
        }

        public void setBaseUrl(String baseUrl) {
            this.baseUrl = baseUrl;
        }
    }
}
