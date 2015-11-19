package vz.parser.query;

import vz.parser.helper.SQLHelper;
import vz.parser.parse.Parse;
import vz.parser.parse.SQLResolver;

public class ProcessDelete {
	SQLHelper sqlHelp=new SQLHelper();
	Parse parse=new Parse();
	SQLResolver resolver=new SQLResolver();

	public void processDeletion(String sqlText)
	{
		String[] parsedRows=new String[5];
		String schema="";
		String tbl="";
		String built=resolver.resolveAlias(sqlText);
		//System.out.println(built);
		//DELETE(String tName, String where)

		//System.out.println(sqlText);
		String tname=sqlText.substring(sqlText.indexOf("FROM")+4 , sqlText.indexOf(" WHERE "));
		String tnamepseudo=sqlText.substring(sqlText.indexOf("DELETE") , sqlText.indexOf(" WHERE ")+6);
		String where=sqlText.replace(tnamepseudo, "");

		parsedRows[0]="DELETE";
		parsedRows[1]=resolver.getSqlId()+"";
		parsedRows[2]=schema;
		parsedRows[3]=tbl;
		parsedRows[4]="*";
		sqlHelp.INSERT("Hackathon_targetTB", "'"+parsedRows[0]+"','"+parsedRows[1]+"','"+parsedRows[2]+"','"+parsedRows[3]+"','"+parsedRows[4]+"'");
		sqlHelp.INSERT("Hackathon_srcTB", "'"+resolver.getSqlId()+"','"+parsedRows[4]+"','"+"NA"+"','"+"NA"+"','"+"NA"+"','"+"NA"+"'");


		where=where.replace(";", "");


		String sqlW1[]=tname.split("[.]");
		tbl=sqlW1[0].trim(); 
		schema=sqlW1[1].trim(); 
		String sel[]=new String[50];
		int i=0;
		while(where.contains("("))
		{
			sel[i]=where.substring(where.indexOf("(") , where.indexOf(")")+1);
			where=where.replace(sel[i], "DUMP-"+i);
			System.out.println(sel[i]);
			i++;
		}

		where=where.replaceAll("AND", "INSTIGARDO");
		where=where.replaceAll("OR", "INSTIGARDO");
		String index="";
		int idx=0;
		String clauses[]=where.split("INSTIGARDO");
		for (String string : clauses) {
			String sqlta[]=parse.sqlWordExtractor(string);
			for (String string2 : sqlta) {
				if(string2.contains("DUMP")){
					String brk[]=string2.split("-");
					index=brk[1].trim();
					idx=Integer.parseInt(index);
					string=string.replace("DUMP-"+idx, sel[idx]);
				}
			}

			sqlHelp.INSERT("Hackathon_whereTB","'"+resolver.getSqlId()+"','"+tbl+"','"+string+"'");
		}

	}
}
