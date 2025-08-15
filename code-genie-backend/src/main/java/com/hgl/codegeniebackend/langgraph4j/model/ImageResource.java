package com.hgl.codegeniebackend.langgraph4j.model;

import com.hgl.codegeniebackend.langgraph4j.model.enums.ImageCategoryEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

/**
 * @ClassName: ImageResource
 * @Package: com.hgl.codegeniebackend.langgraph4j.state
 * @Description: 图片资源对象
 * @Author HGL
 * @Create: 2025/8/13 15:54
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ImageResource implements Serializable {

    /**
     * 图片类别
     */
    private ImageCategoryEnum category;

    /**
     * 图片描述
     */
    private String description;

    /**
     * 图片地址
     */
    private String url;

    @Serial
    private static final long serialVersionUID = 1L;
}
