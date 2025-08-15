package com.hgl.codegeniebackend.langgraph4j.tools;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.hgl.codegeniebackend.langgraph4j.model.ImageResource;
import com.hgl.codegeniebackend.langgraph4j.model.enums.ImageCategoryEnum;
import dev.langchain4j.agent.tool.P;
import dev.langchain4j.agent.tool.Tool;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * @ClassName: ImageSearchTool
 * @Package: com.hgl.codegeniebackend.langgraph4j.tools
 * @Description:  图片搜索工具 （根据关键词搜索内容图片）
 * @Author HGL
 * @Create: 2025/8/14 15:48
 */
@Slf4j
@Component
public class ImageSearchTool {

    private static final String PEXELS_API_URL = "https://api.pexels.com/v1/search";

    @Value("${pexels.api-key}")
    private String pexelsApiKey;

    @Tool("搜索内容相关的图片，用于网站内容展示")
    public List<ImageResource> searchContentImages(@P("搜索关键词") String query) {
        int searchCount = 12;
        // 调用 API，注意释放资源
        try (HttpResponse response = HttpRequest.get(PEXELS_API_URL)
                .header("Authorization", pexelsApiKey)
                .form("query", query)
                .form("per_page", String.valueOf(searchCount))
                .form("page", "1")
                .execute()
        ) {
            if (response.isOk()) {
                JSONObject result = JSONUtil.parseObj(response.body());
                return result.getJSONArray("photos").stream().map(item -> {
                    JSONObject photo = (JSONObject) item;
                    JSONObject src = photo.getJSONObject("src");
                    return ImageResource.builder()
                            .url(src.getStr("medium"))
                            .description(photo.getStr("alt", query))
                            .category(ImageCategoryEnum.CONTENT)
                            .build();
                }).toList();
            }
        } catch (Exception e) {
            log.error("Pexels API 调用失败: {}", e.getMessage(), e);
        }
        return new ArrayList<>();
    }
}
