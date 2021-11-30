import com.sun.tracing.dtrace.ModuleName;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class Test
{
    public static void main(String[] args)
    {
        List<String> list = new ArrayList<>();
        list.add("aaa");
        list.add("bbb");
        Map<String, String> aaa = list.stream().filter(l -> l.equals("aaa"))
                .collect(Collectors.toMap(l -> l, Function.identity()));
        Map<String, String> aa = list.stream().collect(Collectors.toMap(l -> l, Function.identity()));

        System.out.println(aa);
    }
}
