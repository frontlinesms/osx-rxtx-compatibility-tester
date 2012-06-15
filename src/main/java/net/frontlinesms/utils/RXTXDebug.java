package net.frontlinesms.utils;

import net.frontlinesms.messaging.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;
import javax.mail.*;
import javax.mail.internet.*;

public class RXTXDebug {
	public static void main(String [] args)
	{
		if (args.length == 0 )
		{
			System.out.println("Please use arg 'info' or 'test'");
			return;
		}
		System.out.println("Loading native lib into library path");
		String oldPath = System.getProperty("java.library.path");
		try
		{
			String newPath = new File(".").getCanonicalPath() + File.pathSeparator + oldPath;
			System.out.println("OldPath:::"+oldPath);
			System.out.println("NewPath:::"+newPath);
			System.setProperty("java.library.path", newPath);
		}
		catch(Exception e){
			System.out.println("Failed to set library path");
			e.printStackTrace(System.out);
			System.exit(1);
		}
		if(args[0].compareTo("info") == 0)
		{
			System.out.println("DEBUGGER ::::: System props\n----------");
			String [] props = {"os.arch", "os.name", "os.version", "java.version", "java.vm.mame", "user.name", "user.pc"};
			for(int i = 0; i < props.length; i++)
			{
				System.out.println(props[i] + "=" + System.getProperty(props[i]));
			}
		}
		else if(args[0].compareTo("test") == 0)
		{
			System.out.println("DEBUGGER ::::: RXTX Test\n----------");
			Main mainDetector = new Main();
			mainDetector.main(null);
		}
		else if(args[0].compareTo("mail") == 0)
		{
			send("Debugging: "+System.getProperty("user.name") + " - "+System.getProperty("os.name")+System.getProperty("os.version"), "Report attached",
				new File("report.log").getAbsolutePath());
		}
		else
		{
			System.out.println("Unknown command '"+args[0]+"'");
		}
	}

   	public static void send(String subject, String content, String filepath) {

		// Recipient's email ID needs to be mentioned.
		String toV = "vaneyck@frontlinesms.com";
		String toS = "sitati@frontlinesms.com";

		// Sender's email ID needs to be mentioned
		String from = "registration@frontlinesms.com";

		// Get system properties
		Properties props = System.getProperties();
		props.put("mail.smtp.host", "smtp.gmail.com");
		props.put("mail.debug", "true");
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.starttls.enable", "true");
		props.put("mail.smtp.EnableSSL.enable", "true");

		props.setProperty("mail.smtp.socketFactory.class",
				"javax.net.ssl.SSLSocketFactory");
		props.setProperty("mail.smtp.socketFactory.fallback", "false");
		props.setProperty("mail.smtp.port", "465");
		props.setProperty("mail.smtp.socketFactory.port", "465");

		// Get the default Session object.
		Session session = Session.getDefaultInstance(props);

		try {
			// Create a default MimeMessage object.
			MimeMessage message = new MimeMessage(session);
			message.setFrom(new InternetAddress(from));

			message.addRecipient(Message.RecipientType.TO, new InternetAddress(
					toS));
			message.addRecipient(Message.RecipientType.CC, new InternetAddress(
					toV));
			// Set Subject: header field
			message.setSubject(subject);

			MimeBodyPart mbp1 = new MimeBodyPart();
			MimeBodyPart mbp2 = new MimeBodyPart();
			mbp1.setText(content);
			try {
				File file = new File(filepath);
				if (file.exists()) {
					mbp2.attachFile(file);
					mbp2.setHeader("Content-Transfer-Encoding", "base64");
				}else{
					mbp2.setText("ERROR >>>>>>>> file not found >>>>>>>> ERROR");
				}
			} catch (IOException e) {
				e.printStackTrace();
				mbp2.setText("ERROR >>>>>>>> another error >>>>>>>> ERROR");
			}
			Multipart mp = new MimeMultipart();
			mp.addBodyPart(mbp1);
			mp.addBodyPart(mbp2);
			message.setContent(mp);
			message.setSentDate(new Date());

			Transport transport = session.getTransport("smtp");
			transport.connect("smtp.gmail.com", from, "alexregister");
			transport.sendMessage(message, message.getAllRecipients());
			transport.close();

			System.out.println("Sent message successfully....");
		} catch (MessagingException mex) {
			mex.printStackTrace();
			Exception ex = null;
			if ((ex = mex.getNextException()) != null) {
				ex.printStackTrace();
				System.out.println("##########################\n File not Found \n##########################");
			}
		}
	}
}