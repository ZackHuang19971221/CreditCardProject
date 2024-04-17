package com.hotsauce.creditcard.util.converter;
import java.lang.reflect.Field;

public class SplitConverter {
    public static <T> T DeserializeObject(String[] values, Class<T> clazz) throws Exception {
        T instance = clazz.newInstance();
        String ID;
        Object value;
        String[] tempArr;
        Field field;
        for (String arg : values) {
            tempArr = arg.toString().split("=");
            if (tempArr.length < 2) {
                continue;
            }
            ID = tempArr[0];
            try {
                field = clazz.getDeclaredField(ID);
            } catch (NoSuchFieldException e) {
                continue;
            }
            try {
                value = Convert(tempArr[1], field.getType());
                field.setAccessible(true);
                field.set(instance, value);
            } catch (Exception e) {
            }
        }
        return instance;
    }

    private static Object Convert(String value, Class<?> targetType) {
        if (targetType == int.class || targetType == Integer.class) {
            return Integer.parseInt(value);
        } else if (targetType == long.class || targetType == Long.class) {
            return Long.parseLong(value);
        } else if (targetType == float.class || targetType == Float.class) {
            return Float.parseFloat(value);
        } else if (targetType == double.class || targetType == Double.class) {
            return Double.parseDouble(value);
        } else if (targetType == boolean.class || targetType == Boolean.class) {
            return Boolean.parseBoolean(value);
        } else if (targetType == String.class) {
            return value;
        }
        return null;
    }
}

