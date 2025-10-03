package com.fintech.user_service.services;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;



@Service
public class EmailServiceImpl {
    private static final Logger log = LoggerFactory.getLogger(EmailServiceImpl.class);
    //java mail sender interface use
//    @Autowired
//    private JavaMailSender javaMailSender;
//    JavaMailSender javaMailSender= new JavaMailSender() {
//    @Override
//    public void send(SimpleMailMessage... simpleMessages) throws MailException {
//
//    }
//
//    @Override
//    public MimeMessage createMimeMessage() {
//        return null;
//    }

//    @Override
//    public MimeMessage createMimeMessage(InputStream contentStream) throws MailException {
//        return null;
//    }

//    @Override
//    public void send(MimeMessage... mimeMessages) throws MailException {
//
//    }
//};

    //

//    public boolean sendEnail(String to,String subject,String body){
//        try{
//            SimpleMailMessage mail=new SimpleMailMessage();
//            mail.setTo(to);
//            mail.setSubject(subject);
//            mail.setText(body);
//            javaMailSender.send(mail);
//            return true;
//
//
//        }
//        catch (Exception e) {
//            log.error("exception while sending email",e);
//
//            return false;
//        }
//
//    }
}
