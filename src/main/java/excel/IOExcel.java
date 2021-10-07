package excel;

import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Objects;

//HSSFWorkbook：操作Excel2003以前（包括2003）的版本，扩展名是.xls；
//XSSFWorkbook：扩展名.xlsx,操作版本Excel2007
//异常No valid entries or contents found：文件本身损坏;读取前生成的excel至少有一个sheet
public class IOExcel
{
	public static void main(String[] args) throws IOException
	{
		String path = "C:\\Users\\win10\\Desktop\\test\\测试.xlsx";
		// 存储于系统临时文件目录上
		File tempFile = File.createTempFile("测试x", "xlsx");

		File file = new File(path);
		if (!file.exists())
		{
			file.createNewFile();
			FileOutputStream fileOutputStream = new FileOutputStream(file);//写在存在判断里面【目标文件不存在，该流会创建该文件】
			XSSFWorkbook wb = new XSSFWorkbook();
			wb.createSheet("汇总表");
			wb.write(fileOutputStream);// wb，write和close同一个流不可反复执行
			wb.close();
			fileOutputStream.close();
		}
		FileInputStream fileInputStream =new FileInputStream(file);
		XSSFWorkbook wb = new XSSFWorkbook(fileInputStream);//传参指定excel文件，不然覆盖
		FileOutputStream fileOutputStream = new FileOutputStream(file);

		XSSFSheet sheet = wb.getSheet("汇总表");
		//避免覆盖
		if (Objects.isNull(sheet))
		{
			sheet = wb.createSheet("汇总表");
		}

		XSSFRow titleRow = sheet.createRow(0);
		int titleNum = 0;
		XSSFCell cell = titleRow.createCell(titleNum++);
		cell.setCellValue("第一列02");
		wb.write(fileOutputStream);
		wb.close();
		fileOutputStream.close();
	}
}