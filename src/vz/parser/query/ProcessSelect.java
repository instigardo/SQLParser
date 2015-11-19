package vz.parser.query;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import vz.parser.helper.SQLHelper;
import vz.parser.parse.Parse;
import vz.parser.parse.SQLResolver;

public class ProcessSelect {
	String setFunc[]={"UNION", "UNION_ALL", "INTERSECT", "MINUS"};

	SQLHelper sqlHelp=new SQLHelper();
	Parse parse=new Parse();
	SQLResolver resolver=new SQLResolver();

	public void processSelection(String string, String sqlText ,String[] tgCols, String setTransform)
	{
		string=resolver.resolveAlias(string).trim();
		//		SELECT 
		//				[SRC columns] 
		//				[ FROM table_source ] 
		//				[ WHERE search_condition ] 
		//				[ GROUP BY group_by_expression ] 
		//				[ HAVING search_condition ] 
		//				[ ORDER BY order_expression [ ASC | DESC ] ] 

		String coalesce[]=new String[50];
		int indexOpen=0;
		int indexClose=0;
		int index=0;
		int idx=0;
		while(string.contains("COALESCE"))
		{
			int count=0;
			String str="";
			String sqlW[]=parse.sqlWordExtractor(string);
			for (int i = 0; i < sqlW.length; i++) {
				if(sqlW[i].equals("COALESCE")){
					indexOpen=i;
					break;
				}
			}

			for (int i = indexOpen; i < sqlW.length; i++) {

				if(sqlW[i].equals("(")){
					count++;
				}
				if(sqlW[i].equals(")")){
					count--;
					if(count==0){
						indexClose=i;
						break;
					}
				}
			}

			for(int j=indexOpen;j<=indexClose;j++){
				str=str+sqlW[j]+" ";
			}

			coalesce[idx]=str;
			string=string.replace(str, " FIFA-"+idx+" ");
			//System.out.println(str);
			idx++;
		}
		for(int i=0;i<10;i++)
			string=string.replaceAll("  "," ");

		//System.out.println(string);

		// [SRC Columns]

		String srcColumns=string.substring(string.indexOf("SELECT ")+6, string.indexOf("FROM"));
		//System.out.println(srcColumns);
		String srcColumnsArr[]=srcColumns.split(",");
		for (String string2 : srcColumnsArr) {
			string2=string2.trim();
			//			System.out.println(string2);
		}

		// [FROM TblSource { SrcSchema.SrcTable },{ SUBQUERY }]

		String subSelect="";
		String limit=";";
		String Tblsrc="";
		String subUrb=string.substring(string.indexOf("FROM"));


		String subTemp=subUrb;
		//System.out.println(subUrb);
		int count=0;
		String str="";
		indexOpen=0;
		int ice=0;
		String sub[]=new String[100];
		while(subTemp.contains("FROM ( SELECT"))
		{
			int count1=0;
			String str1="";
			String sqlW[]=parse.sqlWordExtractor(subTemp);
			for (int i = 0; i < sqlW.length; i++) {
				if(sqlW[i].equals("(")){
					indexOpen=i;
					break;
				}
			}

			for (int i = indexOpen; i < sqlW.length; i++) {

				if(sqlW[i].equals("(")){
					count1++;
				}
				if(sqlW[i].equals(")")){
					count1--;
					if(count1==0){
						indexClose=i;
						break;
					}
				}
			}

			for(int j=indexOpen;j<=indexClose;j++){
				str1=str1+sqlW[j]+" ";
			}
			subSelect=str1.substring(str1.indexOf("(")+1, str1.lastIndexOf(")"));
			sub[ice]=subSelect;
			//System.out.println(subSelect);
			subTemp=subTemp.replace(str1, " FART-"+ice+" ");
			ice++;	
		}


		//System.out.println(subTemp);
		String arr[]={"WHERE","GROUP", "HAVING", "ORDER"};
		for (String string2 : arr) {
			if(subTemp.contains(string2))
			{
				Tblsrc=string.substring(string.indexOf("FROM")+4,string.indexOf(string2));
				break;
			}
			else
				Tblsrc=string.substring(string.indexOf("FROM")+4,string.indexOf(";"));
		}
		if(Tblsrc.contains(" SELECT ")){

			for (String string2 : sub) {
				if(string2!=null){
					if(string2.contains(" SELECT "))
					{

						processSelection(string2.trim()+";", sqlText, tgCols,setTransform);
					}
				}



				else
				{

					String dbtb="";
					String srcSchema="";
					String srcTbl="";
					String joinarr[]={"JOIN", "INNER JOIN", "OUTER JOIN", "LEFT JOIN", "RIGHT JOIN", "FULL JOIN"};
					for (String string3 : joinarr) {
						if(Tblsrc.contains(string3))
							Tblsrc=Tblsrc.replace(string3, "JOIN");
					}
					String tbl[]=Tblsrc.split("JOIN");
					int i=0;
					for (String string3 : tbl) {

						string3=string3.trim();
						String transform;
						if(string3.contains("ON"))
							transform="JOIN ON "+string3.split("ON")[1];
						else
							transform="NA";

						String sqlW[]=parse.sqlWordExtractor(string3);
						for (int j = 0; j < sqlW.length; j++) {
							if(sqlW[j].contains(".")&&sqlW[j].contains("EDW_")){
								dbtb=sqlW[j];
								String split[]=dbtb.split("[.]");
								srcSchema=split[0].trim();
								srcTbl=split[1].trim();

							}
						}



						if(srcColumnsArr[i].contains(".")){
							srcColumnsArr[i]=srcColumnsArr[i].split("[.]")[1];
						}
						//System.out.println(srcColumnsArr[i]);
						//System.out.println(string3.trim());
						sqlHelp.INSERT("Hackathon_srcTB", "'"+resolver.getSqlId()+"','"+tgCols[i].trim()+"','"+srcSchema.trim()+"','"+srcTbl.trim()+"','"+srcColumnsArr[i].trim()+"','"+transform+"'");
						i++;

						if(Tblsrc.contains("WHERE")){
							String whereStr;
							if(Tblsrc.contains("GROUP"))
								whereStr=Tblsrc.substring(Tblsrc.indexOf("WHERE")+6,Tblsrc.indexOf("GROUP"));	
							else if(Tblsrc.contains("ORDER"))
								whereStr=Tblsrc.substring(Tblsrc.indexOf("WHERE")+6,Tblsrc.indexOf("ORDER"));		
							else
								whereStr=Tblsrc.substring(Tblsrc.indexOf("WHERE")+6);	
							String sqlFW[]=parse.sqlWordExtractor(whereStr);

							String tgTbl=sqlFW[0].split("[.]")[0];
							String tblTransform[]=whereStr.split("AND", 0);

							//System.out.println(whereStr);
							String trans[]=sqlFW[0].split("\n");

							for(String str1: trans)
							{
								if(str1!=null)
									sqlHelp.INSERT("Hackathon_whereTB","'"+resolver.getSqlId()+"','"+tgTbl+"','"+str1+"'");
							}

						}
							
					}
					//System.out.println(string);
					//System.out.println(Tblsrc);
					System.out.println("here");
					break;
				}
			}
		}
	}
}
