package utils;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class JudgeUtils
{
	String boolUtils()
	{
		if (BooleanUtils.isTrue(true)) // null返回false
		{
			return "xx";
		}
		return null;
	}

	String StringUtils()
	{

    // 如果字符串为null,多个空白符"   "，"\t \n \f \r"均为false,比isNotEmpty严格
		if (StringUtils.isNotBlank("xx"))
		{
			return "xx";
		}
		if(StringUtils.containsIgnoreCase("xyz","x")){
			return "xx";
		}
		return null;
	}

    // 集合和数组判断是否为null
	String collectionUtils(){
		List<String> list = new ArrayList<>();
		if(CollectionUtils.isNotEmpty(list)){
			return "xx";
		}
		return null;
	}

	// Objects.equals(a,b)对比a.equals(b)避免空指针
	void ObjectsUtils()
	{
		String i="abc";
		if(Objects.equals(i,null)){
			System.out.println(true);
		}
		else{
			System.out.println(false);
		}
	}
}
