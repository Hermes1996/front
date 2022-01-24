import com.sun.tracing.dtrace.ModuleName;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.record.DVALRecord;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static com.sun.javafx.binding.StringFormatter.concat;

public class Test
{
    public static void main(String[] args)
    {
        List <String> a = new ArrayList<>();
        a.add("7863637304392_CP_210");
        a.subList(a.size()-1,a.size()).clear();
        System.out.println(a);
    }
}
