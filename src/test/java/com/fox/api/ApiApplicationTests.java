package com.fox.api;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import javax.mail.internet.InternetAddress;
import java.io.UnsupportedEncodingException;
import java.util.Date;

@SpringBootTest
class ApiApplicationTests {

    @Autowired
    private JavaMailSender javaMailSender;

    @Test
    void contextLoads() throws UnsupportedEncodingException {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setSubject("这是一封测试邮件");
        message.setFrom(new InternetAddress("lusongsong@1fox3.com", "张玉洁").toString());
        message.setTo("lusongsong@jd.com");
//        message.setCc("37xxxxx37@qq.com");
//        message.setBcc("14xxxxx098@qq.com");
        message.setSentDate(new Date());
        message.setText("这是测试邮件的正文");
        javaMailSender.send(message);
    }
}
