package com.akapps.puzka;

public class DocumentClass {
    int id, type, label;
    String text, body, preview_text;

    public DocumentClass(int id, int type, int label, String text, String body, String preview_text) {
        this.id = id;
        this.type = type;
        this.label = label;
        this.text = text;
        this.body = body;
        this.preview_text = preview_text;
    }

    public DocumentClass() {}

    public int getId() {
        return id;
    }

    public int getType() {
        return type;
    }

    public int getLabel() {
        return label;
    }

    public String getText() {
        return text;
    }

    public String getBody() {
        return body;
    }

    public String getPreview_text() {
        return preview_text;
    }
}
