package vz.parser.helper;

import java.io.BufferedInputStream;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException; 
import java.io.InputStream; 
import java.util.Iterator; 
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.sl.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.usermodel.HSSFRow;
public class ReadExcel
{
	static String sql[]=new String[10];
	static String sqlId[]=new String[10];

	public String[] getSql() {

		return sql;
	}

	public void setSql(String[] sql) {
		this.sql = sql;
	}

	public String[] getSqlId() {
		return sqlId;
	}

	public void setSqlId(String[] sqlId) {
		this.sqlId = sqlId;
	}
	String text="";
	public void excel()
	{
		String sqlId1[]=new String[10];
		String sql1[]=new String[10];
		
				
		int count=0;
		try {
			InputStream input = new BufferedInputStream( new FileInputStream("preprocessed_sql.xls"));
			POIFSFileSystem fs = new POIFSFileSystem( input );
			HSSFWorkbook wb = new HSSFWorkbook(fs);
			HSSFSheet sheet = wb.getSheetAt(0);
			Iterator rows = sheet.rowIterator();
			while( rows.hasNext() )
			{ 
				HSSFRow row = (HSSFRow) rows.next();

				Iterator cells = row.cellIterator();
				while( cells.hasNext() )
				{
					HSSFCell cell = (HSSFCell) cells.next(); 
					if(HSSFCell.CELL_TYPE_NUMERIC==cell.getCellType())
					{//sqlId1[count]=cell.getNumericCellValue()+" ";
					//System.out.print( sql[count]);
					}
					//System.out.print( cell.getNumericCellValue()+" " );
					else if(HSSFCell.CELL_TYPE_STRING==cell.getCellType()) 
					{text+=cell.getStringCellValue()+"\n";
					System.out.print(text);
					}
					else if(HSSFCell.CELL_TYPE_BOOLEAN==cell.getCellType()) 
						System.out.print( cell.getBooleanCellValue()+" " );
					else if(HSSFCell.CELL_TYPE_BLANK==cell.getCellType())
						System.out.print( "BLANK " ); 
					else System.out.print("Unknown cell type");
				} 
				count++;
				System.out.println(count);
			}
			setSql(sql1);
			setSqlId(sqlId1);} 
		catch ( IOException ex ) 
		{ 
			ex.printStackTrace();
		}


	}
	org.apache.poi.ss.usermodel.Sheet sheet;
	Workbook wb;
	FileOutputStream fileOut;
	
	public org.apache.poi.ss.usermodel.Sheet getSheet() {
		return sheet;
	}

	public void setSheet(org.apache.poi.ss.usermodel.Sheet sheet) {
		this.sheet = sheet;
	}

	public void createExcel() throws FileNotFoundException{
		wb = new HSSFWorkbook();
		//Workbook wb = new XSSFWorkbook();
		CreationHelper createHelper = wb.getCreationHelper();
		sheet = wb.createSheet("new sheet");
		setSheet(sheet);
		fileOut = new FileOutputStream("workbook.xls");
	}
	int rowsNum=0;
	public 	    FileOutputStream writeExcel(String sql_text, String keywords) throws Exception
	{


		// Create a row and put some cells in it. Rows are 0 based.
		Row row = sheet.createRow(rowsNum);
		// Create a cell and put a value in it.
		//System.out.println(sql_text);
		//System.out.println(keywords);
		//System.out.println(sql_text.length());
		//System.out.println(keywords.length());
		row.createCell(0).setCellValue(rowsNum);
		row.createCell(1).setCellValue(sql_text);
		row.createCell(2).setCellValue(keywords);


		// Write the output to a file
		
		
		rowsNum++;
	System.out.println(rowsNum);
		
		return fileOut;
	}
	public void writeclose() throws IOException
	{
		wb.write(fileOut);
		fileOut.close();
	}
public static void main(String[] args) {
	ReadExcel re=new ReadExcel();
	re.excel();
	try {
        BufferedWriter out = new BufferedWriter(new FileWriter("file.txt"));
            
                out.write(re.text);
            
            out.close();
        } catch (IOException e) {}

	System.out.println(re.text);
}
} 


