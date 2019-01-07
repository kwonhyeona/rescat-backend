package com.sopt.rescat.dto;

import com.sopt.rescat.domain.Notification;
import lombok.Builder;
import lombok.Data;
import org.json.JSONObject;
import org.springframework.http.HttpEntity;

@Data
@Builder
public class NotificationDto {
    private String title;
    private String body;
    private JSONObject data = new JSONObject();
    private String to;
    private String priority = "normal";

    public NotificationDto addData(String keyName, Object value) {
        data.put(keyName, value);
        return this;
    }

    public HttpEntity<String> toFormalNotification() {
        return new HttpEntity<>(new JSONObject()
                .put("message", new JSONObject()
                        .put("notification", new JSONObject()
                            .put("title", title)
                            .put("body", body))
                        .put("data", data)
                        .put("to", to)
                        .put("priority", priority))
                .toString());
    }
}