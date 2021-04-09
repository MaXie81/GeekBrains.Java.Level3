public class MainClass {
    public static final int CARS_COUNT = 4;

    public static void main(String[] args) {
        Race race = new Race(CARS_COUNT, new Road(60), new Tunnel((int) (CARS_COUNT / 2)), new Road(40));
        Car[] cars = new Car[CARS_COUNT];
        for (int i = 0; i < cars.length; i++) {
            cars[i] = new Car(race, 20 + (int) (Math.random() * 10));
        }
        for (int i = 0; i < cars.length; i++) {
            new Thread(cars[i]).start();
        }
    }
}
