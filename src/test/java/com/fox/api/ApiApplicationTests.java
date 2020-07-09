package com.fox.api;

import com.fox.api.dao.user.entity.StockHelperUserInfoEntity;
import com.fox.api.dao.user.mapper.StockHelperUserInfoMapper;
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
    @Autowired
    private StockHelperUserInfoMapper stockHelperUserInfoMapper;

    @Test
    void contextLoads() {
        try {
            StockHelperUserInfoEntity stockHelperUserInfoEntity = new StockHelperUserInfoEntity();
            if (null == stockHelperUserInfoEntity || null == stockHelperUserInfoEntity.getId()) {
                stockHelperUserInfoEntity.setAccount("aaaaa");
                stockHelperUserInfoEntity.setType(1);
                Integer a = stockHelperUserInfoMapper.insert(stockHelperUserInfoEntity);
                System.out.println(a);
            }

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

    }

    @Test
    void randomStr() {
        System.out.println(RandomStrUtil.getRandomStr(50, RandomStrUtil.NUMBER));
    }
}
