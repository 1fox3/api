package com.fox.api.controller.api.admin.quartz;

import com.fox.api.dao.quartz.entity.QuartzJobEntity;
import com.fox.api.entity.dto.result.ResultDto;
import com.fox.api.service.quartz.QuartzJobService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 处理跨域问题
 */
@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/admin/quartz/job")
/**
 * 计划任务管理
 * @author lusongsong
 */
public class QuartzJobController {

    @Autowired
    private QuartzJobService quartzJobService;

    @RequestMapping("/getListByGroup")
    public ResultDto getListByGroup(String jobGroup) {
        return ResultDto.success(quartzJobService.getListByGroup(jobGroup));
    }

    @RequestMapping("/info")
    public ResultDto info(Integer jobId) {
        return ResultDto.success(quartzJobService.getById(jobId));
    }

    @RequestMapping("/create")
    public ResultDto create(QuartzJobEntity quartzJobEntity) {
        Integer jobId = quartzJobService.insert(quartzJobEntity);
        return ResultDto.success(jobId);
    }

    @RequestMapping("/update")
    public ResultDto update(QuartzJobEntity quartzJobEntity) {
        return ResultDto.success(quartzJobService.updateJob(quartzJobEntity));
    }

    @RequestMapping("/delete")
    public ResultDto delete(Integer jobId) {
        return ResultDto.success(quartzJobService.deleteJob(jobId));
    }
}
