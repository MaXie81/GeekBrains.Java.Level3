package fruits;

public class Apple extends Fruit{
    Apple() {
        super(1.0f, "яблоко");
    }
    public static Apple[] getPortion(int size) {
        Apple[] arr = new Apple[size];
        for (int i = 0; i < arr.length; i++) arr[i] = new Apple();
        return arr;
    }
}
