package com.example.letmecook.api;

import java.util.List;

/**
 * Kelas data (POJO) untuk mem-parsing respons JSON dari Gemini API.
 * Strukturnya disesuaikan dengan output dari model gemini-pro.
 */
public class GeminiResponse {
    public List<Candidate> candidates;

    public static class Candidate {
        public Content content;
    }

    public static class Content {
        public List<Part> parts;
        public String role;
    }

    public static class Part {
        public String text;
    }
}