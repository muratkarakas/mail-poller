package com.example.mail.poller;

import java.io.IOException;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.mail.Flags;
import javax.mail.internet.MimeMessage;
import javax.mail.search.FlagTerm;
import javax.mail.search.SearchTerm;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.integration.dsl.Pollers;
import org.springframework.integration.dsl.context.StandardIntegrationFlowContext;
import org.springframework.integration.mail.support.DefaultMailHeaderMapper;
import org.springframework.integration.mapping.HeaderMapper;
import org.springframework.integration.scheduling.PollerMetadata;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootApplication
public class MailPollerApplication {

	public static void main(String[] args) {
		SpringApplication.run(MailPollerApplication.class, args);
	}
	
	
	
	private ObjectMapper mapper = new ObjectMapper();

	@Value("${poller.emails}")
	private String emails;


	@Autowired
	private ApplicationContext appContext;
	
	
	@Autowired
	private StandardIntegrationFlowContext context;

	@Bean(name = PollerMetadata.DEFAULT_POLLER)
	public PollerMetadata poller() {
		return Pollers.fixedRate(10000).maxMessagesPerPoll(100).get();

	}

	@PostConstruct
	private void setupPollers() throws IOException {
		
		List<EmailConfig> emailConfigs = mapper.readValue(emails, new TypeReference<List<EmailConfig>>() {});
		for (EmailConfig item : emailConfigs) {
			ImapInterationFlow flow = appContext.getBean(item.getFetchStrategy(),ImapInterationFlow.class);
			context.registration(flow.interationFlow(item.getEmail(),item.getPassword())).register();
		}

	}



	@Bean("mailHeaderMapper")
	public HeaderMapper<MimeMessage> mailHeaderMapper() {
		return new DefaultMailHeaderMapper();
	}

	
	@Bean("imapURL")
	public String imapUrl() {
		return "imaps://imap.gmail.com:993/INBOX";
	}
	@Bean("searchTerm")
	public SearchTerm notSeenTerm() {
		return new FlagTerm(new Flags(Flags.Flag.SEEN), false);
	}

}
