import fruits.Apple;
import fruits.Orange;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class Lesson1 {
    static <T> void method1(T[] arr) {
        try {
            T val = arr[0];
            arr[0] = arr[1];
            arr[1] = val;
        } catch (ArrayIndexOutOfBoundsException e) {
            System.out.println("Ошибка при обработке массива!");
        }
    }
    static <T> void printArr(T[] arr) {
        for (int i = 0; i < arr.length; i++)
            System.out.print(arr[i] + " ");
        System.out.println();
    }

    static <T> ArrayList<T> method2(T[] arr) {
        ArrayList<T> arrLst = new ArrayList<>();
        for (T val: arr) {
            arrLst.add(val);
        }
//        arrLst.addAll(Arrays.asList(arr));    -- более простая альтернатива
        return arrLst;
    }

    public static void main(String[] args) {
        // Проверка корректности выполнения п.1 и 2
        Integer[] arrIntegers;
        String[] arrStrings;

        arrIntegers = new Integer[]{1, 2};
        arrStrings = new String[]{"A", "B"};

        method1(arrIntegers);
        method1(arrStrings);

        printArr(arrIntegers);
        printArr(arrStrings);

        List<Integer> lstIntegers = method2(arrIntegers);
        List<String> lstStrings = method2(arrStrings);

        System.out.println(lstIntegers);
        System.out.println(lstStrings);

        // Проверка корректности выполнения п.3
        Box<Apple> boxApple1 = new Box<>();
        Box<Apple> boxApple2 = new Box<>();

        Box<Orange> boxOrange1 = new Box<>();
        Box<Orange> boxOrange2 = new Box<>();

        boxApple1.add(Apple.getPortion(4));
        boxApple2.add(Apple.getPortion(2));
        boxOrange1.add(Orange.getPortion(1));
        boxOrange2.add(Orange.getPortion(1));
        boxOrange2.add(Orange.getPortion(2));

        System.out.println(boxApple1);
        System.out.println(boxApple2);
        System.out.println(boxOrange1);
        System.out.println(boxOrange2);

        System.out.println(boxOrange1.compare(boxApple1));
        System.out.println(boxOrange2.compare(boxApple2));

        boxApple1.empty(boxApple2);
        System.out.println("яблоки пересыпали из box1 в box2");

        System.out.println(boxApple1);
        System.out.println(boxApple2);
        System.out.println(boxOrange1);
        System.out.println(boxOrange2);

        System.out.println(boxOrange1.compare(boxApple1));
        System.out.println(boxOrange2.compare(boxApple2));

        boxOrange1.empty(boxOrange2);
        System.out.println("апельсины пересыпали из box1 в box2");

        System.out.println(boxApple1);
        System.out.println(boxApple2);
        System.out.println(boxOrange1);
        System.out.println(boxOrange2);

        System.out.println(boxOrange1.compare(boxApple1));
        System.out.println(boxOrange2.compare(boxApple2));
    }
}
