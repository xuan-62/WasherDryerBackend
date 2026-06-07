package notify;

import java.util.Properties;

import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import config.AppConfig;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;

public class SendEmail implements Job {
	private static final Logger logger = LoggerFactory.getLogger(SendEmail.class);
	private static final String SMTP_HOST = "smtp.gmail.com";
	private static final String SMTP_FROM = AppConfig.get("SMTP_FROM");
	private static final String SMTP_PASSWORD = AppConfig.get("SMTP_PASSWORD");

	public static boolean sendtext(String to, String subject, String text) {
		Properties properties = System.getProperties();
		properties.put("mail.smtp.host", SMTP_HOST);
		properties.put("mail.smtp.port", "465");
		properties.put("mail.smtp.ssl.enable", "true");
		properties.put("mail.smtp.auth", "true");

		Session session = Session.getInstance(properties, new jakarta.mail.Authenticator() {
			@Override
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(SMTP_FROM, SMTP_PASSWORD);
			}
		});

		try {
			MimeMessage message = new MimeMessage(session);
			message.setFrom(new InternetAddress(SMTP_FROM));
			message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
			message.setSubject(subject);
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
		Properties properties = System.getProperties();
		properties.put("mail.smtp.host", SMTP_HOST);
		properties.put("mail.smtp.port", "465");
		properties.put("mail.smtp.ssl.enable", "true");
		properties.put("mail.smtp.auth", "true");

		Session session = Session.getInstance(properties, new jakarta.mail.Authenticator() {
			@Override
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(SMTP_FROM, SMTP_PASSWORD);
			}
		});
		session.setDebug(logger.isDebugEnabled());

		try {
			MimeMessage message = new MimeMessage(session);
			message.setFrom(new InternetAddress(SMTP_FROM));
			JobDataMap dataMap = context.getJobDetail().getJobDataMap();
			message.addRecipient(Message.RecipientType.TO, new InternetAddress(dataMap.getString("to")));
			message.setSubject(dataMap.getString("subject"));
			message.setText(dataMap.getString("text"));
			Transport.send(message);
		} catch (MessagingException mex) {
			throw new RuntimeException("Failed to send email", mex);
		}
	}
}
