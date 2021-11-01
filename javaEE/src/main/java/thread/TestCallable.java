package thread;

import java.util.concurrent.*;

// callable可以有返回值，也可以抛出异常
public class TestCallable
{

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        ExecutorService executorService = Executors.newCachedThreadPool();
        Mycallable mc = new Mycallable();
        Future<Long> future = executorService.submit(mc);
        System.out.println("future: " + future.get()); //get会阻塞
        executorService.shutdown();
    }

    public static class Mycallable implements Callable<Long>{
        @Override
        public Long call() throws Exception
        {
            long sum = 0;
            for (int j = 0; j < 5; j++)
            {
                sum +=j;
            }
            return sum;
        }
    }
}
