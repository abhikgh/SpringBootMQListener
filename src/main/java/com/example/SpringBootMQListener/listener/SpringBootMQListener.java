package com.example.SpringBootMQListener.listener;

import java.net.UnknownHostException;
import java.util.Optional;

import javax.xml.bind.JAXBException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.support.JmsMessageHeaderAccessor;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import com.example.SpringBootMQListener.sender.MessageSenderService;

@Component
public class SpringBootMQListener {

	@Autowired
	JmsTemplate jmsTemplate;

	@Autowired
	private MessageSenderService messageSenderService;

	@JmsListener(destination = "${ibm.mq.appRequestQueue}")
	public void getAppRequestPayload(@Payload String appRequestPayload, JmsMessageHeaderAccessor jmsMessageHeaderAccessor) {
		System.out.println("Input received from Request :: ");
		System.out.println(appRequestPayload);
		
		String messageId = Optional.ofNullable(jmsMessageHeaderAccessor)
									.map(JmsMessageHeaderAccessor::getMessageId)
									.orElse("");
		
		String responseMessage = null;
		try {
			responseMessage = messageSenderService.generateResponse(appRequestPayload);
		} catch (JAXBException | UnknownHostException e) {
			e.printStackTrace();
		}
		
		messageSenderService.sendMessage(responseMessage,messageId);
		
	}

}
