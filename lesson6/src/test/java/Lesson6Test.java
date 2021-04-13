import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class Lesson6Test {

    @Test
    void get4Arr() {
        Integer[] arr1 = new Integer[]{1, 2, 3, 4, 5, 6, 7, 4, 8, 9};
        Integer[] arr2 = new Integer[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 4};
        Integer[] arr3 = new Integer[]{1, 2, 3, 1, 5, 6, 7, 8, 7, 9};
        Integer[] arr4 = new Integer[]{1, 2, 3, 4, 5, 6, 7, 4, 8, 9};
        Integer[] arr5 = new Integer[]{};

        Integer[] arrResult1 = new Integer[]{8, 9};
        Integer[] arrResult2 = new Integer[]{};

        Assertions.assertArrayEquals(arrResult1, Lesson6.get4Arr(arr1));
        Assertions.assertArrayEquals(arrResult2, Lesson6.get4Arr(arr2));
        Assertions.assertThrows(Lesson6.ExceptionNotInclude4.class, () -> Lesson6.get4Arr(arr3));
        Assertions.assertDoesNotThrow(() -> Lesson6.get4Arr(arr4));
        Assertions.assertThrows(Lesson6.ExceptionEmptyArr.class, () -> Lesson6.get4Arr(arr5));
    }

    @Test
    void check14Arr() {
        Integer[] arr1 = new Integer[]{1, 4, 1, 1, 1};
        Integer[] arr2 = new Integer[]{1, 1, 1};
        Integer[] arr3 = new Integer[]{4, 4, 4, 4};
        Integer[] arr4 = new Integer[]{4, 4, 4, 4, 1};

        Assertions.assertTrue(Lesson6.check14Arr(arr1));
        Assertions.assertFalse(Lesson6.check14Arr(arr2));
        Assertions.assertFalse(Lesson6.check14Arr(arr3));
        Assertions.assertTrue(Lesson6.check14Arr(arr4));
    }
}