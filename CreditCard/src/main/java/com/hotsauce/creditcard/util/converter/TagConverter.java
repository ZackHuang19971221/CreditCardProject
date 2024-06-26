package com.hotsauce.creditcard.util.converter;

import java.lang.reflect.Field;
import java.util.AbstractMap;
import java.util.Map;

public class TagConverter {
    public static boolean ignoreNull = true;
    public static boolean format = false;
    public static String SerializeObject(String rootName, Object value) {
        int whiteSpace = 0;
        if (format) {
            whiteSpace = 1;
        }
        String valueString = generateWithTagObjectString(value, whiteSpace);
        if (rootName == null || rootName.isEmpty()) {
            return valueString;
        }
        if (format) {
            return "<" + rootName + ">" + System.lineSeparator() + valueString + "</" + rootName + ">";
        }
        return "<" + rootName + ">" + valueString + "</" + rootName + ">";
    }
    private static String generateWithTagObjectString(Object value, int whiteSpace) {
        StringBuilder returnString = new StringBuilder();
        Class<?> objectType = value.getClass();
        Field[] fieldInfoList = objectType.getFields();
        Object fieldValue;
        for (Field fieldInfo : fieldInfoList) {
            try {
                fieldValue = fieldInfo.get(value);
            } catch (IllegalArgumentException | IllegalAccessException e) {
                fieldValue = null;
            }
            if (ignoreNull && fieldValue == null) {
                continue;
            }
            returnString.append(" ".repeat(whiteSpace)).append("<").append(fieldInfo.getName()).append(">");
            if (fieldValue.getClass().getPackageName().startsWith("java.lang")) {
                if (fieldValue == null) {
                    fieldValue = "";
                }
                returnString.append(fieldValue);
                returnString.append("</").append(fieldInfo.getName()).append(">");
            } else {
                if (format) {
                    int newWhiteSpace = whiteSpace + 1;
                    returnString.append(System.lineSeparator()).append(generateWithTagObjectString(fieldValue, newWhiteSpace));
                    returnString.append(" ".repeat(whiteSpace)).append("</").append(fieldInfo.getName()).append(">");
                } else {
                    returnString.append(generateWithTagObjectString(fieldValue, whiteSpace));
                    returnString.append("</").append(fieldInfo.getName()).append(">");
                }
            }
            if (format) {
                returnString.append(System.lineSeparator());
            }
        }
        return returnString.toString();
    }
    public static <T> T DeserializeObject(Class<?> type,String rootName, String value) {
       try {
           return (T) CreateInstance(type , rootName, value);
       }
       catch (Exception e){
            return  null;
       }

    }
    private static Object CreateInstance(Class<?> type, String nodeName, String value) throws InstantiationException, IllegalAccessException {
        Object NewObject = null;
        int TagLength = ("<" + nodeName + ">").length();
        if (value == null) {
            value = "";
        }
        if (!value.startsWith("<" + nodeName + ">") || !value.endsWith("</" + nodeName + ">")) {
            throw new IllegalArgumentException("Invalid format");
        }
        value = value.substring(TagLength, value.length() - TagLength - 1);
        if (type.isPrimitive())
        {
            try
            {
                if(type.equals(byte.class))
                {
                    NewObject = Byte.parseByte(value);
                }
                else if (type.equals(short.class))
                {
                    NewObject = Short.parseShort(value);
                }
                else if (type.equals(int.class))
                {
                    NewObject =Integer.parseInt(value);
                }
                else if (type.equals(long.class))
                {
                    NewObject = Long.parseLong(value);
                }
                else if (type.equals(float.class))
                {
                    NewObject = Float.parseFloat(value);
                }
                else if (type.equals(double.class))
                {
                    NewObject = Double.parseDouble(value);
                }
                else if (type.equals(boolean.class))
                {
                    NewObject =Boolean.parseBoolean(value);
                }
                else if (type.equals(char.class))
                {
                    NewObject =value.charAt(0);
                }
            }
            catch(Exception exception)
            {
                NewObject = type.newInstance();
            }
        }
        else if (type.equals(String.class))
        {
            NewObject= value;
        }
        else
        {
            NewObject = type.newInstance();
            Field field;
            Map.Entry<String, Integer> PreReadResult;
            String FieldValue;
            String TagName;
            StringBuilder TagNameWithTag = new StringBuilder();
            boolean StartReadingTag = false;
            for (int i = 0; i < value.length(); i++) {
                if (value.charAt(i) == '<') {
                    StartReadingTag = !StartReadingTag;
                }
                if (StartReadingTag) {
                    TagNameWithTag.append(value.charAt(i));
                    if (value.charAt(i) == '>') {
                        StartReadingTag = false;
                        TagName = TagNameWithTag.toString().trim().substring(1, TagNameWithTag.length() - 1);
                        PreReadResult = PreReadString(TagName, value.substring(i + 1));
                        FieldValue = TagNameWithTag + PreReadResult.getKey();
                        i += PreReadResult.getValue();
                        // SetField
                        try {
                            field = NewObject.getClass().getDeclaredField(TagName);
                            field.setAccessible(true);
                            field.set(NewObject, CreateInstance(field.getType(), TagName, FieldValue));
                        } catch (NoSuchFieldException e) {
                            // Do nothing
                        } catch (IllegalAccessException e) {
                            // Do nothing
                        }
                        TagNameWithTag = new StringBuilder();
                    }
                } else {
                    TagNameWithTag = new StringBuilder();
                }
            }
        }
        return NewObject;
    }
    /**
     * PreRead the value and find end tag
     */
    private static Map.Entry<String, Integer> PreReadString(String tagName, String nextString) {
        // </tagName>
        int EndTagLength = tagName.length() + 3;
        String TempString = "";
        StringBuilder ReturnString = new StringBuilder();
        for (int i = 0; i < nextString.length(); i++) {
            if (nextString.charAt(i) == '<') {
                if (EndTagLength + i > nextString.length()) {
                    break;
                }
                TempString = nextString.substring(i, i + EndTagLength);
                if (TempString.equals("</" + tagName + ">")) {
                    return new AbstractMap.SimpleEntry<>(ReturnString + TempString, i + EndTagLength);
                } else {
                    TempString = "";
                }
            }
            ReturnString.append(nextString.charAt(i));
        }
        throw new IllegalArgumentException("Invalid Format");
    }
}