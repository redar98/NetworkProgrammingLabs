package smtp;

import com.sun.mail.smtp.SMTPTransport;
import pop3.ReadEmail;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import java.util.Calendar;
import java.util.Properties;

public class WriteEmail {
    private static Multipart multipart = new MimeMultipart();

    public static void main(String[] args) {
        Session session = Session.getInstance(GetProperties(), null);

        try {
            Message msg = createMessage(session);
            SMTPTransport t = (SMTPTransport)session.getTransport("smtps");
            t.connect("smtp.gmail.com", ReadEmail.USERNAME, ReadEmail.PASSWORD);
            t.sendMessage(msg, msg.getAllRecipients());

            System.out.println("Response: " + t.getLastServerResponse());
            t.close();
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }

    private static Message createMessage(Session session) throws MessagingException{
        Message msg = new MimeMessage(session);
        msg.setFrom(new InternetAddress("mail@pr.com"));
        msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(ReadEmail.USERNAME, false));
        msg.setSubject("SMTP Message Subject " + System.currentTimeMillis());
        msg.setSentDate(Calendar.getInstance().getTime());


        addMessageBody("<em>This is a message body.</em><br>" +
                "<strong>Important information ahead!</strong><br>" +
                "Lorem ipsum dolor sit amet... Es nos duos einth..." +
                "<h3><a href=\"https://google.com\">Click Here!</a></h3>" +
                "<a href=\"https://www.google.com/search?q=barrels&tbm=isch\"><img src=\"cid:image\"></a>", "text/html; charset=utf-8");
        addAttachmentWithContentId("logo.png", "<image>");
        addAttachmentWithContentId("testFile.txt", null);

        msg.setContent(multipart);

        return msg;
    }

    private static void addAttachmentWithContentId(String path, String contentId) throws MessagingException {
        BodyPart messageBodyPart = new MimeBodyPart();
        DataSource fds = new FileDataSource(path);
        messageBodyPart.setDataHandler(new DataHandler(fds));
        messageBodyPart.setHeader("Content-ID", contentId);
        messageBodyPart.setFileName(path);
        multipart.addBodyPart(messageBodyPart);
    }

    private static void addMessageBody(String contentText, String contentType) throws MessagingException {
        BodyPart messageBodyPart = new MimeBodyPart();
        messageBodyPart.setContent(contentText, contentType);
        multipart.addBodyPart(messageBodyPart);
    }

    private static Properties GetProperties(){
        Properties props = System.getProperties();
        props.put("mail.smtps.host","smtp.gmail.com");
        props.put("mail.smtps.auth","true");
        props.put("mail.smtp.EnableSSL.enable","true");
        return props;
    }
}
