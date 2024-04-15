package org.apache.commons.mail;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import javax.mail.Message;
import javax.mail.Session;
import java.util.Date;
import java.util.Properties;
import static org.junit.Assert.*;

public class EmailTest 
{

    private EmailConcrete email;

    @Before
    public void setUp() 
    {
        email = new EmailConcrete();
    }

    @After
    public void tearDown() 
    {
        email = null; // To ensure the reference is dropped after each test
    }

    //----------------------------------------------------------------------------------//

    @Test
    public void testGetSocketConnectionTimeout() 
    {
        int to = 30000;
        email.setSocketConnectionTimeout(to);
        assertEquals(to, email.getSocketConnectionTimeout());
    }

    //----------------------------------------------------------------------------------//

    @Test
    public void testSetFrom() throws Exception 
    {
        email.setFrom("from@example.com");
        assertNotNull(email.getFromAddress());
    }

    //----------------------------------------------------------------------------------//

    @Test
    public void testGetSentDate() 
    {
        Date date = new Date();
        email.setSentDate(date);
        assertEquals(date, email.getSentDate());
    }

    //----------------------------------------------------------------------------------//

    @Test
    public void testGetMailSessionWithDefaultSettings() throws EmailException 
    {
        email.setHostName("smtp.example.com");
        Session session = email.getMailSession();
        assertNotNull("Session is null", session);
        assertEquals("SMTP host must match", "smtp.example.com", session.getProperty("mail.smtp.host"));
    }

    @Test
    public void testGetMailSessionWithCustomPort() throws EmailException 
    {
        email.setHostName("smtp.example.com");
        email.setSmtpPort(2525);
        Session session = email.getMailSession();
        assertEquals("SMTP port must match", "2525", session.getProperty("mail.smtp.port"));
    }

    @Test
    public void testGetMailSessionWithAuthentication() throws EmailException 
    {
        email.setHostName("smtp.example.com");
        email.setAuthentication("user", "pass");
        Session session = email.getMailSession();
        assertTrue("SMTP auth must be true", Boolean.parseBoolean(session.getProperty("mail.smtp.auth")));
    }

    @Test(expected = EmailException.class)
    public void testGetMailSessionWithoutHostName() throws EmailException 
    {
        email.getMailSession();
    }

    //----------------------------------------------------------------------------------//

    @Test
    public void testGetHostNameWhenSetExplicitly() 
    {
        email.setHostName("smtp.example.com");
        assertEquals("Expected hostname to match explicit value", "smtp.example.com", email.getHostName());
    }

    @Test
    public void testGetHostNameWhenNotSet() 
    {
        assertNull("Expected hostname to return null", email.getHostName());
    }

    @Test
    public void testGetHostNameFromSession() 
    {
        Properties properties = new Properties();
        properties.put("mail.smtp.host", "smtp.example.com");
        Session session = Session.getInstance(properties);
        email.setMailSession(session);
        String hostName = email.getHostName();
        assertNotNull("Host name shouldn't be null when retrieved from session", hostName);
        assertEquals("smtp.example.com", hostName);
    }

    //----------------------------------------------------------------------------------//

    @Test(expected = IllegalStateException.class)
    public void testBuildMimeMessageTwice() throws Exception 
    {
        email.setHostName("smtp.example.com");
        email.setFrom("sender@example.com");
        email.addTo("recipient@example.com");
        email.setSubject("Test Subject");
        email.setContent("This is a test", "text/plain");
        email.buildMimeMessage();
        email.buildMimeMessage();
    }

    @Test(expected = EmailException.class)
    public void testBuildMimeMessageWithoutFrom() throws Exception 
    {
        email.setHostName("smtp.example.com");
        email.addTo("recipient@example.com");
        email.setSubject("Test Subject");
        email.setContent("This is a test", "text/plain");
        email.buildMimeMessage();
    }

    @Test(expected = EmailException.class)
    public void testBuildMimeMessageWithoutRecipients() throws Exception 
    {
        email.setHostName("smtp.example.com");
        email.setFrom("sender@example.com");
        email.setSubject("Test Subject");
        email.setContent("This is a test", "text/plain");
        email.buildMimeMessage();
    }

    @Test
    public void testBuildMimeMessageWithReplyToAndHeaders() throws Exception 
    {
        email.setHostName("smtp.example.com");
        email.setFrom("sender@example.com");
        email.addTo("recipient@example.com");
        email.addReplyTo("replyto@example.com");
        email.addHeader("X-Custom-Header", "CustomValue");
        email.setSubject("Test Subject");
        email.setContent("This is a test email.", "text/plain");
        email.buildMimeMessage();

        assertNotNull("ReplyTo must be set", email.getMimeMessage().getReplyTo());
        assertEquals("Header must match", "CustomValue", email.getMimeMessage().getHeader("X-Custom-Header")[0]);
    }

    @Test
    public void testBuildMimeMessageWithCharset() throws Exception 
    {
        email.setHostName("smtp.example.com");
        email.setFrom("sender@example.com");
        email.addTo("recipient@example.com");
        email.setCharset("UTF-8");
        email.setSubject("Subject with Charset");
        email.buildMimeMessage();
        assertNotNull("MimeMessage subject should not be null", email.getMimeMessage().getSubject());
    }

    @Test
    public void testBuildMimeMessageWithContentAndType() throws Exception 
    {
        email.setHostName("smtp.example.com");
        email.setFrom("sender@example.com");
        email.addTo("recipient@example.com");
        email.setContent("Content with ContentType", "text/html");
        email.buildMimeMessage();
        assertEquals("Content type should be text/html", "text/html", email.getMimeMessage().getDataHandler().getContentType());
    }

    @Test
    public void testBuildMimeMessageWithCcRecipients() throws Exception 
    {
        email.setHostName("smtp.example.com");
        email.setFrom("sender@example.com");
        email.addTo("recipient@example.com");
        email.addCc("cc@example.com");
        email.buildMimeMessage();

        assertTrue("Should have CC recipients", email.getMimeMessage().getRecipients(Message.RecipientType.CC).length > 0);
    }

    @Test
    public void testBuildMimeMessageWithBccRecipients() throws Exception 
    {
        email.setHostName("smtp.example.com");
        email.setFrom("sender@example.com");
        email.addTo("recipient@example.com");
        email.addBcc("bcc@example.com");
        email.buildMimeMessage();
        assertTrue("Should have BCC recipients", email.getMimeMessage().getRecipients(Message.RecipientType.BCC).length > 0);
    }

    //----------------------------------------------------------------------------------//

    @Test
    public void testAddReplyToWithEmailOnly() throws EmailException 
    {
        email.addReplyTo("replyto@example.com");
        assertEquals("replyto@example.com", email.getReplyToAddresses().get(0).getAddress());
    }

    @Test
    public void testAddReplyToWithEmailAndName() throws EmailException 
    {
        email.addReplyTo("replyto@example.com", "Reply To");
        assertEquals("replyto@example.com", email.getReplyToAddresses().get(0).getAddress());
        assertEquals("Reply To", email.getReplyToAddresses().get(0).getPersonal());
    }

    @Test
    public void testAddReplyToWithEmailNameAndCharset() throws EmailException 
    {
        email.setCharset("UTF-8");
        email.addReplyTo("replyto@example.com", "67", "UTF-8");
        assertEquals("67", email.getReplyToAddresses().get(0).getPersonal());
    }

    @Test(expected = EmailException.class)
    public void testAddReplyToWithInvalidEmail() throws EmailException 
    {
        email.addReplyTo("invalid-email");
    }

    @Test(expected = NullPointerException.class)
    public void testAddReplyToWithNullEmail() throws EmailException 
    {
        email.addReplyTo(null);
    }

    //----------------------------------------------------------------------------------//

    @Test
    public void testAddBccWithSingleValidEmail() throws Exception 
    {
        email.addBcc("singlebcc@example.com");
        assertEquals("Expected 1 BCC address", 1, email.getBccAddresses().size());
    }

    @Test
    public void testAddBccWithMultipleValidEmails() throws Exception 
    {
        String[] emails = {"bcc1@example.com", "bcc2@example.com"};
        email.addBcc(emails);
        assertEquals("Expected 2 BCC addresses", 2, email.getBccAddresses().size());
    }

    @Test(expected = EmailException.class)
    public void testAddBccWithNullEmails() throws Exception 
    {
        email.addBcc((String[]) null);
    }

    @Test(expected = EmailException.class)
    public void testAddBccWithEmptyEmails() throws Exception 
    {
        email.addBcc(); // Empty argument list
    }

    //----------------------------------------------------------------------------------//

    @Test
    public void testAddCcWithSingleValidEmail() throws Exception 
    {
        email.addCc("testcc@example.com");
        assertEquals(1, email.getCcAddresses().size());
    }

    @Test
    public void testAddCcWithMultipleValidEmails() throws Exception 
    {
        String[] ccEmails = {"cc1@example.com", "cc2@example.com"};
        email.addCc(ccEmails);
        assertEquals("Expected 2 CC addresses", 2, email.getCcAddresses().size());
    }

    @Test(expected = EmailException.class)
    public void testAddCcWithNullEmails() throws Exception 
    {
        email.addCc((String[]) null);
    }

    @Test(expected = EmailException.class)
    public void testAddCcWithEmptyEmails() throws Exception 
    {
        email.addCc();
    }

    //----------------------------------------------------------------------------------//

    @Test
    public void testAddHeader() 
    {
        email.addHeader("X-Test-Header", "HeaderValue");
        assertTrue(email.getHeaders().containsKey("X-Test-Header"));
        assertEquals("HeaderValue", email.getHeaders().get("X-Test-Header"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testAddHeaderWithNullName() 
    {
        email.addHeader(null, "SomeValue");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testAddHeaderWithEmptyName() 
    {
        email.addHeader("", "SomeValue");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testAddHeaderWithNullValue() 
    {
        email.addHeader("X-Test-Header", null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testAddHeaderWithEmptyValue() 
    {
        email.addHeader("X-Test-Header", "");
    }
}
        