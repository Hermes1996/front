package e;

import java.util.List;

public class Exception
{
	public static void main(String[] args)
	{
		List list = null;
		try
		{
			if (null != list && list.size() >= 1)
			//if (null != list || list.size()>=1)会抛异常
			{
				System.out.println("不为空");
			}
			else
			{
				System.out.println("为空");
			}
		}
		catch (java.lang.Exception e)
		{
			System.out.println(e.getMessage());
		}
	}
}
