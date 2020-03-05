package com.fox.api.service.stock.entity;

import lombok.Data;

@Data
public class PageInfo {
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
