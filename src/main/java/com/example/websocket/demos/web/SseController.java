package com.example.websocket.demos.web;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@RestController
public class SseController {


    SseEmitter sseEmitter;

    @GetMapping(value = "/sse", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter sse() {
        sseEmitter = new SseEmitter();
        Executors.newScheduledThreadPool(1).schedule(() -> {
            for (int i = 0; i < 10; i++) {
                try {
                    SseEmitter.SseEventBuilder message = SseEmitter.event().id(String.valueOf(i))
                            .data("hello")
                            .comment("m")
                            .name("message");
                    sseEmitter.send(message);
                    Thread.sleep(500);
                } catch (Exception e) {
                    sseEmitter.completeWithError(e);
                }
            }
            try {
                SseEmitter.SseEventBuilder message = SseEmitter.event().id(UUID.randomUUID().toString().substring(0, 6))
                        .data("end")
                        .comment("m")
                        .name("message");
                sseEmitter.send(message);
                sseEmitter.complete();
            } catch (IOException e) {
                sseEmitter.completeWithError(e);
            }
        }, 2000, TimeUnit.MILLISECONDS);
        return sseEmitter;
    }


    private void sensMessage() throws IOException {


        sseEmitter.send("hello");
        sseEmitter.complete();
    }
}
