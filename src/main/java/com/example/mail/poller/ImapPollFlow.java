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
import org.springframework.integration.dsl.Pollers;
import org.springframework.integration.mail.dsl.Mail;
import org.springframework.integration.mapping.HeaderMapper;
import org.springframework.stereotype.Component;

@Component("imapPollFlow")
public class ImapPollFlow implements ImapInterationFlow {
	
	
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
	

	@Override
	public IntegrationFlow interationFlow(String email,String password){

		return IntegrationFlows
				.from(Mail.imapInboundAdapter(imapURL).shouldMarkMessagesAsRead(true).shouldDeleteMessages(false)
						.searchTermStrategy((a, b) -> defaultSearchTerm).javaMailAuthenticator(new Authenticator() {
							@Override
							protected PasswordAuthentication getPasswordAuthentication() {
								return new PasswordAuthentication(email, password);
							}
						}).javaMailProperties(
								p -> p.put("mail.mime.allowutf8", "true").put("mail.imap.connectionpoolsize", "5")
										.put("mail.imap.starttls.enable", "true").put("mail.imap.ssl.trust", "*")),
						e -> e.poller(Pollers.fixedRate(5000).maxMessagesPerPoll(1)))
				.<MimeMessage>handle((payload, header) -> logMail(payload)).get();

	}
	
	
	private Object logMail(MimeMessage message) {
		try {
			logger.info(message.getContent().toString());
		} catch (Exception e) {
			logger.error("Read mail content error", e);
		}
		return null;
	}

}
