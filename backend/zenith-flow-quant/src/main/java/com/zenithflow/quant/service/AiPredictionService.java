package com.zenithflow.quant.service;

import ai.onnxruntime.OnnxTensor;
import ai.onnxruntime.OrtEnvironment;
import ai.onnxruntime.OrtException;
import ai.onnxruntime.OrtSession;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.zenithflow.quant.mapper.AiModelMapper;
import com.zenithflow.quant.model.AiModel;
import com.zenithflow.commons.dynamic.datasource.annotation.DataSource;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Service;

import javax.annotation.PreDestroy;
import java.io.File;
import java.nio.FloatBuffer;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@DataSource("quant")
public class AiPredictionService implements ApplicationRunner {

    private final AiModelMapper aiModelMapper;

    private OrtEnvironment env;

    // 管理多个模型 Session: Map<ModelName, OrtSession>
    private final Map<String, OrtSession> sessionMap = new ConcurrentHashMap<>();

    private final Object lock = new Object();

    // 默认模型存放路径
    private static final String MODELS_DIR = "ai-engine/models";

    @Override
    public void run(ApplicationArguments args) {
        synchronized (lock) {
            try {
                // 初始化 ONNX 环境 (只需一次)
                this.env = OrtEnvironment.getEnvironment();
                loadAllModels();
            } catch (Exception e) {
                System.err.println("❌ AI 环境初始化失败: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    /**
     * 从数据库加载所有模型
     */
    public void loadAllModels() {
        synchronized (lock) {
            List<AiModel> models = aiModelMapper.selectList(null);

            System.out.println("🔍 从数据库查询到 " + models.size() + " 个模型记录，开始加载...");

            // 清理旧 Session
            closeAllSessions();

            if (models.isEmpty()) {
                // 尝试加载本地文件系统作为 fallback (兼容旧逻辑)
                System.out.println("⚠️ 数据库无模型记录，尝试扫描 models 目录...");
                scanLocalModels();
                return;
            }

            for (AiModel model : models) {
                try {
                    String path = model.getFilePath();
                    // 如果是相对路径，拼上 MODELS_DIR
                    // 假设 file_path 存的是文件名 "rf_v1.onnx" 或相对路径 "models/rf_v1.onnx"
                    File f = new File(path);
                    if (!f.exists()) {
                        f = new File(MODELS_DIR, path); // try models/filename
                    }
                    if (!f.exists()) {
                        f = new File("../" + MODELS_DIR, path); // try ../models/filename
                    }

                    if (f.exists()) {
                        System.out.println("   loading: " + model.getModelName() + " from " + f.getAbsolutePath());
                        OrtSession session = env.createSession(f.getAbsolutePath(), new OrtSession.SessionOptions());
                        sessionMap.put(model.getModelName(), session);
                    } else {
                        System.err.println("❌ 模型文件未找到: " + path);
                    }
                } catch (Exception e) {
                    System.err.println("❌ 加载模型失败 [" + model.getModelName() + "]: " + e.getMessage());
                }
            }

            System.out.println("✅ 成功加载 " + sessionMap.size() + " 个模型: " + sessionMap.keySet());
        }
    }

    private void scanLocalModels() {
        File dir = new File(MODELS_DIR);
        if (!dir.exists()) dir = new File("../" + MODELS_DIR);

        if (dir.exists() && dir.isDirectory()) {
            File[] files = dir.listFiles((d, name) -> name.endsWith(".onnx"));
            if (files != null) {
                for (File f : files) {
                    try {
                        String modelName = f.getName().replace(".onnx", "");
                        OrtSession session = env.createSession(f.getAbsolutePath(), new OrtSession.SessionOptions());
                        sessionMap.put(modelName, session);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    /**
     * 热重载指定模型 (或全部)
     */
    public void reload() {
        System.out.println(">>> 收到模型热重载请求...");
        loadAllModels();
    }

    /**
     * 预测下一日涨跌
     * @param modelName 模型名称
     * @param features float[] 特征向量
     * @return 1 (涨) 或 0 (跌)
     */
    public long predict(String modelName, float[] features) {
        synchronized (lock) {
            OrtSession session = sessionMap.get(modelName);

            // 容错：如果只有一个模型，默认使用
            if (session == null && sessionMap.size() == 1) {
                session = sessionMap.values().iterator().next();
            }

            if (session == null) {
                System.err.println("❌ 模型未找到: " + modelName);
                return 0;
            }

            try {
                long[] shape = new long[]{1, features.length};
                FloatBuffer buffer = FloatBuffer.wrap(features);
                OnnxTensor tensor = OnnxTensor.createTensor(env, buffer, shape);

                String inputName = session.getInputNames().iterator().next();
                Map<String, OnnxTensor> inputs = Collections.singletonMap(inputName, tensor);

                try (OrtSession.Result results = session.run(inputs)) {
                    long[] labels = (long[]) results.get(0).getValue();
                    return labels[0];
                }
            } catch (OrtException e) {
                e.printStackTrace();
                return 0;
            }
        }
    }

    /**
     * 获取可用模型列表 (从数据库查询详细信息)
     */
    public List<AiModel> getAvailableModelDetails() {
        List<AiModel> dbModels = aiModelMapper.selectList(new QueryWrapper<AiModel>().orderByDesc("created_at"));
        // 过滤掉那些虽然在数据库但加载失败的模型 (sessionMap 中不存在的)
        return dbModels.stream()
                .filter(m -> sessionMap.containsKey(m.getModelName()))
                .collect(Collectors.toList());
    }

    // 兼容旧接口
    public Set<String> getAvailableModels() {
        return sessionMap.keySet();
    }

    private void closeAllSessions() {
        for (OrtSession s : sessionMap.values()) {
            try {
                s.close();
            } catch (OrtException e) {
                e.printStackTrace();
            }
        }
        sessionMap.clear();
    }

    @PreDestroy
    public void close() {
        synchronized (lock) {
            closeAllSessions();
            try {
                if (env != null) env.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
