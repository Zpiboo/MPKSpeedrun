package io.github.zpiboo.mpkspeedrun.util;

import io.github.kurrycat.mpkmod.Main;
import io.github.kurrycat.mpkmod.gui.infovars.InfoTree;
import io.github.kurrycat.mpkmod.gui.infovars.InfoVar;
import io.github.kurrycat.mpkmod.util.ClassUtil;
import io.github.kurrycat.mpkmod.util.StringUtil;
import io.github.kurrycat.mpkmod.util.Tuple;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Collectors;

import static io.github.zpiboo.mpkspeedrun.MPKSpeedrun.LOGGER;

/**
 * Temporary solution to remedy the fact that modules
 * don't have access to the classes.txt file
 */
public class InfoString {
    private static final Method recursiveSearch;

    static {
        try {
            recursiveSearch = io.github.kurrycat.mpkmod.gui.infovars.InfoString.class
                    .getDeclaredMethod("recursiveSearch",
                            InfoVar.class, List.class, List.class
                    );
            recursiveSearch.setAccessible(true);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    public static void loadInfoVars(Class<?> clazz) {
        loadInfoVars(Collections.singletonList(clazz));
    }

    public static void loadInfoVars(List<Class<?>> classes) {
        InfoTree infoMap = Main.infoTree;

        HashMap<Class<?>, List<Object>> accessInstances = new HashMap<>();
        List<Tuple<io.github.kurrycat.mpkmod.gui.infovars.InfoString.AccessInstance, Field>> accessInstanceAnnotations = ClassUtil.getFieldAnnotations(classes, io.github.kurrycat.mpkmod.gui.infovars.InfoString.AccessInstance.class);
        for (Tuple<io.github.kurrycat.mpkmod.gui.infovars.InfoString.AccessInstance, java.lang.reflect.Field> a : accessInstanceAnnotations) {
            java.lang.reflect.Field field = a.getSecond();
            Class<?> accessClass = field.getDeclaringClass();
            accessInstances.put(accessClass, Arrays.asList(accessClass, field));
        }
        List<Tuple<io.github.kurrycat.mpkmod.gui.infovars.InfoString.AccessInstance, Class<?>>> accessClassAnnotations = ClassUtil.getClassAnnotations(classes, io.github.kurrycat.mpkmod.gui.infovars.InfoString.AccessInstance.class);
        for (Tuple<io.github.kurrycat.mpkmod.gui.infovars.InfoString.AccessInstance, Class<?>> a : accessClassAnnotations) {
            Class<?> clazz = a.getSecond();
            accessInstances.put(clazz, Collections.singletonList(clazz));
        }

        List<Tuple<io.github.kurrycat.mpkmod.gui.infovars.InfoString.DataClass, Class<?>>> dataClassAnnotations = ClassUtil.getClassAnnotations(classes, io.github.kurrycat.mpkmod.gui.infovars.InfoString.DataClass.class);
        List<Class<?>> dataClassList = dataClassAnnotations.stream().map(Tuple::getSecond).collect(Collectors.toList());

        List<Class<?>> accessClassList = new ArrayList<>(accessInstances.keySet());

        List<Tuple<io.github.kurrycat.mpkmod.gui.infovars.InfoString.Field, Field>> fieldAnnotations = ClassUtil.getFieldAnnotations(classes, io.github.kurrycat.mpkmod.gui.infovars.InfoString.Field.class);

        for (Tuple<io.github.kurrycat.mpkmod.gui.infovars.InfoString.Field, java.lang.reflect.Field> t : fieldAnnotations) {
            java.lang.reflect.Field f = t.getSecond();
            Class<?> clazz = f.getDeclaringClass();

            String name = f.getName();
            List<Object> objects = new ArrayList<>(accessInstances.get(clazz));
            objects.add(f);

            InfoVar var = new InfoVar(name, objects);
            infoMap.addElement(name, var);

            if (dataClassList.contains(f.getType())) {
                try {
                    recursiveSearch(var, objects, dataClassList);
                } catch (InvocationTargetException | IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
            }
        }

        List<Tuple<io.github.kurrycat.mpkmod.gui.infovars.InfoString.Getter, Method>> methodAnnotations = ClassUtil.getMethodAnnotations(accessClassList, io.github.kurrycat.mpkmod.gui.infovars.InfoString.Getter.class);
        for (Tuple<io.github.kurrycat.mpkmod.gui.infovars.InfoString.Getter, Method> t : methodAnnotations) {
            Method m = t.getSecond();
            Class<?> clazz = m.getDeclaringClass();

            String name = StringUtil.getterName(m.getName());
            List<Object> objects = new ArrayList<>(accessInstances.get(clazz));
            objects.add(m);

            InfoVar var = new InfoVar(name, objects);
            infoMap.addElement(name, var);

            if (dataClassList.contains(m.getReturnType())) {
                try {
                    recursiveSearch(var, objects, dataClassList);
                } catch (InvocationTargetException | IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        LOGGER.info("Loaded infoVars for {}, now there are {}", classes, Main.infoTree.getSize());
    }

    private static void recursiveSearch(InfoVar parentVar, List<Object> parent, List<Class<?>> dataClassList) throws InvocationTargetException, IllegalAccessException {
        recursiveSearch.invoke(null, parentVar, parent, dataClassList);
    }
}
