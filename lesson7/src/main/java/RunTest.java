import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

public class RunTest {
    private static TreeMap<Integer, List<Method>> mapMethod = new TreeMap<>();

    public static void start(String className) throws ClassNotFoundException, NoSuchMethodException, InstantiationException, IllegalAccessException, InvocationTargetException {
        start(Class.forName(className));
    }
    public static void start(Class clazz) throws NoSuchMethodException, ClassNotFoundException, IllegalAccessException, InvocationTargetException, InstantiationException {
        List<Method> mainListMethod = new ArrayList<>(Arrays.asList(clazz.getDeclaredMethods()));

        for (Method method : mainListMethod) {
            for (BeforeSuite annotaton : method.getAnnotationsByType(BeforeSuite.class)) {
                addMapMethod(0, method);
            }
            for (Test annotaton : method.getAnnotationsByType(Test.class)) {
                addMapMethod(annotaton.priority(), method);
            }
            for (AfterSuite annotaton : method.getAnnotationsByType(AfterSuite.class)) {
                addMapMethod(11, method);
            }
        }

        if (mapMethod.containsKey(0) && mapMethod.get(0).size() > 1) throw new RuntimeException("Класс тестирования содержит более одного метода с аннотацией @BeforeSuite");
        if (mapMethod.containsKey(11) && mapMethod.get(11).size() > 1) throw new RuntimeException("Класс тестирования содержит более одного метода с аннотацией @AfterSuite");

        Constructor constructor = clazz.getConstructor();
        Object obj = constructor.newInstance();

        for (Map.Entry pairPriorityMethods : mapMethod.entrySet()) {
            for (Method method : (List<Method>) pairPriorityMethods.getValue() ) {
                method.invoke(constructor.newInstance());
            }
        }
    }
    private static void addMapMethod(Integer key, Method val) {
        List<Method> list = mapMethod.containsKey(key) ? mapMethod.get(key) : new ArrayList<>();
        list.add(val);
        mapMethod.put(key, list);
    }
}
