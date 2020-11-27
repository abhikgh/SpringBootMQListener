package com.example.SpringBootMQListener.sender;

import java.io.StringReader;
import java.io.StringWriter;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.time.LocalDateTime;
import java.util.Optional;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.example.SpringBootMQListener.model.AppData;
import com.example.SpringBootMQListener.model.Conversion;
import com.example.SpringBootMQListener.model.Other;
import com.example.SpringBootMQListener.utils.SpringBootMQUtils;

import lombok.extern.slf4j.Slf4j;

import static com.example.SpringBootMQListener.utils.SpringBootMQUtils.BLANK;

@Component
@Slf4j
public class MessageSenderService {

	@Value("${ibm.mq.appResponseQueue}")
	private String appResponseQueue;
	
	@Autowired
	private JmsTemplate jmsTemplate;

	public void sendMessage(String responseMessage,String messageId) {
		try {
			log.info("Before sending...");
			jmsTemplate.convertAndSend(appResponseQueue, responseMessage, message -> {
				message.setJMSCorrelationID(messageId);
				return message;
			});
			log.info("Message sent successfully");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public String generateResponse(String appRequestPayload) throws JAXBException, UnknownHostException {
		
		//Unmarshall the XML
		JAXBContext jaxbContext = JAXBContext.newInstance(AppData.class,Conversion.class,Other.class);
		Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
		AppData appData = (AppData) unmarshaller.unmarshal(new StringReader(appRequestPayload));
		
		String celsius = Optional.ofNullable(appData).map(AppData::getConversion).map(Conversion::getCelsius)
				.orElse(BLANK);
		if(!StringUtils.isEmpty(celsius)) {
			appData.getConversion().setFarenheit(SpringBootMQUtils.getFarenheit(celsius)); 
		}
		
		String miles = Optional.ofNullable(appData).map(AppData::getConversion).map(Conversion::getMiles)
				.orElse(BLANK);
		if(!StringUtils.isEmpty(miles)) {
			appData.getConversion().setKilometer(SpringBootMQUtils.getKm(miles)); 
		}
		
		String kilogram = Optional.ofNullable(appData).map(AppData::getConversion).map(Conversion::getKilogram)
				.orElse(BLANK);
		if(!StringUtils.isEmpty(kilogram)) {
			appData.getConversion().setPound(SpringBootMQUtils.getPound(kilogram)); 
		}
		
		appData.getOther().setServiceIP(InetAddress.getLocalHost().getHostAddress());
		appData.getOther().setServiceTime(LocalDateTime.now().toString());
		
		//Marshall the response
		JAXBContext jaxbContext2 = JAXBContext.newInstance(AppData.class);
		Marshaller marshaller = jaxbContext2.createMarshaller();
		marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
		StringWriter stringWriter = new StringWriter();
		marshaller.marshal(appData, stringWriter);
		
		return stringWriter.toString();
		
	}

}
