package vz.parser.query;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import vz.parser.helper.SQLHelper;
import vz.parser.parse.Parse;
import vz.parser.parse.Queries;
import vz.parser.parse.SQLResolver;

public class ProcessInsert {
	String setFunc[]={"UNION", "UNION_ALL", "INTERSECT", "MINUS"};

	SQLHelper sqlHelp=new SQLHelper();
	Parse parse=new Parse();
	SQLResolver resolver=new SQLResolver();
	ProcessSelect ps=new ProcessSelect();
	public void processInsertion(String sqlText)
	{
		String parsedRows[]=new String[5];
		//String sqlText=resolver.getSqlText();
		String sqlW[]=parse.sqlWordExtractor(sqlText);
		String pschemaTb=sqlW[2];

		//System.out.println(pschemaTb);

		String sqlWo[]=pschemaTb.split("[.]");
		String pDB=sqlWo[0];
		String pTB=sqlWo[1];
		String cols=sqlText.substring(sqlText.indexOf("(") + 1, sqlText.indexOf(")"));
		cols=cols.replaceAll("\n","");
		cols=cols.replaceAll("\r","");
		cols=cols.replaceAll(" ","");
		String colsArr[]=cols.split(",");
		//System.out.println(colsArr.length);
		for (int i=0;i<colsArr.length;i++) {


			parsedRows[0]="INSERT";
			parsedRows[1]=resolver.getSqlId()+"";
			parsedRows[2]=pDB;
			parsedRows[3]=pTB;
			parsedRows[4]=colsArr[i];

			sqlHelp.INSERT("Hackathon_targetTB", "'"+parsedRows[0]+"','"+parsedRows[1]+"','"+parsedRows[2]+"','"+parsedRows[3]+"','"+parsedRows[4]+"'");


		}
		srcSelect(sqlText,colsArr);
	}

	public void srcSelect(String sqlText,String[] tgCols){
		boolean setP=false;
		String setTransform="";
		String setT="";
		//System.out.println(sqlText);
		String sourceString=sqlText.substring(sqlText.indexOf("SELECT"));
		//System.out.println(sourceString);
		//sourceString=resolver.resolveAlias(sourceString).trim();

		String selArr[]=new String[2];
		for (String set : setFunc) {
			if(sourceString.contains(set)){
				selArr=sourceString.split(set);
				setT=set;
				setP=true;
				break;
			}
		}
		if(setP){
			for (int i=0;i<2;i++) {
				String string=selArr[i]+";";
				if(i==0)
					setTransform="NA";
				else if (i==1)
					setTransform=setT;

				//ps.processSelection(string, sqlText,tgCols,setTransform);
				Selection(string, sqlText,tgCols,setTransform);
			}
		}
		else
			//ps.processSelection(sourceString.trim()+";", sqlText, tgCols,setTransform);
		Selection(sourceString.trim()+";", sqlText, tgCols,setTransform);

	}

	public void Selection(String string, String sqlText,String[] tgCols, String setTransform)
	{
		string=resolver.resolveAlias(string).trim();
		String tgtbl="";
		String srcTbl="";
		String srcCol="";
		String srcSchema="";
		String schema[]=new String[50];
		String tbl[]=new String[50];
		boolean subq=false;
		System.out.println(string);
		String colmn=string.substring(string.indexOf("SELECT")+6, string.indexOf("FROM"));
		String sqlW[]=parse.sqlWordExtractor(string);

		//System.out.println(colmn);
		colmn=colmn.replaceAll("\n"," ");
		colmn=colmn.replaceAll("\r","");
		String subquery="";
		String sqlWo[]=colmn.split(",");
		String sd="";
		int c=0;
		String DBTB[]=new String [100];
		for(int i=0;i<sqlW.length;i++)
		{
			if(sqlW[i].equals("FROM")||sqlW[i].equals("JOIN"))
			{
				DBTB[c]=sqlW[i+1];
				if(sqlW[i].equals("JOIN")&&setTransform.equals("NA"))
					sd=DBTB[c].split("[.]")[1];
				c++;
			}


		}
		c=0;
		for(int i=0;i<DBTB.length;i++)
		{
			String aliTB[]=new String [2];
			if((DBTB[i]!=null)&&(DBTB[i].contains("."))){
				aliTB=DBTB[i].split("[.]");
				schema[c]=aliTB[0];
				tbl[c]=aliTB[1];
				c++;
			}
			else if((DBTB[i]!=null)&&(!DBTB[i].contains("."))){
				subquery=string.substring(string.indexOf("FROM")+6);
				Selection(subquery+";", sqlText, tgCols,setTransform);
				//System.out.println(subquery);
				subq=true;
			}
		}

		for(int i1=0;i1<10;i1++)
			colmn=colmn.replaceAll("  "," ");

		for (int j=0; j<sqlWo.length; j++) {
			//String string2 : sqlWo
			if(sqlWo[j].contains(".")){
				String colTB[]=sqlWo[j].split("[.]");
				srcTbl=colTB[0];
				srcCol=colTB[1];

				String fromWhere=string.substring(string.indexOf("FROM"), string.indexOf("WHERE"));
				String sqlFW[]=parse.sqlWordExtractor(fromWhere);

				if(fromWhere.contains("JOIN"))
				{
					String sub1=fromWhere.substring(fromWhere.indexOf("FROM")+4, fromWhere.indexOf("JOIN"));
					/**To find number of joins**/	
					int k = 0;
					Pattern p = Pattern.compile("JOIN");
					Matcher m = p.matcher( fromWhere );
					while (m.find()) {
						k++;
					}
					/***************************/
					if(k>1)
					{
						String sub11=fromWhere.substring(fromWhere.indexOf("FROM")+4, fromWhere.indexOf("JOIN"));

					}
				}


				for (int i=0;i<tbl.length;i++) {
					if((tbl[i]!=null)&&(srcTbl.trim().equals(tbl[i]))){
						srcSchema=schema[i];
						break;
					}
				}
			}
			if((setTransform.equals("NA"))&&(sd.equals(srcTbl.trim())))
				setTransform="JOIN";
			else if((setTransform.equals("JOIN"))&&(!sd.equals(srcTbl.trim())))
				setTransform="NA";
			else if((setTransform.equals("JOIN"))&&(sd.equals(srcTbl.trim())))
				setTransform="JOIN";
			if(subq)
				continue;
			sqlHelp.INSERT("Hackathon_srcTB", "'"+resolver.getSqlId()+"','"+tgCols[j]+"','"+srcSchema.trim()+"','"+srcTbl.trim()+"','"+srcCol.trim()+"','"+setTransform+"'");



			String whereStr;
			if(string.contains("GROUP"))
				whereStr=string.substring(string.indexOf("WHERE")+6,string.indexOf("GROUP"));	
			else if(string.contains("ORDER"))
				whereStr=string.substring(string.indexOf("WHERE")+6,string.indexOf("ORDER"));		
			else
				whereStr=string.substring(string.indexOf("WHERE")+6);	
			String sqlFW[]=parse.sqlWordExtractor(whereStr);

			tgtbl=sqlFW[0].split("[.]")[0];
			String tblTransform[]=whereStr.split("AND", 0);
			for (String string2 : tblTransform) {
				//System.out.println(string2);
			}
			//System.out.println(whereStr);
			String trans[]=sqlFW[0].split("\n");

			for(String str: trans)
			{
				if(str!=null)
					sqlHelp.INSERT("Hackathon_whereTB","'"+resolver.getSqlId()+"','"+tgtbl+"','"+str+"'");
			}
		}
	}
}
//}
