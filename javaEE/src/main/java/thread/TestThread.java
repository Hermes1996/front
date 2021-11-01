package thread;

import org.apache.commons.lang3.StringUtils;

public class TestThread
{
    public static void main(String[] args)
    {
        MyThread mt = new MyThread("新线程");
        mt.start();
        for (int i = 0; i < 5; i++)
        {
            System.out.println("main线程:" + i);
        }
    }

    public static class MyThread extends Thread
    {
        // 定义指定线程名称的构造方法
        public MyThread(String name)
        {
            super(name);
        }

        @Override
        public void run()
        {
            for (int j = 0; j < 5; j++)
            {
//                StringUtils.join影响线程？？
                System.out.println(getName() + ":" + j);
            }
        }
    }
}