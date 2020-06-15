package com.fox.api.controller.api.admin;

import com.fox.api.dao.admin.entity.SignalEntity;
import com.fox.api.dao.quartz.entity.QuartzJobEntity;
import com.fox.api.entity.dto.result.ResultDto;
import com.fox.api.entity.po.PageInfoPo;
import com.fox.api.service.admin.Signal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 信号
 * @author lusongsong
 */
@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/admin/signal")
public class SignalController {
    @Autowired
    private Signal signal;

    /**
     * 添加信号
     * @param signalEntity
     * @return
     */
    @RequestMapping("/create")
    public ResultDto create(SignalEntity signalEntity) {
        Integer jobId = signal.insert(signalEntity);
        return ResultDto.success(jobId);
    }

    /**
     * 信号列表
     * @param startId
     * @param pageInfoPo
     * @return
     */
    @RequestMapping("/getList")
    public ResultDto create(Integer startId, PageInfoPo pageInfoPo) {
        List<SignalEntity> list = signal.getList(startId, pageInfoPo);
        return ResultDto.success(list);
    }
}
