
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by huyuening on 2017/12/29.
 */
public class test {

    public static void main(String[] args) {
        List<Map<String,Object>> list = new ArrayList<>();
        Map<String,Object> map1 = new HashMap<>();
        map1.put("1","aa");
        map1.put("2","bb");
        map1.put("3","cc");
        list.add(map1);
        List<Map<String,Object>> list2 = new ArrayList<>();
        Map<String,Object> map2 = new HashMap<>();
        map2.put("11","aa");
        map2.put("22","bb");
        map2.put("33","cc");
        list2.add(map2);

        list2.get(0).putAll(list.get(0));
        System.out.println(list2);
    }
}
