package thread.pool;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.ThreadPoolExecutor;

@Configuration
public class XxlJobConfig
{

    @Bean
    public TaskExecutor jobTaskExecutor()
    {
        // 创建线程池的对象
        ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();

        // 核心线程
        taskExecutor.setCorePoolSize(500);

        // 最大线程
        taskExecutor.setMaxPoolSize(1000);

        // 队列大小
        taskExecutor.setQueueCapacity(1000);

        // 空闲时间
        taskExecutor.setKeepAliveSeconds(300);

        // 拒绝策略
        taskExecutor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());

        // 允许核心线程超时
        taskExecutor.setAllowCoreThreadTimeOut(true);

        return taskExecutor;
    }
}
