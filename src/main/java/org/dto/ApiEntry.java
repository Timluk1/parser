package org.dto;

import java.util.Objects;

public class ApiEntry {
    private String name;
    private String description;
    private String auth;
    private boolean https;
    private String cors;
    private String link;
    private String category;

    public ApiEntry() {
    }

    public ApiEntry(String name, String description, String auth, boolean https, String cors, String link, String category) {
        this.name = name;
        this.description = description;
        this.auth = auth;
        this.https = https;
        this.cors = cors;
        this.link = link;
        this.category = category;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getAuth() {
        return auth;
    }

    public void setAuth(String auth) {
        this.auth = auth;
    }

    public boolean isHttps() {
        return https;
    }

    public void setHttps(boolean https) {
        this.https = https;
    }

    public String getCors() {
        return cors;
    }

    public void setCors(String cors) {
        this.cors = cors;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public boolean hasAuth() {
        return auth != null && !auth.equalsIgnoreCase("No") && !auth.isEmpty();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ApiEntry apiEntry = (ApiEntry) o;
        return Objects.equals(name, apiEntry.name) && Objects.equals(link, apiEntry.link);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, link);
    }

    @Override
    public String toString() {
        return "ApiEntry{" +
                "name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", auth='" + auth + '\'' +
                ", https=" + https +
                ", cors='" + cors + '\'' +
                ", category='" + category + '\'' +
                '}';
    }
}

