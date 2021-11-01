package thread;

public class TestRunnable
{
    public static void main(String[] args)
    {
//        MyRunnable mr = new MyRunnable();
//        Thread t = new Thread(mr, "新线程");
//        t.start();

//        Lambda表达式 + 匿名内部类
        new Thread(()->{
            for (int j = 0; j < 5; j++)
            {
                System.out.println(Thread.currentThread().getName()+ ":" + j);
            }
        }).start();

        for (int i = 0; i < 5; i++)
        {
            System.out.println("main线程:" + i);
        }
    }
}

//class MyRunnable implements Runnable{
//    @Override
//    public void run()
//    {
//        for (int j = 0; j < 5; j++)
//        {
//            System.out.println(Thread.currentThread().getName()+ ":" + j);
//        }
//    }
//}