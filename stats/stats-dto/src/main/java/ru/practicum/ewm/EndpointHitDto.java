package ru.practicum.ewm;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class EndpointHitDto {
    public static final String DTF = "yyyy-MM-dd HH:mm:ss";
    private String app;
    private String uri;
    private String ip;
    @JsonFormat(pattern = DTF)
    private LocalDateTime timestamp;
}
