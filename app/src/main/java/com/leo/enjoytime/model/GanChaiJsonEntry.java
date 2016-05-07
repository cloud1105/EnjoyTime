package com.leo.enjoytime.model;

/**
 * Created by leo on 16/5/2.
 */
public class GanChaiJsonEntry extends JsonEntry{
    private String message;
    private String status;
    private GanChaiJsonDataEntry data;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public GanChaiJsonDataEntry getData() {
        return data;
    }

    public void setData(GanChaiJsonDataEntry data) {
        this.data = data;
    }
}
