package thread.pool;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import javax.annotation.Resource;

public class ThreadTest
{
//    controller层调用
//    @Resource(name = "taskExecutor")
//    private ThreadPoolTaskExecutor executor;

    public static void main(String[] args)
    {
        System.out.println("Hello World!");
        // 配置文件
        ApplicationContext appContext = new ClassPathXmlApplicationContext("applicationContext.xml");
        TaskExecutor executor = (TaskExecutor) appContext.getBean("taskExecutor");

        for (int i = 0; i < 10; i++)
        {
            SpringThread t = new SpringThread(i);
            executor.execute(t);
        }
        System.out.println("main process is finish .....");
    }
}
