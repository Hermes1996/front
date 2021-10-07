package json;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.annotation.JSONField;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class JsonClass {

	public static void main(String[] args) throws ParseException
	{
		Person p = new Person();
		p.setFirstName("hermes");
		SimpleDateFormat myFormatter = new SimpleDateFormat("yyyy-MM-dd");
		Date date= myFormatter.parse("2003-05-1");
		p.setdate(date);
		p.setRemart("A01");

		// 转化的json字符串，会把firstName对应为first_name
		String json = JSON.toJSONString(p);
		System.out.println(json);

		// 转化的对象，会把first_name对应到firstName
		Person pp = JSON.parseObject(json, Person.class);
		System.out.println(pp.getFirstName());
	}
}

class Person implements Serializable //对象实例的状态存储,本地代码状态保持在内存）；传输时对象状态时需要实现接口
{
	@JSONField(name="first_name")//与数据库字段映射
	private String firstName;

	@JSONField(format="yyyyMMdd")//格式化
	private Date date;

	@JSONField(serialize = false)//屏蔽序列化
	private String remart;

	public String getFirstName() {
		return firstName;
	}
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
	public Date getdate() {
		return date;
	}
	public void setdate(Date date) {
		this.date = date;
	}
	public String getRemart() {
		return remart;
	}
	public void setRemart(String remart) {
		this.remart = remart;
	}
}