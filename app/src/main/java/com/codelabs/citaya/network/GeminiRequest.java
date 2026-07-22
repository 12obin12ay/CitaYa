package com.codelabs.citaya.network;

import java.util.*;

public class GeminiRequest {
    public List<Content> contents;

    public GeminiRequest(String prompt) {
        this.contents = new ArrayList<>();
        Content content = new Content();
        Part part = new Part();
        part.text = prompt;
        content.parts = Collections.singletonList(part);
        this.contents.add(content);
    }

    // Constructor para Texto + Imagen
    public GeminiRequest(String prompt, String base64Image, String mimeType) {
        this.contents = new ArrayList<>();
        Content content = new Content();
        
        Part textPart = new Part();
        textPart.text = prompt;

        Part imagePart = new Part();
        imagePart.inline_data = new InlineData(mimeType, base64Image);

        content.parts = Arrays.asList(textPart, imagePart);
        this.contents.add(content);
    }

    public static class Content {
        public List<Part> parts;
    }

    public static class Part {
        public String text;
        public InlineData inline_data;
    }

    public static class InlineData {
        public String mime_type;
        public String data;

        public InlineData(String mimeType, String data) {
            this.mime_type = mimeType;
            this.data = data;
        }
    }
}
