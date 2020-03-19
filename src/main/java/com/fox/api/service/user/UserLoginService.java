package com.fox.api.service.user;

import com.fox.api.model.user.entity.UserLoginEntity;

public interface UserLoginService {
    UserLoginEntity getUserLoginBySessionid(Integer sessionid);
}
