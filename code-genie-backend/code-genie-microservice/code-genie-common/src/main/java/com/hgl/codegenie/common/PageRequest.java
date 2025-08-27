package com.hgl.codegenie.common;

import lombok.Data;

/**
 * ClassName: PageRequest
 * Package: com.hgl.codegeniebackend.common
 * Description:
 *
 * @Author HGL
 * @Create: 2025/7/25 14:11
 */
@Data
public class PageRequest {

    /**
     * 当前页号
     */
    private int pageNum = 1;

    /**
     * 页面大小
     */
    private int pageSize = 10;

    /**
     * 排序字段
     */
    private String sortField;

    /**
     * 排序顺序（默认降序）
     */
    private String sortOrder = "descend";
}

