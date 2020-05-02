package com.fox.api.service.quartz;

/**
 * 计划任务执行管理
 * @author lusongsong
 */
public interface QuartzJobManageService {
    /**
     * 启动任务
     * @param jobId
     * @return
     */
    Boolean startJob(Integer jobId);

    /**
     * 暂停任务
     * @param jobId
     * @return
     */
    Boolean pauseJob(Integer jobId);

    /**
     * 继续运行
     * @param jobId
     * @return
     */
    Boolean resumeJob(Integer jobId);

    /**
     * 终止运行
     * @param jobId
     * @return
     */
    Boolean stopJob(Integer jobId);

    /**
     * 加载所有计划任务
     */
    void loadTotalQuartzJob();
}
