package utils;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Utils
{
	public static String demo(String type)
	{
		switch (type)
		{
			case "MASK":
			{
				List<String> list = new ArrayList<>();
				list.add(BooleanUtils.isTrue(true) ? "xx" : null);
				list.add(BooleanUtils.isTrue(true) ? "xx" : null);
				list.add(BooleanUtils.isTrue(true) ? "xx" : null);
//				list去除所有同一元素
				list.removeAll(Collections.singletonList(null));
				//	如果不是在循环体中进行字符串拼接的话，直接使用+就好了。
				//	如果在并发场景中进行字符串拼接的话，要使用StringBuffer(安全）来代替StringBuilder。
				//	StringUtils.join为StringBuilding+空判断+唯一元素判断+循环迭代
				String StringJoin = StringUtils.join(list, ",");
				return StringJoin;
			}
		}
		return null;
	}

	public static void main(String[] args)
	{
		Utils utils = new Utils();
		String mask = utils.demo("MASK");
		System.out.println(mask);

	}
}