package com.pandey.notifications;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;


@Slf4j
@Controller
public class MessageController {

    @Autowired
    SimpMessagingTemplate simpMessagingTemplate;

    // Mapped as /app/application
    @MessageMapping("/application")
    @SendTo("/all/messages")
    public Message send(final Message message) throws Exception {
        return message;
    }

    @MessageMapping("/topic")
    public Message sendTopic(final Message message) throws Exception {
        return message;
    }

    // Mapped as /app/private
    @MessageMapping("/private")
    public void sendToSpecificUser(@Payload Message message) {
        simpMessagingTemplate.convertAndSendToUser(message.getTo(), "/specific", message);
    }

    @GetMapping("/topic/{requestId}")
    public String receiveFromTemplate(@PathVariable String requestId, Model model) {
        model.addAttribute("requestId", requestId);
        return "message-template";
    }


    @RestController
    class TopicController {
        @GetMapping("/send/{requestId}/{message}")
        public String sendToTopic(@PathVariable String requestId, @PathVariable String message) {
            Message msg = new Message();
            msg.setText(message);
            msg.setTo(requestId);

            simpMessagingTemplate.convertAndSend("/topic/"+requestId, msg);
            log.info(" message send successful for {}", requestId);
            return "message send to topic " + requestId;
        }

        @GetMapping("/sendAll/{message}")
        public String sendToAllTopic( @PathVariable String message) {
            Message msg = new Message();
            msg.setText(message);
            msg.setTo("all");

            simpMessagingTemplate.convertAndSend("/all/messages", msg);
            log.info(" message send successful for {}", "all");
            return "message send to all ";
        }
    }
}
