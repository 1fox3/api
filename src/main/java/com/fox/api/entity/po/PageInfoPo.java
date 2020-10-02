package com.fox.api.entity.po;

import lombok.Data;

/**
 * 翻页信息
 * @author lusongsong
 * @date 2020/3/5 18:13
 */
@Data
public class PageInfoPo {
    private Integer pageNum;
    private Integer pageSize;

    public Integer getPageNum() {
        return null == pageNum || pageNum < 1 ? 1 : pageNum;
    }

    public Integer getPageSize() {
        return null == pageSize || pageSize < 1 ? 10 : pageSize;
    }

    public String getLimitStr() {
        return ((this.getPageNum() - 1) * this.getPageSize()) + "," + this.getPageSize();
    }
}
