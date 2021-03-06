package com.fox.api.entity.vo.open.wechatmini.login;

import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

/**
 * 微信小程序登录
 * @author lusongsong
 * @date 2020/3/20 16:47
 */
@Data
public class WechatMiniLoginVo {
    @NotNull(message = "code必须存在")
    @NotEmpty(message = "code必须不能为空")
    String code;
    @NotNull(message = "iv必须存在")
    @NotEmpty(message = "iv必须不能为空")
    String iv;
    @NotNull(message = "encryptedData必须存在")
    @NotEmpty(message = "encryptedData必须不能为空")
    String encryptedData;
    @NotNull(message = "encryptedData必须存在")
    @NotEmpty(message = "encryptedData必须不能为空")
    String platId;
}
