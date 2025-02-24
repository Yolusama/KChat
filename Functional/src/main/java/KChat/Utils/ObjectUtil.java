package KChat.Utils;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class ObjectUtil {
    public static<T1,T2> T2 copy(T1 src,T2 to)  {
        Class<?> srcType = src.getClass();
        Class<?> toType = to.getClass();

        Field[] fields = toType.getDeclaredFields();
        List<Field> srcFields = Arrays.stream(srcType.getDeclaredFields()).collect(Collectors.toList());
        try {
         for(Field field: fields)
           {
             if(!srcType.equals(toType)) {
                 Optional<Field> srcField = srcFields.stream().filter(f -> f.getName().equals(field.getName())).findFirst();
                 if (srcField.isEmpty())
                     continue;
                 field.setAccessible(true);
                 srcField.get().setAccessible(true);
                 field.set(to, srcField.get().get(src));
             }
             else{
                 field.setAccessible(true);
                 field.set(to,field.get(src));
             }
           }
        }
        catch (Exception ex){
            ex.printStackTrace();
        }
        return to;
    }

    public static String asJsonStr(Map<String,Integer> map)
    {
        StringBuilder builder = new StringBuilder();
        builder.append('{');
        for(String key: map.keySet()){
            builder.append(String.format("\"%s\":%d,",key,map.get(key)));
        }
        builder.delete(builder.length()-1,builder.length());
        builder.append('}');
        return builder.toString();
    }

    public static boolean isRequestParamStrNull(String param){
        return param==null||param.equals("")||param.equals("null");
    }

    public static<T> List<T> toList(T[] array){
        return List.of(array);
    }

    public static<T> T[] toArray(List<T> list,Class<T> type){
        T[] array = (T[]) Array.newInstance(type,list.size());
        return list.toArray(array);
    }
}
