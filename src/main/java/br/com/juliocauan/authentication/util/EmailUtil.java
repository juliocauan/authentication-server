package br.com.juliocauan.authentication.util;

import org.openapitools.model.EmailType;
import org.springframework.mail.MailSendException;

import br.com.juliocauan.authentication.util.emailers.Emailer;
import br.com.juliocauan.authentication.util.emailers.GmailEmailer;
import br.com.juliocauan.authentication.util.emailers.GreenMailEmailer;
import lombok.experimental.UtilityClass;

@UtilityClass
public final class EmailUtil {

    protected static Emailer currentEmailer = null;

    public static void sendEmail(String receiver, String subject, String message) {
        if (currentEmailer == null)
            throw new MailSendException("Emailer not set. ADMIN must set one.");

        currentEmailer.sendSimpleEmail(receiver, subject, message);
    }

    public static void setEmailer(String username, String key, EmailType emailerType) {
        if(emailerType == null) {
            currentEmailer = null;
            return;
        }
        else if(emailerType.getValue().equals("GMAIL")) currentEmailer = new GmailEmailer(username, key);
        else currentEmailer = new GreenMailEmailer(username, key);
    }

}
