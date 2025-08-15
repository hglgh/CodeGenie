package com.hgl.codegeniebackend.langgraph4j.config;

import com.hgl.codegeniebackend.langgraph4j.CodeGenWorkflow;
import org.bsc.langgraph4j.GraphStateException;
import org.bsc.langgraph4j.studio.springboot.AbstractLangGraphStudioConfig;
import org.bsc.langgraph4j.studio.springboot.LangGraphFlow;
import org.springframework.context.annotation.Configuration;

/**
 * @ClassName: LangGraphStudioSampleConfig
 * @Package: com.hgl.codegeniebackend.langgraph4j.config
 * @Description:  可视化调试配置 （实际上很难使用）
 * @Author HGL
 * @Create: 2025/8/15 15:21
 */
@Configuration
public class LangGraphStudioSampleConfig extends AbstractLangGraphStudioConfig {

    final LangGraphFlow flow;

    public LangGraphStudioSampleConfig() throws GraphStateException {
        var workflow = new CodeGenWorkflow().createWorkflow().stateGraph;
        // define your workflow
        this.flow = LangGraphFlow.builder()
                .title("LangGraph Studio")
                .stateGraph(workflow)
                .build();
    }

    @Override
    public LangGraphFlow getFlow() {
        return this.flow;
    }
}
