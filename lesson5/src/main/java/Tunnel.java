import java.util.concurrent.Semaphore;

public class Tunnel extends Stage {
    private final Semaphore smpTonnel;

    public Tunnel(int trafficWidth) {
        this.length = 80;
        this.description = "Тоннель " + length + " метров";
        this.smpTonnel = new Semaphore(trafficWidth);
    }

    @Override
    public void go(Car c) {
        try {
            try {
                System.out.println(c.getName() + " готовится к этапу(ждет): " + description);
                smpTonnel.acquire();
                System.out.println(c.getName() + " начал этап: " + description);
                Thread.sleep(length / c.getSpeed() * 1_000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                System.out.println(c.getName() + " закончил этап: " + description);
                Thread.sleep(20);
                smpTonnel.release();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
