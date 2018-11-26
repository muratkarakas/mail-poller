package com.example.mail.poller;

import javax.mail.Authenticator;
import javax.mail.PasswordAuthentication;
import javax.mail.internet.MimeMessage;
import javax.mail.search.SearchTerm;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.IntegrationFlows;
import org.springframework.integration.mail.dsl.ImapIdleChannelAdapterSpec;
import org.springframework.integration.mail.dsl.Mail;
import org.springframework.integration.mapping.HeaderMapper;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHandler;
import org.springframework.stereotype.Component;


@Component("imapIdleFlow")
public class ImapIdleFlow  implements ImapInterationFlow {
	
	private Logger logger = LoggerFactory.getLogger(getClass());

	
	@Autowired
	@Qualifier("imapURL")
	private String imapURL;
	
	@Autowired
	@Qualifier("mailHeaderMapper")
	public HeaderMapper<MimeMessage> mailHeaderMapper;
	
	@Autowired
	@Qualifier("searchTerm")
	public SearchTerm defaultSearchTerm;

	
	
	public IntegrationFlow interationFlow(String email,String password){
		ImapIdleChannelAdapterSpec spec = Mail.imapIdleAdapter(imapURL).autoStartup(true)
				.shouldMarkMessagesAsRead(true).searchTermStrategy((a, b) -> defaultSearchTerm)
				.javaMailAuthenticator(new Authenticator() {
					@Override
					protected PasswordAuthentication getPasswordAuthentication() {
						return new PasswordAuthentication(email, password);
					}
				}).shouldReconnectAutomatically(false).headerMapper(mailHeaderMapper);

		return IntegrationFlows.from(spec).handle(new MessageHandler() {

			@Override
			public void handleMessage(Message<?> message){
				logger.info(message.getPayload().toString());

			}
		}).get();
	}
	
	
	
}
