package Models;

import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import Core.CApplication;
import javafx.application.Platform;

public class MailModel extends Thread {

	private String username;
	private String password;
	private Properties props;

	private String subject;
	private String text;
	private String fromEmail;
	private String toEmail;
	private String fileName = null;
	private String file = null;

	public MailModel(String username, String password) {
		this.username = username;
		this.password = password;
		props = new Properties();
		props.put("mail.smtp.host", "smtp.yandex.ru");
		props.put("mail.smtp.socketFactory.port", "465");
		props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.port", "465");
	}

	public void run() // Этот метод будет выполнен в побочном потоке
	{
		sendWhithFile(this.getSubject(), this.getText(), this.getFromEmail(), this.getToEmail(), this.getFileName(),
				this.getFile());
	}

	private void sendWhithFile(String subject, String text, String fromEmail, String toEmail, String fileName,
			String file) {
		Session session = Session.getDefaultInstance(props, new Authenticator() {
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(username, password);
			}
		});

		try {
			Message message = new MimeMessage(session);
			message.setFrom(new InternetAddress(username));
			message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail));
			message.setSubject(subject);

			MimeBodyPart messageBodyPart = new MimeBodyPart();
			messageBodyPart.setText(text);

			Multipart multipart = new MimeMultipart();

			multipart.addBodyPart(messageBodyPart);

			if (this.file != null) {
				messageBodyPart = new MimeBodyPart();
				DataSource source = new FileDataSource(file);
				messageBodyPart.setDataHandler(new DataHandler(source));
				messageBodyPart.setFileName(fileName);
				multipart.addBodyPart(messageBodyPart);
			}
			message.setContent(multipart);

			Transport.send(message);
			/*
			 * Из-за мультипоточности вызвать алент напрямую нельзя, поэтому
			 * используется Platform.runLater
			 */
			if (this.file != null) {
				Platform.runLater(new Runnable() {
					@Override
					public void run() {
						CApplication.alert("Запрос с пиcьмом успешно отправлен!");
					}
				});
			}

		} catch (MessagingException e) {
			throw new RuntimeException(e);
		}
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public String getFromEmail() {
		return fromEmail;
	}

	public void setFromEmail(String fromEmail) {
		this.fromEmail = fromEmail;
	}

	public String getToEmail() {
		return toEmail;
	}

	public void setToEmail(String toEmail) {
		this.toEmail = toEmail;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getFile() {
		return file;
	}

	public void setFile(String file) {
		this.file = file;
	}
}