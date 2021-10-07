package jdbc;
import java.sql.*;

public class Connect
{

	public static void main(String[] args) throws ClassNotFoundException, SQLException
	{
		String URL = "jdbc:mysql://10.30.1.22:3306/bessky_pss?useUnicode=true&characterEncoding=utf-8";
		String USER = "root";
		String PASSWORD = "20092009";
		//1.加载驱动程序
		Class.forName("com.mysql.jdbc.Driver");
		//2.获得数据库链接
		Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
		//3.通过数据库的连接操作数据库，实现增删改查（使用Statement类）
		Statement st = conn.createStatement();
		ResultSet rs = st.executeQuery("select * from t_order limit 0,2");
		//4.处理数据库的返回结果(使用ResultSet类)
		ResultSetMetaData rsmd=rs.getMetaData();
		int colCount = rsmd.getColumnCount();
		while(rs.next())
		{
			for (int i = 0; i < colCount; i++)
			{
				//					System.out.println(rs.getString("order_id") );
				System.out.println(rs.getString(i+1));
			}
		}
		//关闭资源
		rs.close();
		st.close();
		conn.close();
	}
}