package com.fox.api.service.user.impl;

import com.fox.api.dao.user.entity.StockHelperUserInfoEntity;
import com.fox.api.dao.user.mapper.StockHelperUserInfoMapper;
import com.fox.api.enums.code.user.StockHelperUserCode;
import com.fox.api.exception.self.ServiceException;
import com.fox.api.service.user.StockHelperUserService;
import com.fox.api.util.ParamCheckUtil;
import com.fox.api.util.RandomStrUtil;
import com.fox.api.util.redis.UserRedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;

import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.UnsupportedEncodingException;
import java.util.Date;

/**
 * 股票助手用户服务
 * @author lusongsong
 */
public class StockHelperUserImpl implements StockHelperUserService {
    final static String STOCK_HELPER_VERIFY_CODE_CACHE_KEY = "StockHelperVerifyCode:";

    @Autowired
    private JavaMailSender javaMailSender;

    @Autowired
    private UserRedisUtil userRedisUtil;

    @Autowired
    private StockHelperUserInfoMapper stockHelperUserInfoMapper;

    /**
     * 获取保存验证码的缓存key
     * @param account
     * @return
     */
    private String getVerifyCodeCacheKey(String account) {
        return STOCK_HELPER_VERIFY_CODE_CACHE_KEY + (null == account ? "" : account);
    }

    /**
     * 发送验证码
     *
     * @param account
     * @return
     */
    @Override
    public boolean sendCode(String account) {
        if (!ParamCheckUtil.isEmail(account)) {
            throw new ServiceException(StockHelperUserCode.ACCOUNT_ERROR);
        }
        try {
            String verifyCode = RandomStrUtil.getRandomStr(6, RandomStrUtil.NUMBER);
            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(message, true);
            mimeMessageHelper.setFrom(new InternetAddress("lusongsong@1fox3.com", "股票助手").toString());
            mimeMessageHelper.setTo(account);
            mimeMessageHelper.setSubject("股票助手验证码");
            mimeMessageHelper.setSentDate(new Date());
            StringBuffer stringBuffer = new StringBuffer(500);
            stringBuffer.append("[股票助手]您的验证码");
            stringBuffer.append("<font color=\"blue\">");
            stringBuffer.append(verifyCode);
            stringBuffer.append("</font>");
            stringBuffer.append("，请在15分钟内按照页面提示提交验证码，切勿将验证码泄露与他人。");
            mimeMessageHelper.setText(stringBuffer.toString(), true);
            javaMailSender.send(message);
            userRedisUtil.set(getVerifyCodeCacheKey(account), verifyCode, 900L);
        } catch (UnsupportedEncodingException e) {
            throw new ServiceException(1, "发件人信息错误:" + e.getMessage());
        } catch (MailException e) {
            throw new ServiceException(1, "邮件发送错误:" + e.getMessage());
        } catch (MessagingException e) {
            throw new ServiceException(1, "邮件内容错误:" + e.getMessage());
        }
        return true;
    }

    /**
     * 验证验证码
     *
     * @param account
     * @param verifyCode
     * @return
     */
    @Override
    public boolean verifyCode(String account, String verifyCode) {
        return null != verifyCode && verifyCode.equals(userRedisUtil.get(getVerifyCodeCacheKey(account)));
    }

    /**
     * 获取用户信息
     *
     * @param account
     * @return
     */
    @Override
    public StockHelperUserInfoEntity getInfoByAccount(String account) {
        return stockHelperUserInfoMapper.getByAccount(account);
    }

    /**
     * 验证账号密码
     *
     * @param account
     * @param pwd
     * @return
     */
    @Override
    public boolean verifyPwd(String account, String pwd) {
        return false;
    }

    /**
     * 注册
     *
     * @param account
     * @param pwd
     * @param verifyCode
     * @return
     */
    @Override
    public boolean register(String account, String pwd, String verifyCode) {
        return false;
    }

    /**
     * 用户登录
     *
     * @param account
     * @param pwd
     * @param verifyCode
     * @return
     */
    @Override
    public boolean login(String account, String pwd, String verifyCode) {
        return false;
    }

    /**
     * 无验证码登录
     *
     * @param account
     * @param pwd
     * @return
     */
    @Override
    public boolean login(String account, String pwd) {
        return false;
    }

    /**
     * 退出
     *
     * @param account
     * @param pwd
     * @param sessionid
     * @return
     */
    @Override
    public boolean logout(String account, String pwd, String sessionid) {
        return false;
    }
}
