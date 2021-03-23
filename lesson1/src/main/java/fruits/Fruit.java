package fruits;

public class Fruit {
    private final float WEIGHT;
    private final String NAME;
    Fruit(float weight, String name) {
        WEIGHT = weight;
        NAME = name;
    }
    public float getWEIGHT() {
        return WEIGHT;
    }

    @Override
    public String toString() {
        return NAME;
    }
}
