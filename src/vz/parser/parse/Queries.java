package vz.parser.parse;

import java.io.IOException;

import vz.parser.helper.ReadExcel;
import vz.parser.query.*;

public class Queries {
	Parse parse=new Parse();
	SQLResolver resolver=new SQLResolver();
	ReadExcel Rex=new ReadExcel();
	ProcessInsert processInsert=new ProcessInsert();
	ProcessUpdate processUpdate=new ProcessUpdate();
	ProcessDelete processDelete=new ProcessDelete();
	QueryDef qd=new QueryDef();
	
	public void processSql() throws IOException
	{		
		Queries query=new Queries();
		String sqlText=resolver.getSqlText();
		String sqlW[]=parse.sqlWordExtractor(sqlText);

		//System.out.println(sqlText);
		
		if(sqlW[0].equals("INSERT")){
			System.out.println("ins");
			//qd.qdInsert(sqlText);

			processInsert.processInsertion(sqlText);
		}
		else if (sqlW[0].equals("UPDATE")){
			//processUpdate();
			System.out.println("upd");
		processUpdate.processUpdation(sqlText);
		}
		else if (sqlW[0].equals("DELETE")){
			//processDelete();
			System.out.println("del");
			processDelete.processDeletion(sqlText);
		}
	}
	String shortSql[]={" SEL "," INS "," DEL "};
	String replacement[]={" SELECT ", " INSERT ", " DELETE "};
	public void parseInit(int id) throws IOException
	{
		
		String sqlID[]=Rex.getSqlId();
		String sql[]=Rex.getSql();
		String sqlText=sql[id].toUpperCase();
		sqlText=sqlText.replaceAll("\\(", " \\( ");
		sqlText=sqlText.replaceAll("\\)", " \\) ");
		sqlText=sqlText.replaceAll(",", " , ");

		sqlText=sqlText.replaceAll("\n", " ");
		sqlText=sqlText.replaceAll("\r", " ");
		sqlText=sqlText.replaceAll("'", " \" ");
		for(int i=0;i<10;i++)
			sqlText=sqlText.replaceAll("  "," ");
		int i=0;
		for(String str:shortSql){
			if(sqlText.contains(str)){
				sqlText=sqlText.replaceAll(str, replacement[i]);
			}
			i++;
		}
		sqlText=sqlText.trim();
		//resolver.resolveAlias(sqlText);
		resolver.setSqlText(sqlText);
		//System.out.println(sqlText);
		//parse.getSQLString("SQL_TEXT938.txt");
		//resolver.resolveAlias(sqlText);
		
		processSql();
		
	}
	
	public static void main(String[] args) throws IOException {
		System.out.println("start");
		Parse parse=new Parse();
		SQLResolver resolver=new SQLResolver();
		Queries query=new Queries();
		int id=6;
		resolver.setSqlId(id);
		query.parseInit(id);
		System.out.println("end");
		
		
	}
	
}
