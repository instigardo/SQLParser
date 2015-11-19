//package vz.parser.parse;
//
//import java.io.IOException;
//import java.sql.ResultSet;
//import java.sql.SQLException;
//import java.util.regex.Matcher;
//import java.util.regex.Pattern;
//
//import vz.parser.helper.SQLHelper;
//
//
//public class Temp {
//
//	String setFunc[]={"UNION", "UNION_ALL", "INTERSECT", "MINUS"};
//
//	String sqlText;
//	String parsedRows[]=new String[9];
//	int sqlId=0;
//
//
//	public int getSqlId() {
//		return sqlId;
//	}
//	public void setSqlId(int sqlId) {
//		this.sqlId = sqlId;
//	}
//
//	Parse parse=new Parse();
//
//
//	public String getSqlText() {
//		return sqlText;
//	}
//	public void setSqlText(String sqlText) {
//		this.sqlText = sqlText;
//	}
//
//	SQLHelper sqlHelp=new SQLHelper();
//	public void getSqlTxt(int i)
//	{
//		ResultSet rs= sqlHelp.SELECT("edw_base.hackathon_h","sql_text","sql_id="+i);
//		try {
//			while(rs.next())
//			{
//				setSqlText(rs.getString("sql_text"));
//				i++; 
//			}
//		} catch (SQLException e) {
//			e.printStackTrace();
//		}
//	}
//
//	public int countRows()
//	{
//		ResultSet rs=sqlHelp.SELECTNW("edw_base.hackathon_h","count(*) as number_rows");
//		try {
//			while(rs.next())
//			{
//				return rs.getInt("number_rows");
//
//			}
//		} catch (SQLException e) {
//			e.printStackTrace();
//		}
//		return 0;
//	}
//
//	public void processSql() throws IOException
//	{		
//		Queries query=new Queries();
//		String sqlText=getSqlText();
//		String sqlW[]=parse.sqlWordExtractor(sqlText);
//		String keyW[]=parse.extractKeywords(sqlText);
//
//		if(keyW[0].equals("INSERT"))
//			processInsert();
//
//		else if (keyW[0].equals("UPDATE"))
//			processUpdate();
//
//		else if (keyW[0].equals("DELETE"))
//			processDelete();
//
//
//		//		for (String string : keyW) {
//		//			if(string!=null)
//		//		System.out.println(string);
//		//				}
//		//	
//		//	for (String string1 : sqlW) {
//		//		if(string1!=null)
//		//	System.out.println(string1);
//		//			}
//	}
//
//	public void processInsert() throws IOException{
//		String sqlText=getSqlText();
//		String sqlW[]=parse.sqlWordExtractor(sqlText);
//		String keyW[]=parse.extractKeywords(sqlText);
//
//		String pschemaTb=sqlW[2];
//		//		System.out.println(pschemaTb);
//		String sqlWo[]=pschemaTb.split("[.]");
//		String pDB=sqlWo[0];
//		String pTB=sqlWo[1];
//		String cols=sqlText.substring(sqlText.indexOf("(") + 1, sqlText.indexOf(")"));
//		cols=cols.replaceAll("\n","");
//		cols=cols.replaceAll("\r","");
//		cols=cols.replaceAll(" ","");
//		String colsArr[]=cols.split(",");
//		System.out.println(colsArr.length);
//		for (int i=0;i<colsArr.length;i++) {
//
//
//			parsedRows[0]="INSERT";
//			parsedRows[1]=getSqlId()+"";
//			parsedRows[2]=pDB;
//			parsedRows[3]=pTB;
//			parsedRows[4]=colsArr[i];
//
//			
//
//
//		}
//		processSource();
//
//	}
//
//	public void processUpdate(){
//		parsedRows[0]="UPDATE";
//		parsedRows[1]=getSqlId()+"";
//		
//	}
//
//	public void processDelete(){
//		parsedRows[0]="DELETE";
//		parsedRows[1]=getSqlId()+"";
//
//	}
//
//	public void processSource() throws IOException
//	{
//		String srcSchema;
//		String srcTable[]=new String[50];
//		String srcColumn[]=new String[50];
//		String tempArr[]=new String[2];
//				
//		String selArr[]=new String[2];
//		String sqlText=getSqlText();
//		String sourceString=sqlText.substring(sqlText.indexOf("SELECT") , sqlText.indexOf(";")+1);
//		//System.out.println(sourceString);
//
//		for (String set : setFunc) {
//
//			if(sourceString.contains(set)){
//				selArr=sourceString.split(set);
//			}
//		}
//		for (String string : selArr) {
//			string=string+";";
//			//System.out.println(string);
//			
//			String subCol=string.substring(string.indexOf("SELECT")+6 , string.indexOf("FROM"));			
//			String sqlW[]=parse.sqlWordExtractor(subCol);
//			int q=0;
//			for (String strings : sqlW) {
//				if(strings.contains(".")){
//					selArr=strings.split("[.]");
//					srcColumn[q]=selArr[1];
//					srcTable[q]=selArr[0];
//					srcSchema="EDW_STG";
//					q++;
//					
//				}
//			}
//			for (int j=0 ; j<srcColumn.length;j++) {
//				System.out.println(srcColumn[j]);
//				System.out.println(srcTable[j]);
//			}
//			
//
//			
//			
//			String keyW[]=parse.extractKeywords(string);
//
//			/**To find number of joins**/	
//			int k = 0;
//			Pattern p = Pattern.compile("JOIN");
//			Matcher m = p.matcher( string );
//			while (m.find()) {
//				k++;
//			}
//			/******/
//			String subs = string.substring(string.indexOf("FROM") , string.indexOf(";")+1);
//			String ON;
//			if(k!=0){
//				if(subs.contains("WHERE"))
//				{
//					ON=subs.substring(subs.indexOf("ON")+2 , subs.indexOf("WHERE"));
//				}
//				else
//					ON=subs.substring(subs.indexOf("ON")+2 , subs.indexOf(";"));
//				String str=subs.substring(subs.indexOf("ON")+2 , subs.indexOf("WHERE"));
//				
//				String sqlWds[]=parse.sqlWordExtractor(subs);
//			//	String 
//				for(int a=0;a<sqlWds.length;a++){
//					
//				}
//				
//				
//				for (String strings : sqlWds) {
//					System.out.println(strings);
//					//if(strings.contains(".")){
//						//String tempArr[]=strings.split("[.]");
//						//System.out.println(tempArr[0]);
//
//						
//					}
//				}
//				
//			}
////			System.out.println(subs);
//			
//
//		}
//
//		//		for (String string : keyW) {
//		//			if(string!=null)
//		//			System.out.println(string);
//		//		}
//
//
//	
//
//	public void resolveAlias()
//	{
//		String sqlText=getSqlText();
//		String sqlWd[]=parse.sqlWordExtractor(sqlText);
//		if(sqlWd[0].equals("INSERT")){
//			String tbAliasStr=sqlText.substring(sqlText.indexOf("SELECT")+6 , sqlText.indexOf("FROM"));
//
//			String sqlW[]=parse.sqlWordExtractor(tbAliasStr);
//			for (String string : sqlW) {
//				//System.out.println(string);
//				if(string.contains(".")){
//					String tempArr[]=string.split("[.]");
//					//System.out.println(tempArr[0]);
//
//					for (int i=1;i< sqlWd.length;i++) {
//						if(sqlWd[i].equals(tempArr[0])) {
//							String str[]=sqlWd[i-1].split("[.]");
//							sqlText=sqlText.replaceAll(tempArr[0]+"[.]",str[1]+".");
//
//							//System.out.println(sqlText);
//						}
//					}
//				}
//			}
//			if(sqlText.contains("UNION ALL"))
//				sqlText=sqlText.replaceAll("UNION ALL","UNION_ALL");
//			setSqlText(sqlText);
//		}
//	}
//
//	public static void main(String[] args) throws IOException {
//		SQLResolver resolve=new SQLResolver();
//		//System.out.println(resolve.countRows());
//
//
//		resolve.getSql(1);
//		System.out.println(resolve.getSqlText());
//		resolve.resolveAlias();
//		String sqltext=resolve.getSqlText();
//		System.out.println(sqltext);
//
//		//resolve.processSql();
//		//resolve.processSource(1);
//		System.out.println("end");
//
//
//
//
//	}
//}
