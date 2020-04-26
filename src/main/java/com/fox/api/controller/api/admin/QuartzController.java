package com.fox.api.controller.api.admin;

import com.fox.api.dao.quartz.entity.QuartzJobEntity;
import com.fox.api.entity.dto.result.ResultDto;
import com.fox.api.service.quartz.QuartzJobService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 计划任务管理
 * @author lusongsong
 */
@CrossOrigin(origins = "*")//处理跨域问题
@RestController
@RequestMapping("/admin/quartz")
public class QuartzController {

    @Autowired
    private QuartzJobService quartzJobService;


    @RequestMapping("/schedule")
    public ResultDto add() {
        return ResultDto.success("success");
    }
}
