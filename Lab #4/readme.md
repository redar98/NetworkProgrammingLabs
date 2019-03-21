# Java SMTP/POP3

I used JavaMail class to show the work of SMTP and POP3. JavaMail is a Java API used to send and receive email via SMTP, POP3 and IMAP. JavaMail is built into the Java EE platform, but also provides an optional package for use in Java SE.


## Reading emails from Inbox in Gmail

Below is an abstract code for reading all email from your inbox. Complete class can be found [here!](src/main/java/pop3/ReadEmail.java)

```java
// set mail server
String host = "pop.gmail.com";

// set properties
Properties props = new Properties();
props.put("mail.pop3.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
props.put("mail.pop3.socketFactory.port", "995");
props.put("mail.pop3.port", "995");
props.put("mail.pop3.host", "pop.gmail.com");
props.put("mail.pop3.user", USERNAME);
props.put("mail.store.protocol", "pop3");

// connect to pop3
Session session = Session.getDefaultInstance(props);

try {
    Store store = session.getStore("pop3");
    store.connect(host, USERNAME, PASSWORD);
    Folder inbox = store.getFolder("INBOX");
    inbox.open(Folder.READ_ONLY);

    // get the list of inbox messages
    Message[] messages = inbox.getMessages();
     
    for (int i = 0; i < messages.length; i++) {
        System.out.println(messages[i].getAllInformation());
    }
    inbox.close(true);
    store.close();
} catch (Exception e) {
    e.printStackTrace();
}
```

## Sending emails with attachments using SMTP

To keep it simple, I will just show a part of completely implemented class that can be found [here](src/main/java/smtp/WriteEmail.java)

```java
Session session = Session.getInstance(GetProperties(), null);

try {
    // create message object and fill basic fields
    Message msg = new MimeMessage(session);
    msg.setFrom(new InternetAddress("mail@pr.com"));
    msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(ReadEmail.USERNAME, false));
    msg.setSubject("SMTP Message Subject " + System.currentTimeMillis());
    msg.setSentDate(Calendar.getInstance().getTime());

    // this method puts given text in message body
    addMessageBody("<em>This is a message body.</em><br>" +
            "<strong>Important information ahead!</strong><br>" +
            "Lorem ipsum dolor sit amet... Es nos duos einth..." +
            "<h3><a href=\"https://google.com\">Click Here!</a></h3>" +
            "<a href=\"https://www.google.com/search?q=barrels&tbm=isch\"><img src=\"cid:image\"></a>", "text/html; charset=utf-8");
    
    // include attachments that can directly be used in message body or be sent as files
    addAttachmentWithContentId("logo.png", "<image>");
    addAttachmentWithContentId("testFile.txt", null);

    // multipart contains all of our message parts (body, attachments...)
    msg.setContent(multipart);
    
    // connect using SMTP Transport object with given host, email and password
    SMTPTransport t = (SMTPTransport)session.getTransport("smtps");
    t.connect("smtp.gmail.com", ReadEmail.USERNAME, ReadEmail.PASSWORD);
    t.sendMessage(msg, msg.getAllRecipients());

    System.out.println("Response: " + t.getLastServerResponse());
    t.close();
} catch (MessagingException e) {
    e.printStackTrace();
}
```

## Conclusions

Using SMTP (Simple Mail Transfer Protocol) makes it easy to send emails with any number of attachments (including content with id for using them directly in html message body). For getting information from all of the new emails we use POP3 (Post Office Protocol) (although there are alternatives like IMAP...) and retrieve message body with all of its headers and other specific information. All these protocols are implemented in Java Mail API, which makes it easier to check the workflow of these processes. To make them work we have to setup some settings like host, port, user, password, protocol and etc...