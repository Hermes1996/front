package utils;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

public class CommonUtils
{
	//字符串组合（数字）拆分成list
	public static List<Integer> splitIntList(String str, String separator)
	{
		List<Integer> intergerList = new ArrayList<Integer>();
		if (StringUtils.isNotBlank(str))
		{
			String[] split = StringUtils.split(str, separator);

			for (int i = 0, len = split.length; i < len; i++)
			{
				if (StringUtils.isNotBlank(split[i]))
				{
					if (isNumber(split[i]))
					{
						intergerList.add(Integer.valueOf(StringUtils.trim(split[i])));
					}
				}
			}
		}

		return intergerList;
	}

	//判断一个字符串是否全部由数字组成
	public static boolean isNumber(String str)
	{
		char[] chars = str.toCharArray();
		for (int i = 0; i < chars.length; i++)
		{
			if (chars[i] < '0' || chars[i] > '9')
			{
				return false;
			}
		}
		return true;
	}

	public static void main(String[] args)
	{
		String str = "222,333,aaa";
		CommonUtils commonUtils = new CommonUtils();
		List<Integer> integers = commonUtils.splitIntList(str, ",");
		System.out.println(integers);
	}
}
