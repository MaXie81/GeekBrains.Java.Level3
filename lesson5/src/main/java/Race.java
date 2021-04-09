import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.CyclicBarrier;

public class Race {
    private ArrayList<Stage> stages;
    private final int carCount;
    private String carWinName;
    private final CyclicBarrier barrierStartFinish;
    private final CountDownLatch lockWin = new CountDownLatch(1);

    public ArrayList<Stage> getStages() { return stages; }
    public void setBarrierStartFinish() {
        try {
            barrierStartFinish.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (BrokenBarrierException e) {
            e.printStackTrace();
        }
    }
    synchronized public void finish(String carWinName) {
        if (lockWin.getCount() > 0) {
            this.carWinName = carWinName;
            lockWin.countDown();
        }
    }

    public Race(int carCount, Stage... stages) {
        this.carCount = carCount;
        this.stages = new ArrayList<>(Arrays.asList(stages));
        this.barrierStartFinish = new CyclicBarrier(this.carCount + 1);

        System.out.println("ВАЖНОЕ ОБЪЯВЛЕНИЕ >>> Подготовка!!!");
        new Thread(() -> {
            try {
                barrierStartFinish.await();
                System.out.println("ВАЖНОЕ ОБЪЯВЛЕНИЕ >>> Гонка началась!!!");
                lockWin.await();
                System.out.println(carWinName + " - WIN");
                barrierStartFinish.await();
                System.out.println("ВАЖНОЕ ОБЪЯВЛЕНИЕ >>> Гонка закончилась!!!");
            } catch (InterruptedException | BrokenBarrierException e) {
                e.printStackTrace();
            }
        }).start();
    }
}
