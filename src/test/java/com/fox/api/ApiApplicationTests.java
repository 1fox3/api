package com.fox.api;

import com.fox.api.exception.self.ServiceException;
import com.fox.api.util.RandomStrUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;

import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.UnsupportedEncodingException;
import java.util.Date;

@SpringBootTest
class ApiApplicationTests {

    @Autowired
    private JavaMailSender javaMailSender;

    @Test
    void contextLoads() {
        try {
            String verifyCode = RandomStrUtil.getRandomStr(6, RandomStrUtil.NUMBER);
            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(message, true);
            mimeMessageHelper.setFrom(new InternetAddress("lusongsong@1fox3.com", "股票助手").toString());
            mimeMessageHelper.setTo("lusongsong@jd.com");
            mimeMessageHelper.setSubject("股票助手验证码");
            mimeMessageHelper.setSentDate(new Date());
            StringBuffer stringBuffer = new StringBuffer(500);
            stringBuffer.append("[股票助手]您的验证码");
            stringBuffer.append("<font color=\"blue\">");
            stringBuffer.append("156874");
            stringBuffer.append("</font>");
            stringBuffer.append("，请在15分钟内按照页面提示提交验证码，切勿将验证码泄露与他人。");
            mimeMessageHelper.setText(stringBuffer.toString(), true);
            javaMailSender.send(message);
        } catch (UnsupportedEncodingException e) {
            throw new ServiceException(1, "发件人信息错误:" + e.getMessage());
        } catch (MailException e) {
            throw new ServiceException(1, "邮件发送错误:" + e.getMessage());
        } catch (MessagingException e) {
            throw new ServiceException(1, "邮件内容错误:" + e.getMessage());
        }

    }

    @Test
    void randomStr() {
        System.out.println(RandomStrUtil.getRandomStr(50, RandomStrUtil.NUMBER));
    }
}
