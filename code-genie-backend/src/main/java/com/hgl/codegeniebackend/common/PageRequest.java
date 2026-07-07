package com.hgl.codegeniebackend.common;

import cn.hutool.core.util.StrUtil;
import com.hgl.codegeniebackend.common.exception.BusinessException;
import com.hgl.codegeniebackend.common.exception.ErrorCode;
import lombok.Data;

import java.util.Set;

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

    /**
     * 获取安全的排序字段（白名单校验，防止 SQL 注入）
     *
     * @param allowedFields 允许排序的字段白名单
     * @return 校验通过的排序字段，为空则返回 null
     */
    public String getSafeSortField(Set<String> allowedFields) {
        if (StrUtil.isBlank(sortField)) {
            return null;
        }
        if (!allowedFields.contains(sortField)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "非法排序字段: " + sortField);
        }
        return sortField;
    }

    /**
     * 排序方向是否为升序
     */
    public boolean isAscending() {
        return "ascend".equals(sortOrder);
    }
}

