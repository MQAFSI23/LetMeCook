package com.example.letmecook.api;

import java.util.Collections;
import java.util.List;

/**
 * Kelas data (POJO) untuk merepresentasikan body request yang dikirim ke Gemini API.
 */
public class GeminiRequest {
    private final List<Content> contents;

    public GeminiRequest(String text) {
        Part part = new Part(text);
        Content content = new Content(Collections.singletonList(part));
        this.contents = Collections.singletonList(content);
    }

    static class Content {
        private final List<Part> parts;

        public Content(List<Part> parts) {
            this.parts = parts;
        }
    }

    static class Part {
        private final String text;

        public Part(String text) {
            this.text = text;
        }
    }
}