package utils;

public class IfUtils
{
	public static void main(String[] args)
	{
        // Boolean可以为null,但if 判断不能为空
        //结论：先进行'&&'运算，在进行'||'运算
		Boolean a = false;
		if (a == null || a != null && !a)
		{
			System.out.println("qq");
		}
		System.out.println("ss");
	}
}