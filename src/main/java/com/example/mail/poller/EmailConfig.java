package com.example.mail.poller;

public class EmailConfig {

	private String email;
	private String password;
	private String fetchStrategy;

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getFetchStrategy() {
		if(fetchStrategy == null) {
			return "imapPollFlow";
		}
		return fetchStrategy;
	}

	public void setFetchStrategy(String fetchStrategy) {
		this.fetchStrategy = fetchStrategy;
	}

}
