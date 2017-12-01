package com.rzt.entity;

/**
 * Created by lzz on 2017/11/10.
 */
public class Questions {
    private String textId;
    private String textBody;
    private String textPoints;
    private String optionId;
    private String options;
    private String anwers;
    private Object obj;

    public String getTextId() {
        return textId;
    }

    public void setTextId(String textId) {
        this.textId = textId;
    }

    public String getTextBody() {
        return textBody;
    }

    public void setTextBody(String textBody) {
        this.textBody = textBody;
    }

    public String getTextPoints() {
        return textPoints;
    }

    public void setTextPoints(String textPoints) {
        this.textPoints = textPoints;
    }

    public String getOptionId() {
        return optionId;
    }

    public void setOptionId(String optionId) {
        this.optionId = optionId;
    }

    public String getOptions() {
        return options;
    }

    public void setOptions(String options) {
        this.options = options;
    }

    public String getAnwers() {
        return anwers;
    }

    public void setAnwers(String anwers) {
        this.anwers = anwers;
    }
}
