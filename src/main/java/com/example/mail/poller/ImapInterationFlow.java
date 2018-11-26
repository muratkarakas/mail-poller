package com.example.mail.poller;

import org.springframework.integration.dsl.IntegrationFlow;

public interface ImapInterationFlow {

	public IntegrationFlow interationFlow(String email, String password);

}