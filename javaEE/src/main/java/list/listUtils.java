package list;

import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;


public class listUtils
{

	public static void main(String[] args)
	{
		List list = Stream.of("a", "b", "c", "d", "e", "f").collect(Collectors.toList());
		if (list.size() >= 5)
		{
			list = list.subList(0, 5);
		}
		System.out.println(list);
		//list1前端判断不为空
		List<String> listNew = Stream.of("测试1", "c").collect(Collectors.toList());
		String listOld = " 测试1, c,a";
		if (listOld != null && !listOld.equals(""))
		{
			String[] split = listOld.replaceAll("\\s*", "").split(",");
			// 将数组转化成List集合
			List<String> strings = Arrays.asList(split);
			//linkedHashSet追尾增加元素自然顺序
			LinkedHashSet<String> linkedHashSet = new LinkedHashSet<String>();
			linkedHashSet.addAll(listNew);
			linkedHashSet.retainAll(strings);
			if (linkedHashSet.size() == listNew.size())
			{
				System.out.println("不做修改");
			}
			else
			{
				if (StringUtils.isNotBlank(listOld))
				{
					linkedHashSet.addAll(strings);
					System.out.println("执行");
				}
				linkedHashSet.addAll(listNew);
				String string = linkedHashSet.toString();
				System.out.println(string.substring(1, string.length() - 1));
			}
		}
	}
}
