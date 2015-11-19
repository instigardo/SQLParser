package vz.parser.extracts;

import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLRecoverableException;
import java.util.ArrayList;

import vz.parser.helper.ReadExcel;
import vz.parser.helper.SQLHelper;
import vz.parser.parse.Parse;
import vz.parser.parse.SQLResolver;

public class Extracts {
	String sql_txt="";
	ReadExcel rx=new ReadExcel();
	Parse parse=new Parse();
	SQLResolver resolver=new SQLResolver();
	int id;
	String keysString="";
	public String extractKeywords(String alias, String[] keywords) throws IOException{
	//rx.excel();
	//String sql[]=rx.getSql();
	//get id from servlet
	//id=serv.getId();
	id=6;
	//keysString="";
	String sqlTxt=alias;
			//sql[id];
	//System.out.println(sqlTxt);
	//sqlTxt=resolver.resolveAlias(sqlTxt);
	sqlTxt=parse.formatter(sqlTxt);
	sql_txt=sqlTxt;
	//System.out.println(sqlTxt);
	String sqlKeyWds[]=parse.extractKeywords(sqlTxt,keywords);
	//System.out.println(sqlTxt);
	for (String string : sqlKeyWds) {
		if(string!=null){
		//System.out.println(string);
		keysString+=string+" , ";
		}
	}
	
	return keysString;
	
	
	
	}
	static String pKeys="";
	public static void main(String[] args) throws Exception {
		System.out.println("Started");
		FileOutputStream fileOut = null;
		Extracts ex=new Extracts();
		ReadExcel re=new ReadExcel();
		Parse parse=new Parse();
		re.createExcel();
		String keywords[]=parse.getKeywords();
		String toExcelKeys,toExcelSql;
		SQLHelper help=new SQLHelper();
		ResultSet rs=help.SELECTNW("edw_offshore_metadata_vw.sql_tmp", "sql_text");
		ArrayList<String> a1=new ArrayList<String>();
		while(rs.next()){
			a1.add(rs.getString("sql_text"));
		}
		for (String string : a1) {
			
			toExcelKeys=ex.extractKeywords(string,keywords);

			toExcelSql=ex.sql_txt;
			if(pKeys.equals(toExcelKeys))
			{
				System.out.println(toExcelKeys);
				System.out.println(toExcelSql);
			
			}
			else
			fileOut=re.writeExcel(toExcelSql, toExcelKeys);
			pKeys=toExcelKeys;

		}
	
		re.writeclose();
		
		
		System.out.println("Done");
	}
}
