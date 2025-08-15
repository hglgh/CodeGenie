package com.hgl.codegeniebackend.langgraph4j.ai;

import jakarta.annotation.Resource;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * @ClassName: ImageCollectionServiceTest
 * @Package: com.hgl.codegeniebackend.langgraph4j.ai
 * @Description:
 * @Author HGL
 * @Create: 2025/8/14 17:56
 */
@SpringBootTest
class ImageCollectionServiceTest {

    @Resource
    private ImageCollectionService imageCollectionService;

    @Test
    void testTechWebsiteImageCollection() {
        String result = imageCollectionService.collectImages("创建一个技术博客网站，需要展示编程教程和系统架构");
        Assertions.assertNotNull(result);
        System.out.println("技术网站收集到的图片: " + result);
    }

    @Test
    void testEcommerceWebsiteImageCollection() {
        String result = imageCollectionService.collectImages("创建一个电商购物网站，需要展示商品和品牌形象");
        Assertions.assertNotNull(result);
        System.out.println("电商网站收集到的图片: " + result);
    }
}
