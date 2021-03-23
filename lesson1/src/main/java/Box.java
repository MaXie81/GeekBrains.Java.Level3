import fruits.Fruit;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

class Box<T extends Fruit>{
    List<T> cells;
    Box(){
        cells = new ArrayList<>();
    }
    void add(T[] arr) {
        cells.addAll(Arrays.asList(arr));
    }
    void add(List<T> cells) {
        this.cells.addAll(cells);
    }
    float getWeight() {
        float sum = 0;
        for (T fruit: cells) {
            sum += fruit.getWEIGHT();
        }
        return sum;
    }
    boolean compare(Box<? extends Fruit> box) {
        return getWeight() == box.getWeight();
    }
    void empty(Box<T> box) {
        box.add(cells);
        cells.clear();
    }

    @Override
    public String toString() {
        return "в коробке: " + cells;
    }
}
