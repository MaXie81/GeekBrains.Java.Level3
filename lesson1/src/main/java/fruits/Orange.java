package fruits;

public class Orange extends Fruit{
    Orange() {
        super(1.5f, "апельсин");
    }
    public static Orange[] getPortion(int size) {
        Orange[] arr = new Orange[size];
        for (int i = 0; i < arr.length; i++) arr[i] = new Orange();
        return arr;
    }
}
