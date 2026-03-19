package notify;

import java.util.*;
import jakarta.mail.*;
import jakarta.mail.internet.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import config.AppConfig;

public class SendEmail implements Job {
	private static final Logger logger = LoggerFactory.getLogger(SendEmail.class);
	private static final String from = AppConfig.get("SMTP_FROM");
	private static final String pwd = AppConfig.get("SMTP_PASSWORD");

	// args: recipient's email, mail subject and text
	public static boolean sendtext(String to, String subject, String text) {

		String host = "smtp.gmail.com";
		Properties properties = System.getProperties();
		properties.put("mail.smtp.host", host);
		properties.put("mail.smtp.port", "465");
		properties.put("mail.smtp.ssl.enable", "true");
		properties.put("mail.smtp.auth", "true");

		Session session = Session.getInstance(properties, new jakarta.mail.Authenticator() {
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(from, pwd);
			}

		});

		// Used to debug SMTP issues
		// session.setDebug(true);

		try {
			// Create a default MimeMessage object.
			MimeMessage message = new MimeMessage(session);

			// Set From: header field of the header.
			message.setFrom(new InternetAddress(from));

			// Set To: header field of the header.
			message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));

			// Set Subject: header field
			message.setSubject(subject);

			// Now set the actual message
			message.setText(text);

			logger.info("Sending email to {}", to);
			Transport.send(message);
			logger.info("Email sent successfully to {}", to);

		} catch (MessagingException mex) {
			throw new RuntimeException("Failed to send email", mex);
		}
		return true;
	}

	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		// TODO Auto-generated method stub
		String host = "smtp.gmail.com";
		Properties properties = System.getProperties();
		properties.put("mail.smtp.host", host);
		properties.put("mail.smtp.port", "465");
		properties.put("mail.smtp.ssl.enable", "true");
		properties.put("mail.smtp.auth", "true");
		Session session = Session.getInstance(properties, new jakarta.mail.Authenticator() {
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(from, pwd);
			}

		});
		// Used to debug SMTP issues — enable by setting logger level to DEBUG
		session.setDebug(logger.isDebugEnabled());
		try {
			MimeMessage message = new MimeMessage(session);
			message.setFrom(new InternetAddress(from));

			JobDataMap dataMap = context.getJobDetail().getJobDataMap();
			// Set To: header field of the header.
			message.addRecipient(Message.RecipientType.TO, new InternetAddress(dataMap.getString("to")));

			// Set Subject: header field
			message.setSubject(dataMap.getString("subject"));

			// Now set the actual message
			message.setText(dataMap.getString("text"));
			Transport.send(message);

		} catch (MessagingException mex) {
			throw new RuntimeException("Failed to send email", mex);
		}

	}

}
