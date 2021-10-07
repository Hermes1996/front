package utils;

import java.util.ArrayList;
import java.util.List;

public class ListNullJudge
{

	public static void main(String[] args)
	{
		List<String> list = new ArrayList<>();
		list.add(null);
		System.out.println("运行到此说明list.add的参数可以为null。");
		List<String> list2 = null;
		try
		{
			list2.addAll(null);
		}
		catch (Exception e)
		{
			System.out.println("运行到此说明list.addAll的参数不可以为null。");
		}
		System.out.println("list均不能为null");


	}
}