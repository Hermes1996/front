package stream;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

public class ForEach
{
    public static void main(String[] args)
    {
        Map<String, Integer> items = new HashMap<>();
        items.put("A", 10);
        items.put("B", 20);
        items.put("C", 30);

        items.forEach((k, v) -> {
            if (k.equals("A"))
            {
                System.out.println("匹配成功");
                return;
            }
            System.out.println("return不终止");
        });

        boolean match = items.entrySet().stream().anyMatch(e -> e.getKey().equals("A"));
        System.out.println(match);

        boolean match1 = items.entrySet().stream().anyMatch(e -> {
            switch (e.getKey())
            {
                case "A":
                    System.out.println("匹配A");
                    return true;
                default:
                    break;
            }
            return false;
        });
        System.out.println(match1);

        Map.Entry<String, Integer> a = items.entrySet().stream().filter(e -> e.getKey().equals("AA")).findAny()
                .orElse(null);
        System.out.println(a.getKey());
    }
}
class ListForEach{
    public static void main(String[] args)
    {
       List<String> list = new ArrayList<>();
       list.add("Emily");
       list.add("Tom");
       list.add("Jack");
        boolean match = list.stream().anyMatch(Predicate.isEqual("Emil"));
        System.out.println(match);
    }
}
//    /**
//     * 获取拣货人
//     *
//     * @param warehouseId
//     * @param locationNumber
//     * @return
//     */
//    public static Integer getPackingPerson(Integer warehouseId, String locationNumber)
//    {
//        if (warehouseId == null || StringUtils.isBlank(locationNumber))
//        {
//            return null;
//        }
//
//        LocationNumberBindService locationNumberBindService = SpringContextUtils.getBean(LocationNumberBindService.class);
//        List<LocationNumberBind> locationNumberBinds = locationNumberBindService.getLocationNumberBindListForCache();
//
//        List<Integer> packBys = new ArrayList<>();
//
//        // 获取库位区间并根据仓库过滤
//        locationNumberBinds.stream()
//                .filter(locationNumberBind -> locationNumberBind.getWarehouseId().equals(warehouseId)
//                        && StringUtils.isNotBlank(locationNumberBind.getLocationRangeJson()))
//                .flatMap(locationNumberBind -> { *map返回流
//                    List<LocationRange> locationRanges = JSON.parseArray(locationNumberBind.getLocationRangeJson(), LocationRange.class);
//
//                    return locationRanges.stream().filter(locationRange -> {
//                        String locationNumberStart = StringUtils.trim(locationRange.getLocationNumberStart());
//                        String locationNumberEnd = StringUtils.trim(locationRange.getLocationNumberEnd());
//
//                        if (StringUtils.isBlank(locationNumberStart) || StringUtils.isBlank(locationNumberEnd))
//                        {
//                            return false;
//                        }
//
//                        String locationNumberSub = StringUtils.substring(locationNumber, 0, locationNumberStart.length());
//                        if (StringUtils.isBlank(locationNumberSub))
//                        {
//                            return false;
//                        }
//
//                        locationNumberSub = locationNumberSub.toUpperCase();
//                        locationNumberStart = locationNumberStart.toUpperCase();
//                        locationNumberEnd = locationNumberEnd.toUpperCase();
//
//                        // 库位区间匹配
//                        if (locationNumberSub.compareTo(locationNumberStart) >= 0
//                                && locationNumberSub.compareTo(locationNumberEnd) <= 0)
//                        {
//                            packBys.add(locationNumberBind.getPackBy());
//                            return true;
//                        }
//
//                        return false;
//                    });
//                }).findFirst().orElse(null);  *findFirst终止操作
//
//        return packBys.stream().findFirst().orElse( null );
//    }
//}