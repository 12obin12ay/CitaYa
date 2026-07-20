package com.codelabs.citaya.network;

import java.util.*;

public class GeminiRequest {

    public List<Content> contents;

    public GeminiRequest(String prompt){

        contents = new ArrayList<>();

        Content content = new Content();

        Part part = new Part();
        part.text = prompt;

        content.parts = Arrays.asList(part);

        contents.add(content);
    }

    static class Content {

        List<Part> parts;
    }

    static class Part {

        String text;
    }
}
