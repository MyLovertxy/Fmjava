package com.fmjava.core.pojo.entity;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;
@Getter@Setter
public class PageResult implements Serializable {
    private Long total;//总记录数
    private List rows;//当前页的数据

    public PageResult(Long total, List rows) {
        this.total = total;
        this.rows = rows;
    }
}
