package com.fox.api.controller.api.admin.quartz;

import com.fox.api.dao.quartz.entity.QuartzJobParamEntity;
import com.fox.api.entity.dto.result.ResultDto;
import com.fox.api.service.quartz.QuartzJobParamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 处理跨域问题
 */
@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/admin/quartz/jobParam")
/**
 * 计划任务参数管理
 * @author lusongsong
 */
public class QuartzJobParamController {

    @Autowired
    private QuartzJobParamService quartzJobParamService;

    @RequestMapping("/getByJob")
    public ResultDto getByGroup(Integer jobId) {
        return ResultDto.success(quartzJobParamService.getByJob(jobId));
    }

    @RequestMapping("/info")
    public ResultDto info(Integer jobParamId) {
        return ResultDto.success(quartzJobParamService.getById(jobParamId));
    }

    @RequestMapping("/create")
    public ResultDto create(QuartzJobParamEntity quartzJobParamEntity) {
        Integer jobId = quartzJobParamService.insert(quartzJobParamEntity);
        return ResultDto.success(jobId);
    }

    @RequestMapping("/update")
    public ResultDto update(QuartzJobParamEntity quartzJobParamEntity) {
        return ResultDto.success(quartzJobParamService.update(quartzJobParamEntity));
    }

    @RequestMapping("/delete")
    public ResultDto delete(Integer jobParamId) {
        return ResultDto.success(quartzJobParamService.delete(jobParamId));
    }
}
