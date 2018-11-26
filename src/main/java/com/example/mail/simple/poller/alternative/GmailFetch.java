package com.example.mail.simple.poller.alternative;

import java.util.Properties;

import javax.mail.Flags;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.search.FlagTerm;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GmailFetch {
	private static Logger logger = LoggerFactory.getLogger(GmailFetch.class);

	
	//use with schedule frameworks
	public static void main(String[] args) throws Exception {

		Session session = Session.getDefaultInstance(new Properties());
		Store store = session.getStore("imaps");
		store.connect("imap.googlemail.com", 993, "defne.ece.karakas@gmail.com", "****");
		Folder inbox = store.getFolder("INBOX");
		inbox.open(Folder.READ_WRITE);

		Message[] messages = inbox.search(new FlagTerm(new Flags(Flags.Flag.SEEN), false));



		for (Message message : messages) {
			message.setFlag(Flags.Flag.SEEN, true);
			logger.info("sendDate: {} ,subject {}" ,message.getSentDate(), message.getSubject());
		}
	}
}
