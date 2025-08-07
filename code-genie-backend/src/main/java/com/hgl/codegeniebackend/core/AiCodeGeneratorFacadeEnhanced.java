package com.hgl.codegeniebackend.core;

import cn.hutool.json.JSONUtil;
import com.hgl.codegeniebackend.ai.AiCodeGeneratorService;
import com.hgl.codegeniebackend.ai.AiCodeGeneratorServiceFactory;
import com.hgl.codegeniebackend.ai.enums.CodeGenTypeEnum;
import com.hgl.codegeniebackend.ai.model.HtmlCodeResult;
import com.hgl.codegeniebackend.ai.model.MultiFileCodeResult;
import com.hgl.codegeniebackend.ai.model.message.AiResponseMessage;
import com.hgl.codegeniebackend.ai.model.message.ToolExecutedMessage;
import com.hgl.codegeniebackend.ai.model.message.ToolRequestMessage;
import com.hgl.codegeniebackend.common.exception.BusinessException;
import com.hgl.codegeniebackend.common.exception.ErrorCode;
import com.hgl.codegeniebackend.common.exception.ThrowUtils;
import com.hgl.codegeniebackend.core.parser.CodeParserExecutor;
import com.hgl.codegeniebackend.core.saver.CodeFileSaverExecutor;
import dev.langchain4j.service.TokenStream;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.io.File;

/**
 * ClassName: AiCodeGeneratorFacadeEnhanced
 * Package: com.hgl.codegeniebackend.core
 * Description:
 *
 * @Author HGL
 * @Create: 2025/7/30 17:13
 */
@Slf4j
@Service
public class AiCodeGeneratorFacadeEnhanced {

    @Resource
    private AiCodeGeneratorServiceFactory aiCodeGeneratorServiceFactory;

    /**
     * 统一入口：根据类型生成并保存代码 (使用appId)
     *
     * @param userMessage     用户提示词
     * @param codeGenTypeEnum 生成类型
     * @return 保存的目录
     */
    public File generateAndSaveCode(String userMessage, CodeGenTypeEnum codeGenTypeEnum, Long appId) {

        ThrowUtils.throwIf(codeGenTypeEnum == null, ErrorCode.PARAMS_ERROR, "未指定生成类型");
        AiCodeGeneratorService aiCodeGeneratorService = getAiCodeGeneratorService(appId);
        return switch (codeGenTypeEnum) {
            case HTML -> {
                HtmlCodeResult htmlCodeResult = aiCodeGeneratorService.generateHtmlCode(userMessage);
                yield CodeFileSaverExecutor.executeSaver(htmlCodeResult, CodeGenTypeEnum.HTML, appId);
            }
            case MULTI_FILE -> {
                MultiFileCodeResult multiFileCodeResult = aiCodeGeneratorService.generateMultiFileCode(userMessage);
                yield CodeFileSaverExecutor.executeSaver(multiFileCodeResult, CodeGenTypeEnum.MULTI_FILE, appId);
            }
            default -> {
                String errorMessage = "不支持的生成类型：" + codeGenTypeEnum.getValue();
                throw new BusinessException(ErrorCode.SYSTEM_ERROR, errorMessage);
            }
        };
    }

    /**
     * 统一入口：根据类型生成并保存代码(流式,使用appId)
     *
     * @param userMessage 用户提示词
     * @param codeGenType 生成类型
     */
    public Flux<String> generateAndSaveCodeStream(String userMessage, CodeGenTypeEnum codeGenType, Long appId) {
        ThrowUtils.throwIf(codeGenType == null, ErrorCode.PARAMS_ERROR, "未指定生成类型");
        AiCodeGeneratorService aiCodeGeneratorService = getAiCodeGeneratorService(appId, codeGenType);
        return switch (codeGenType) {
            case HTML -> {
                Flux<String> codeStream = aiCodeGeneratorService.generateHtmlCodeStream(userMessage);
                yield processCodeStream(codeStream, CodeGenTypeEnum.HTML, appId);
            }
            case MULTI_FILE -> {
                Flux<String> codeStream = aiCodeGeneratorService.generateMultiFileCodeStream(userMessage);
                yield processCodeStream(codeStream, CodeGenTypeEnum.MULTI_FILE, appId);
            }
            case VUE_PROJECT -> {
                TokenStream tokenStream = aiCodeGeneratorService.generateVueProjectCodeStream(appId, userMessage);
                Flux<String> codeStream = processTokenStream(tokenStream);
                yield processCodeStream(codeStream, CodeGenTypeEnum.MULTI_FILE, appId);
            }
            default -> {
                String errorMessage = "不支持的生成类型：" + codeGenType.getValue();
                throw new BusinessException(ErrorCode.SYSTEM_ERROR, errorMessage);
            }
        };
    }

    /**
     * 通用流式代码处理方法 (使用appId)
     *
     * @param codeStream  代码流
     * @param codeGenType 代码生成类型
     * @return 流式响应
     */
    private Flux<String> processCodeStream(Flux<String> codeStream, CodeGenTypeEnum codeGenType, Long appId) {
        StringBuilder codeBuilder = new StringBuilder();
        return codeStream
                // 实时收集代码片段
                .doOnNext(codeBuilder::append)
                .doOnComplete(() -> {
                    // 流式返回完成后保存代码
                    try {
                        // 使用执行器解析代码
                        Object codeResult = CodeParserExecutor.executeParser(codeBuilder.toString(), codeGenType);
                        // 使用执行器保存代码
                        File savedDir = CodeFileSaverExecutor.executeSaver(codeResult, codeGenType, appId);
                        log.info("保存成功，路径为：{}", savedDir.getAbsolutePath());
                    } catch (Exception e) {
                        log.error("保存失败: {}", e.getMessage());
                    }
                })
                .doOnError(throwable -> log.error("保存代码失败：{}", throwable.getMessage())
                );
    }

    /**
     * 将 TokenStream 转换为 Flux<String>，并传递工具调用信息
     *
     * @param tokenStream TokenStream 对象
     * @return Flux<String> 流式响应
     */
    private Flux<String> processTokenStream(TokenStream tokenStream) {
        return Flux.create(sink -> tokenStream.onPartialResponse(partialResponse -> {
            AiResponseMessage aiResponseMessage = new AiResponseMessage(partialResponse);
            sink.next(JSONUtil.toJsonStr(aiResponseMessage));
        }).onPartialToolExecutionRequest((index, toolExecutionRequest) -> {
            ToolRequestMessage toolRequestMessage = new ToolRequestMessage(toolExecutionRequest);
            sink.next(JSONUtil.toJsonStr(toolRequestMessage));
        }).onToolExecuted(toolExecution -> {
            ToolExecutedMessage toolExecutedMessage = new ToolExecutedMessage(toolExecution);
            sink.next(JSONUtil.toJsonStr(toolExecutedMessage));
        }).onCompleteResponse(chatResponse -> {
            sink.complete();
        }).onError(throwable -> {
            log.error("processTokenStream方法 Error: ", throwable);
            sink.error(throwable);
        }).start());
    }

    /**
     * 获取AI代码生成服务实例
     *
     * @param appId 应用ID
     * @return AI代码生成服务实例
     */
    private AiCodeGeneratorService getAiCodeGeneratorService(Long appId, CodeGenTypeEnum codeGenType) {
        return aiCodeGeneratorServiceFactory.getAiCodeGeneratorService(appId, codeGenType);
    }

    private AiCodeGeneratorService getAiCodeGeneratorService(Long appId) {
        return aiCodeGeneratorServiceFactory.getAiCodeGeneratorService(appId);
    }
}
