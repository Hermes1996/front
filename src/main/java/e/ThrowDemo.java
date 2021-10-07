package e;

public class ThrowDemo
{
	public static void main(String[] args)
	{
		try
		{
			//抛出异常的实例化对象;一般用于自定义异常，并自定义提示信息
			throw new ArrayIndexOutOfBoundsException("\n个性化异常信息：\n数组下标越界");
		}
		catch(ArrayIndexOutOfBoundsException ex)
		{
			System.out.println(ex);
		}
	}
}