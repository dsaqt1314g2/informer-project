package eetac.upc.edu.dsa.dsaqt1314g2.informer.auth;

import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class Correo {

	private final static String password = "19remrofni";
	private final static String username = "informer.universities";

	public static void sendCorreo(String usuario, String clave, String destino) {
		Properties props = new Properties();
		props.put("mail.smtp.host", "smtp.gmail.com");
		props.put("mail.smtp.socketFactory.port", "465");
		props.put("mail.smtp.socketFactory.class","javax.net.ssl.SSLSocketFactory");
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.port", "465");
		Session session = Session.getDefaultInstance(props,	new javax.mail.Authenticator() {
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(username,password);
			}
		});
		try {
			Message message = new MimeMessage(session);
			message.setFrom(new InternetAddress(username+"@gmail.com"));
			message.setRecipients(Message.RecipientType.TO,	InternetAddress.parse(destino));
			message.setSubject("Informacion de registro");
			message.setText("Hola  "+usuario+","+"\n\nBienvenido a la web de informer.\nTu clave de activacion es "+clave);
			Transport.send(message);
		} catch (MessagingException e) {
			throw new RuntimeException(e);
		}
	}
}
