package vz.parser.helper;


	import java.io.BufferedInputStream;
	import java.io.FileInputStream;
	import java.io.IOException; 
	import java.io.InputStream; 
	import java.util.Iterator; 
	import org.apache.poi.poifs.filesystem.POIFSFileSystem;
	import org.apache.poi.xssf.usermodel.XSSFCell;
	import org.apache.poi.xssf.usermodel.XSSFSheet;
	import org.apache.poi.xssf.usermodel.XSSFWorkbook;
	import org.apache.poi.xssf.usermodel.XSSFRow;
	public class Xcl
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

		public void excel()
		{
			String sqlId1[]=new String[10];
			String sql1[]=new String[10];
			int count=0;
			try {
				InputStream input = new BufferedInputStream( new FileInputStream("Hackathon-SQL.xls"));
				
				POIFSFileSystem fs = new POIFSFileSystem( input );
				XSSFWorkbook wb = new XSSFWorkbook(input);
				XSSFSheet sheet = wb.getSheetAt(0);
				Iterator rows = sheet.rowIterator();
				while( rows.hasNext() )
				{ 
					XSSFRow row = (XSSFRow) rows.next();
					
					Iterator cells = row.cellIterator();
					while( cells.hasNext() )
					{
						XSSFCell cell = (XSSFCell) cells.next(); 
						if(XSSFCell.CELL_TYPE_NUMERIC==cell.getCellType())
						 {sqlId1[count]=cell.getNumericCellValue()+" ";
							//System.out.print( sql[count]);
							 }
							//System.out.print( cell.getNumericCellValue()+" " );
						else if(XSSFCell.CELL_TYPE_STRING==cell.getCellType()) 
							 {sql1[count]=cell.getStringCellValue()+" ";
							//System.out.print( sql[count]);
							 }
						else if(XSSFCell.CELL_TYPE_BOOLEAN==cell.getCellType()) 
							System.out.print( cell.getBooleanCellValue()+" " );
						else if(XSSFCell.CELL_TYPE_BLANK==cell.getCellType())
							System.out.print( "BLANK " ); 
						else System.out.print("Unknown cell type");
						} 
					count++;
					}
				setSql(sql1);
				setSqlId(sqlId1);} 
			catch ( IOException ex ) 
			{ 
				ex.printStackTrace();
				}
			
			
			}
		} 




