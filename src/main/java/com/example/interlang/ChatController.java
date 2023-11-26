package com.example.interlang;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/chat")
public class ChatController {

    private final AssistantService assistantService;

    @Autowired
    public ChatController(AssistantService assistantService) {
        this.assistantService = assistantService;
    }

    @PostMapping
    public String postMessage(@RequestBody String message) {
        return assistantService.chat(message);
    }
}
