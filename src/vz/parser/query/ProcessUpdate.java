package vz.parser.query;

import vz.parser.helper.SQLHelper;
import vz.parser.parse.Parse;
import vz.parser.parse.SQLResolver;

public class ProcessUpdate {

	SQLHelper sqlHelp=new SQLHelper();
	Parse parse=new Parse();
	SQLResolver resolver=new SQLResolver();

	public void processUpdation(String sqlText)
	{
		String[] parsedRows=new String[5];
		String Alias[]=new String[10];
		String tbl[]=new String[10];
		String built=resolver.resolveAlias(sqlText);
		System.out.println(built);
		//"update " + tName + " set " + cName + " where " + where;

		//System.out.println(sqlText);
		String tname=sqlText.substring(sqlText.indexOf("UPDATE")+6 , sqlText.indexOf(" SET "));
		String tnamepseudo=built.substring(built.indexOf("UPDATE")+6 , built.indexOf(" SET "));
		String sqtext=built.replace(tnamepseudo, "");
		String cname=sqtext.substring(sqtext.indexOf(" SET ")+4, sqtext.indexOf("WHERE"));
		String where=sqtext.substring(sqtext.indexOf("WHERE")+5, sqtext.indexOf(";"));
		//String sqlWr[]=parse.sqlWordExtractor(tname);
		//String tblname=sqlWr[0];

		String tblStr= sqlText.substring(sqlText.indexOf("FROM")+4 , sqlText.indexOf(" SET "));
		//System.out.println(tblStr);
		String sqlW[]=tblStr.split(",",2);
		int i=0,cc=0;
		for (String string : sqlW) {
			if(string.contains("SELECT")){
				tbl[cc]=string.substring(string.indexOf("SELECT"),string.indexOf(")")); 
				Alias[cc]=string.substring(string.indexOf(")")+1).trim(); 
				cc++;
			}
			else {
				String sqlW1[]=parse.sqlWordExtractor(string.trim());
				tbl[cc]=sqlW1[0].trim(); 
				Alias[cc]=sqlW1[1].trim(); 
				cc++;
			}
		}
		String pDB="";
		String pTB="";
		String stemp[]=new String[2];
		String tblExt= sqlText.substring(sqlText.indexOf("UPDATE")+6 , sqlText.indexOf("FROM")).trim();
		System.out.println(tblExt);
		for (int j = 0; j < Alias.length; j++) {

			if(Alias[j].equals(tblExt))
			{
				String DBTB=tbl[j].trim();
				stemp=DBTB.split("[.]");
				pDB=stemp[0];
				pTB=stemp[1];
				break;
			}

		}

		String aliasTbl[]=new String[50];
		String schemamap[]=new String[50];
		String tablemap[]=new String[50];

		//System.out.println(pDB+" "+pTB);
		String sqlta[]=parse.sqlWordExtractor(sqlText);
		int u=0;
		for (int j = 0; j < sqlta.length; j++) {
			if(sqlta[j].contains(".")&&sqlta[j].contains("EDW_")){
				aliasTbl[u]=sqlta[j];
				String split[]=aliasTbl[u].split("[.]");
				schemamap[u]=split[0].trim();
				tablemap[u]=split[1].trim();
				u++;
			}
		}

		parsedRows[0]="UPDATE";
		parsedRows[1]=resolver.getSqlId()+"";
		parsedRows[2]=pDB;
		parsedRows[3]=pTB;

		String colsArr[]=new String [20];
		String srcArr[]=new String [20];
		String cols[]=cname.trim().split(",");
		String srcSchema="";
		String srcTbl="";
		String srcCol="";


		cc=0;
		for (String string : cols) {
			String tgC[]=string.split("=");
			colsArr[cc]=tgC[0].trim();
			srcArr[cc]=tgC[1].trim();
			cc++;
		}
		for (int j = 0; j < colsArr.length; j++) {
			if(colsArr[j]==null)
				break;
			parsedRows[4]=colsArr[j];

			String srcTBcol[]= srcArr[j].split("[.]");
			if(srcArr[j].contains(".")){
			srcTbl=srcTBcol[0].trim();
			srcCol=srcTBcol[1].trim();
			}
			else{
				srcTbl="NA";
				srcCol=srcTBcol[0].trim();
			}
				for (int k = 0; k < tablemap.length; k++) {
				if(srcTbl.equals(tablemap[k]))
				{srcSchema=schemamap[k];
				break;}
			}
				if(srcSchema.equals("")){
					if(!srcTbl.equals("NA"))
						srcSchema="Dynamic View";
					else
						srcSchema="NA";
				}
			
			sqlHelp.INSERT("Hackathon_targetTB", "'"+parsedRows[0]+"','"+parsedRows[1]+"','"+parsedRows[2]+"','"+parsedRows[3]+"','"+parsedRows[4]+"'");
			sqlHelp.INSERT("Hackathon_srcTB", "'"+resolver.getSqlId()+"','"+parsedRows[4]+"','"+srcSchema.trim()+"','"+srcTbl.trim()+"','"+srcCol.trim()+"','"+"NA"+"'");
		}


		//System.out.println(where);
		where=where.replaceAll("AND", "INSTIGARDO");
		where=where.replaceAll("OR", "INSTIGARDO");

		String clauses[]=where.split("INSTIGARDO");
		for (String string : clauses) {
			sqlHelp.INSERT("Hackathon_whereTB","'"+resolver.getSqlId()+"','"+parsedRows[3]+"','"+string+"'");
			
		}
	}
}

/**
		String parsedRows[]=new String[5];
		//String sqlText=resolver.getSqlText();
		String sqlW1[]=parse.sqlWordExtractor(sqlText);
		String pschemaTb=sqlW1[2];

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


			parsedRows[0]="UPDATE";
			parsedRows[1]=resolver.getSqlId()+"";
			parsedRows[2]=pDB;
			parsedRows[3]=pTB;
			parsedRows[4]=colsArr[i];



		}
		srcSelect(sqlText,colsArr);
	}

	public void srcSelect(String sqlText,String[] tgCols){
		boolean setP=false;
		String setTransform="";
		String setT="";
		String sourceString=sqlText.substring(sqlText.indexOf("SELECT"), sqlText.indexOf(";"));
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

				Selection(string, sqlText,tgCols,setTransform);
			}
		}
		else
			Selection(sourceString+";", sqlText, tgCols,setTransform);

	}

	public void Selection(String string, String sqlText,String[] tgCols, String setTransform)
	{
		String tgtbl="";
		String srcTbl="";
		String srcCol="";
		String srcSchema="";
		String schema[]=new String[50];
		String tbl[]=new String[50];
		boolean subq=false;
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
					/**To find number of joins**	
					int k = 0;
					Pattern p = Pattern.compile("JOIN");
					Matcher m = p.matcher( fromWhere );
					while (m.find()) {
						k++;
					}
					/***************************
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
			sqlHelp.INSERT("EDW_BASE.Hackathon_srcTB", "'"+resolver.getSqlId()+"','"+tgCols[j]+"','"+srcSchema.trim()+"','"+srcTbl.trim()+"','"+srcCol.trim()+"','"+setTransform+"'");



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
					sqlHelp.INSERT("EDW_BASE.Hackathon_whereTB","'"+resolver.getSqlId()+"','"+tgtbl+"','"+str+"'");
			}
		}
	}
}
//}


//		String parsedRows[]=new String[5];
//		//String sqlText=resolver.getSqlText();
//		String sqlW[]=parse.sqlWordExtractor(sqlText);
//		String pschemaTb=sqlW[2];
//
//		System.out.println(pschemaTb);
//		
//		String sqlWo[]=pschemaTb.split("[.]");
//		String pDB=sqlWo[0];
//		String pTB=sqlWo[1];
//		String cols=sqlText.substring(sqlText.indexOf("(") + 1, sqlText.indexOf(")"));
//		cols=cols.replaceAll("\n","");
//		cols=cols.replaceAll("\r","");
//		cols=cols.replaceAll(" ","");
//		String colsArr[]=cols.split(",");
//		//System.out.println(colsArr.length);
//		for (int i=0;i<colsArr.length;i++) {


//			parsedRows[0]="UPDATE";
//			parsedRows[1]=resolver.getSqlId()+"";
//			parsedRows[2]=pDB;
//			parsedRows[3]=pTB;
//			parsedRows[4]=colsArr[i];
//		
//sqlHelp.INSERT("EDW_BASE.Hackathon_targetTB", "'"+parsedRows[0]+"','"+parsedRows[1]+"','"+parsedRows[2]+"','"+parsedRows[3]+"','"+parsedRows[4]+"'");

//srcSelect(sqlText,colsArr);
//}
}
}**/
