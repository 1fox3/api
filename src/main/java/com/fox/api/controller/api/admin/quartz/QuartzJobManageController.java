package com.fox.api.controller.api.admin.quartz;

import com.fox.api.entity.dto.result.ResultDto;
import com.fox.api.service.quartz.QuartzJobManageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 处理跨域问题
 */
@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/admin/quartz/jobManage")
/**
 * 计划任务执行管理
 * @author lusongsong
 */
public class QuartzJobManageController {
    @Autowired
    private QuartzJobManageService quartzJobManageService;

    @RequestMapping("/start")
    public ResultDto start(Integer jobId) {
        return ResultDto.success(quartzJobManageService.startJob(jobId));
    }

    @RequestMapping("/pause")
    public ResultDto pause(Integer jobId) {
        return ResultDto.success(quartzJobManageService.pauseJob(jobId));
    }

    @RequestMapping("/resume")
    public ResultDto resume(Integer jobId) {
        return ResultDto.success(quartzJobManageService.resumeJob(jobId));
    }

    @RequestMapping("/stop")
    public ResultDto stop(Integer jobId) {
        return ResultDto.success(quartzJobManageService.stopJob(jobId));
    }

}
