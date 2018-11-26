
package com.example.mail.simple.poller.alternative;

import javax.mail.Authenticator;
import javax.mail.PasswordAuthentication;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.integration.annotation.InboundChannelAdapter;
import org.springframework.integration.annotation.Poller;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.mail.ImapMailReceiver;
import org.springframework.integration.mail.MailReceiver;
import org.springframework.integration.mail.MailReceivingMessageSource;
@SpringBootApplication
public class GeneralImapAdapter {

	
	public static void main(String[] args) {
		SpringApplication.run(GeneralImapAdapter.class, args);
	}

	private Logger logger = LoggerFactory.getLogger(getClass());


    @Bean
    @InboundChannelAdapter(value = "emailChannel", poller = @Poller(fixedDelay = "10000"))
    public MailReceivingMessageSource mailMessageSource(MailReceiver imapMailReceiver) {
        return new MailReceivingMessageSource(imapMailReceiver);
    }

    @Bean
    @Value("imaps://imap.gmail.com:993/INBOX")
    public MailReceiver imapMailReceiver(String imapUrl) {
        ImapMailReceiver imapMailReceiver = new ImapMailReceiver(imapUrl);
        imapMailReceiver.setShouldMarkMessagesAsRead(true);
        imapMailReceiver.setShouldDeleteMessages(false);
        Authenticator javaMailAuthenticator = new Authenticator() {@Override
        protected PasswordAuthentication getPasswordAuthentication() {
        	return new PasswordAuthentication("defne.ece.karakas", "***");
        }
		};
		imapMailReceiver.setJavaMailAuthenticator(javaMailAuthenticator );
        // other setters here
        return imapMailReceiver;
    }

    @ServiceActivator(inputChannel = "emailChannel")
    public void emailMessageSource(javax.mail.Message message) {
    	logger.info(message.toString());
    }
}
