package com.rookies6.myspringboot4project.exception.advice;

import lombok.Data;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

@Data
public class ErrorObject {
    //에러코드
    private Integer statusCode;
    //에러메시지
    private String message;
    //에러발생시간
    private String timestamp;

    public String getTimestamp() {
        LocalDateTime ldt = LocalDateTime.now();
        return DateTimeFormatter.ofPattern(
                "yyyy-MM-dd HH:mm:ss E a", 
                Locale.KOREA).format(ldt);
    }
}