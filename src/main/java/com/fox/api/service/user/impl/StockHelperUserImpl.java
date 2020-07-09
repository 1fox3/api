package com.fox.api.service.user.impl;

import com.fox.api.dao.user.entity.StockHelperUserInfoEntity;
import com.fox.api.dao.user.entity.UserEntity;
import com.fox.api.dao.user.entity.UserLoginEntity;
import com.fox.api.dao.user.mapper.StockHelperUserInfoMapper;
import com.fox.api.dao.user.mapper.UserMapper;
import com.fox.api.entity.dto.login.LoginDto;
import com.fox.api.enums.code.user.StockHelperUserCode;
import com.fox.api.exception.self.ServiceException;
import com.fox.api.service.user.StockHelperUserService;
import com.fox.api.service.user.UserLoginService;
import com.fox.api.util.DateUtil;
import com.fox.api.util.ParamCheckUtil;
import com.fox.api.util.RandomStrUtil;
import com.fox.api.util.redis.UserRedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.UnsupportedEncodingException;
import java.util.Date;

/**
 * 股票助手用户服务
 * @author lusongsong
 */
@Service
public class StockHelperUserImpl implements StockHelperUserService {
    final static String PLAT_ID = "stock_helper";
    final static Integer PLAT_TYPE = 3;
    /**
     * 默认登录有效时间(天)
     */
    final static Integer LOGIN_TIME = 180;
    final static String STOCK_HELPER_VERIFY_CODE_CACHE_KEY = "StockHelperVerifyCode:";

    @Autowired
    private JavaMailSender javaMailSender;

    @Autowired
    private UserRedisUtil userRedisUtil;

    @Autowired
    private StockHelperUserInfoMapper stockHelperUserInfoMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private UserLoginService userLoginService;

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
     * @param account
     * @param verifyCode
     * @return
     */
    private boolean verifyCode(String account, String verifyCode) {
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
     * 用户登录
     *
     * @param account
     * @param verifyCode
     * @return
     */
    @Override
    public LoginDto login(String account, String verifyCode) {
        //验证码错误
        if (false == verifyCode(account, verifyCode)) {
            throw new ServiceException(StockHelperUserCode.VERIFY_CODE_ERROR);
        }

        //保存用户信息
        StockHelperUserInfoEntity stockHelperUserInfoEntity = getInfoByAccount(account);
        if (null == stockHelperUserInfoEntity || null == stockHelperUserInfoEntity.getId()) {
            stockHelperUserInfoEntity.setAccount(account);
            stockHelperUserInfoEntity.setType(1);
            try {
                stockHelperUserInfoMapper.insert(stockHelperUserInfoEntity);
            } catch (Exception e) {
                throw new ServiceException(1, "用户信息保存失败：" + e.getMessage());
            }
        }

        //添加到用户列表
        UserEntity userEntity = userMapper.getByPlatUserId(account, PLAT_ID, PLAT_TYPE);
        if (null == userEntity || null == userEntity.getId()) {
            userEntity.setPlatId(PLAT_ID);
            userEntity.setPlatType(PLAT_TYPE);
            userEntity.setPlatUserId(account);
            try {
                userMapper.insert(userEntity);
            } catch (Exception e) {
                throw new ServiceException(1, "用户添加失败：" + e.getMessage());
            }
        }

        UserLoginEntity userLoginEntity = new UserLoginEntity();
        userLoginEntity.setUserId(userEntity.getId());
        Date loginDate = new Date();
        String loginTime = DateUtil.dateToStr(loginDate, DateUtil.TIME_FORMAT_1);
        userLoginEntity.setLoginTime(loginTime);
        userLoginEntity.setExpireTime(DateUtil.getRelateDate(0, LOGIN_TIME, 0, DateUtil.TIME_FORMAT_1));
        String sessionid = userLoginService.login(userLoginEntity);
        LoginDto loginDto = new LoginDto();
        loginDto.setSessionid(sessionid);
        loginDto.setExpireTime(LOGIN_TIME * 86400);
        return loginDto;
    }

    /**
     * 退出
     *
     * @param account
     * @param sessionid
     * @return
     */
    @Override
    public boolean logout(String account, String sessionid) {
        UserLoginEntity userLoginEntity = userLoginService.getUserLoginBySessionid(sessionid);
        UserEntity userEntity = userMapper.getByPlatUserId(account, PLAT_ID, PLAT_TYPE);
        if (null != userLoginEntity && null != userEntity
                && null != userLoginEntity.getUserId() && null != userEntity.getId()
                && userLoginEntity.getUserId().equals(userEntity.getId())) {
            userLoginService.logout(sessionid, userEntity.getId());
            return true;
        }
        return false;
    }
}
