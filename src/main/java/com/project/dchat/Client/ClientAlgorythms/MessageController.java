package com.project.dchat.Client.ClientAlgorythms;

import com.project.dchat.Entities.Code;
import com.project.dchat.Entities.Message;
import com.project.dchat.Entities.User;

public class MessageController {
    public static Message generateMessage(String text, User sender) {
        Message message;
        if(text.contains("<code>") && text.contains("</code>")) {
            message = new Message(new Code(text), sender);
        } else {
            message = new Message(text, sender);
        }
        return message;
    }
}
