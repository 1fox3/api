package com.fox.api.service.quartz.impl;

import com.fox.api.dao.quartz.entity.QuartzJobParamEntity;
import com.fox.api.dao.quartz.mapper.QuartzJobParamMapper;
import com.fox.api.exception.self.ServiceException;
import com.fox.api.service.quartz.QuartzJobParamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.LinkedList;
import java.util.List;

/**
 * 任务参数管理服务实现
 * @author lusongsong
 */
@Service
public class QuartzJobParamImpl implements QuartzJobParamService {
    @Autowired
    private QuartzJobParamMapper quartzJobParamMapper;

    @Override
    public Integer insert(QuartzJobParamEntity quartzJobParamEntity) {
        try {
            return this.quartzJobParamMapper.insert(quartzJobParamEntity);
        } catch (Exception e) {
            throw new ServiceException(1, e.getMessage());
        }
    }

    @Override
    public Boolean update(QuartzJobParamEntity quartzJobParamEntity) {
        try {
            return this.quartzJobParamMapper.update(quartzJobParamEntity);
        } catch (Exception e) {
            throw new ServiceException(1, e.getMessage());
        }
    }

    @Override
    public Boolean delete(Integer id) {
        try {
            return this.quartzJobParamMapper.delete(id);
        } catch (Exception e) {
            throw new ServiceException(1, e.getMessage());
        }
    }

    @Override
    public List<QuartzJobParamEntity> getByJob(Integer jobId) {
        try {
            return this.quartzJobParamMapper.getByJobId(jobId);
        } catch (Exception e) {
            throw new ServiceException(1, e.getMessage());
        }
    }

    /**
     * 获取任务执行参数
     *
     * @param jobId
     * @return
     */
    @Override
    public List getJobParams(Integer jobId) {
        try {
            List<QuartzJobParamEntity> list = this.getByJob(jobId);
            List paramList = new LinkedList<>();
            if (0 == list.size()) {
                return paramList;
            }
            for (QuartzJobParamEntity quartzJobParamEntity : list) {
                switch (quartzJobParamEntity.getParamType()) {
                    case "Integer":
                        paramList.add(Integer.valueOf(quartzJobParamEntity.getParamValue()));
                        break;
                    default:
                        paramList.add(String.valueOf(quartzJobParamEntity.getParamValue()));
                        break;
                }
            }
            return paramList;
        } catch (Exception e) {
            throw new ServiceException(1, e.getMessage());
        }
    }

    @Override
    public QuartzJobParamEntity getById(Integer id) {
        try {
            return this.quartzJobParamMapper.getById(id);
        } catch (Exception e) {
            throw new ServiceException(1, e.getMessage());
        }
    }
}
