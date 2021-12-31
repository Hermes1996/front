package thread;

public class SoldTickets
{
    public static void main(String[] args)
    {
        // 创建了两个对象
//        new MyThread().start();
//        new MyThread().start();
        MyThread myThread = new MyThread();
        new Thread(myThread,"t1").start();
        new Thread(myThread,"t2").start();


    }

    static class MyThread extends Thread
    {
        private int ticket = 100;

        public void run()
        {
            while (true)
            {
                // 加锁不会乱序
                synchronized (this)
                {
                    System.out.println("Thread ticket = " + ticket--);
                    if (ticket < 0)
                    {
                        break;
                    }
                }
            }
        }
    }
}
