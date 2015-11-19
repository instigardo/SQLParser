package vz.parser.parse;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import vz.parser.helper.SQLHelper;


public class SQLResolver {
	Parse parse=new Parse();
	String setFunc[]={"UNION", "UNION_ALL", "INTERSECT", "MINUS"};

	static String sqlText;
	String parsedRows[]=new String[9];
	static int sqlId=0;


	public int getSqlId() {
		return sqlId;
	}
	public void setSqlId(int sqlId) {
		this.sqlId = sqlId;
	}


	public String getSqlText() {
		return sqlText;
	}
	public void setSqlText(String sqlText) {
		this.sqlText = sqlText;
	}

	SQLHelper sqlHelp=new SQLHelper();

	public int countRows()
	{
		ResultSet rs=sqlHelp.SELECTNW("edw_base.hackathon_h","count(*) as number_rows");
		try {
			while(rs.next())
			{
				return rs.getInt("number_rows");

			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return 0;
	}

	public void getSql(int i)
	{
		ResultSet rs= sqlHelp.SELECT("edw_base.hackathon_h","sql_text","sql_id="+i);
		try {
			while(rs.next())
			{
				setSqlText(rs.getString("sql_text"));
				i++; 
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}


	public String resolveAlias(String sqlText)
	{

		String aliasTbl[]=new String[50];
		String schemamap[]=new String[50];
		String tablemap[]=new String[50];
		String aliasmap[]=new String[50];
		sqlText=sqlText.toUpperCase();
		
		String sqlta[]=parse.sqlWordExtractor(sqlText);
		int u=0;
		for (int j = 0; j < sqlta.length; j++) {
			if(sqlta[j].contains(".")&&sqlta[j].contains("EDW_")){
				aliasTbl[u]=sqlta[j];
				String split[]=aliasTbl[u].split("[.]");
				schemamap[u]=split[0].trim();
				tablemap[u]=split[1].trim();
				if(sqlta[j+1].equals(")"))
					aliasmap[u]=tablemap[u];
				else
					if(sqlta[j+1].contains("("))
						aliasmap[u]="";
					else
					aliasmap[u]=sqlta[j+1];
				u++;
			}
		}

		for (int i = 0; i < aliasmap.length; i++) {
			if(tablemap[i]!=null){
				String alsi=aliasmap[i]+".";
				String ads=tablemap[i]+"." ;
				sqlText=sqlText.replace(alsi,ads);
			}
		}
		String sqlbuilder[]=parse.sqlWordExtractor(sqlText);

		for (int j = 0; j < sqlbuilder.length; j++) {
			if(sqlbuilder[j].contains(".")&&sqlbuilder[j].contains("EDW_")){
				sqlbuilder[j+1]="";
			}
		}
		String built="";
		for (int j = 0; j < sqlbuilder.length; j++) {
			built=built+sqlbuilder[j]+" ";
		}
		for(int i=0;i<10;i++)
			built=built.replaceAll("  "," ");
		if(sqlText.contains("UNION ALL"))
			sqlText=sqlText.replaceAll("UNION ALL","UNION_ALL");
		

		return built;
	
	
	}

}





