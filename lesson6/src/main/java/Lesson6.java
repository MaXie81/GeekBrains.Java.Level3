import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class Lesson6 {
    static class ExceptionEmptyArr extends RuntimeException {
        public ExceptionEmptyArr() {
            super("Массив не содержит элементов");
        }
    }
    static class ExceptionNotInclude4 extends RuntimeException {
        public ExceptionNotInclude4() {
            super("Массив не содержит элементов со значением 4");
        }
    }
    public static Integer[] get4Arr(Integer[] arr) throws ExceptionEmptyArr, ExceptionNotInclude4 {
        if (arr.length == 0) throw new ExceptionEmptyArr();
        LinkedList<Integer> lst = new LinkedList<>();
        for (int i = arr.length - 1; i >= 0; i--) {
            if (arr[i] != 4)
                lst.addFirst(arr[i]);
            else
                return lst.toArray(new Integer[0]);
        }
        throw new ExceptionNotInclude4();
    }
    public static boolean check14Arr(Integer[] arr) {
        List<Integer> lst = new ArrayList<>(Arrays.asList(arr));
        return (lst.contains(1) && lst.contains(4) ? true : false);
    }
}
