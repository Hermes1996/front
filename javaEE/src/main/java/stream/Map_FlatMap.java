package stream;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

// 1.flatMap匿名内部类表达内容只能是Stream类型，返回类型可能不一样
// 2.map将对象一一映射为匿名内部类表达内容，对去重会有影响；flatMap将对象最底层元素抽取映射
public class Map_FlatMap {


	List<String[]> eggs = new ArrayList<>();

	@Before
	public void init() {
		// 第一箱鸡蛋
		eggs.add(new String[]{"鸡蛋_1", "鸡蛋_1", "鸡蛋_1", "鸡蛋_1", "鸡蛋_1"});
		// 第二箱鸡蛋
		eggs.add(new String[]{"鸡蛋_2", "鸡蛋_2", "鸡蛋_2", "鸡蛋_2", "鸡蛋_2"});
	}

	// 自增生成组编号
	static int group = 1;
	// 自增生成学生编号
	static int student = 1;

	/**
	 * 把二箱鸡蛋分别加工成煎蛋，还是放在原来的两箱，分给2组学生
	 */
	@Test
	public void map() {
		Stream<Stream<String>> streamStream = eggs.stream().map(x -> Arrays.stream(x).map(y -> y.replace("鸡", "煎")));
		streamStream.forEach(x -> System.out.println("组" + group++ + ":" + Arrays.toString(x.toArray())));
	}

	/**
	 * 把二箱鸡蛋分别加工成煎蛋，然后放到一起【10个煎蛋】，分给10个学生
	 */
	@Test
	public void flatMap() {
		Stream<String> stringStream = eggs.stream().flatMap(x -> Arrays.stream(x).map(y -> y.replace("鸡", "煎")));
		stringStream.forEach(x -> System.out.println("学生" + student++ + ":" + x));

	}

	public static void main(String[] args)
	{
		// 将集合中的所有的小写字母转为大写字母
		List<String> list = new ArrayList<>();
		String[] ls = "hello".split("");//不指定分割字符，逐字符分割
//		System.out.println(Arrays.toString(ls));
		list.add("hello");
		list.add("world");
		list.add("java");
		list.add("python");
		list.add("python");
		//去重
		Stream<String> distinct = list.stream().distinct();//x -> x.toUpperCase()
		distinct.forEach(x-> System.out.println(x));
		//Map匿名内部类表达内容无限制
		Stream<String> stringStream = list.stream().map(String::toUpperCase);//x -> x.toUpperCase()
		stringStream.forEach(x-> System.out.println(x));
		//数组去重失败，指针不同
		Stream<Stream<String>> mapStreamStream = list.stream().map(x -> Arrays.stream(x.split(""))).distinct();
		mapStreamStream.forEach(x-> System.out.println(Arrays.toString(x.toArray())));
		//String 类已经覆写了 equals() 和 hashCode() 方法，所以可以去重成功。
		Stream<String> flatMapdistinct = list.stream().flatMap(x -> Arrays.stream(x.split(""))).distinct();
		flatMapdistinct.forEach(x-> System.out.println(x));
	}

}

