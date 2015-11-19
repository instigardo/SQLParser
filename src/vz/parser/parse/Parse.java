package vz.parser.parse;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import vz.parser.helper.SQLHelper;

public class Parse {

	SQLHelper sqlHelp=new SQLHelper();

	String primaryOperation[]={"INSERT", "DELETE", "UPDATE"};
	String Database[]=new String[50000];
	String tableDatabase[]=new String[50000];
	String SQLString;
	//String SQLWords[]=new String[50000];

	public void getTablesDB()
	{
		ResultSet rs=sqlHelp.SELECTNW("dbc.tables", "distinct DatabaseName");
		int i=0;
		try {
			while(rs.next())
			{
				Database[i]=rs.getString("DatabaseName");
				System.out.println(Database[i]);
				i++;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}		
	}

	public void getTables(String DB)
	{

		ResultSet rs=sqlHelp.SELECT("dbc.tables", "TableName", "DatabaseName="+DB);
		int i=0;
		try {
			while(rs.next())
			{
				tableDatabase[i]=rs.getString("TableName");
				System.out.println(tableDatabase[i]);
				i++; 

			}
		} catch (SQLException e) {
			e.printStackTrace();
		}				
	}

	public String getSQLString(String fileName) throws IOException
	{
		BufferedReader br = new BufferedReader(new FileReader(fileName));
		try {
			StringBuilder sb = new StringBuilder();
			String line = br.readLine();
			while (line != null) {
				if(line.contains("--")){

					//	System.out.println("skip");
				}else
					sb.append(line);
				sb.append(System.lineSeparator());
				line = br.readLine();		        
			}
			SQLString = sb.toString().toUpperCase();
			SQLString=SQLString.replaceAll("/\\*(.*?)\\*/", "");
			return SQLString;
		} finally {
			br.close();
		}
	}

	public String[] getKeywords() {
		String keywords[]=new String[1060];
		ResultSet rs=sqlHelp.SELECTNW("SYSLIB.SQLRestrictedWords", "restricted_word");
		int i=0;
		try {
			while(rs.next())
			{
				keywords[i]=rs.getString("restricted_word");
				//System.out.println(keywords[i]);
				i++; 
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}				
		return keywords;
	}

	public String[] sqlWordExtractor(String str){
		//System.out.println(str);
		str=str.replaceAll("\n"," ");
		str=str.replaceAll("\r","");
		for(int i=0;i<10;i++)
			str=str.replaceAll("  "," ");

		String sqlWords[]=str.split(" ");		
		return sqlWords;
	}

	public String resolver(String str)
	{
		int i=0;
		String alias[]=new String [50000];
		String SQLWords[]=sqlWordExtractor(str);
		for (String string : SQLWords) {
			if(string.contains("."))
			{
				alias[i]=string;
				System.out.println(alias[i]);
				i++;
			}
		}

		return null;
	}
	
	String used[]=new String [4000];
	int count=0;
	boolean a=false;
	public String[] extractKeywords(String str, String[] keywords) throws IOException{
		int index=0;
		boolean sql=false;
		String extractKeywords[]=new String[50000] ;
		String SQLKeywords[]=keywords;
		String SQLWords[]=sqlWordExtractor(str);		
		/*****************************************/	
		for (int j = 0; j<SQLWords.length; j++) {

			for (int j2 = 0; j2<SQLKeywords.length; j2++) {
				if(SQLWords[j].equals(SQLKeywords[j2])){
					for(int k=0;k<used.length;k++){
						if(SQLWords[j].equals(used[k]) &&  used[k]!=null){
							a=true;
							
						}
					}
					if(!a){
						sql=true;
						extractKeywords[index]=SQLWords[j];
						used[count]=SQLWords[j];
						count++;
						index++;
						a=false;
						break;
					}else a=false;
				}
				if(j2==1059)
					break;	
			}
		}
		/*******************************************/	
		return extractKeywords;
	}

	public String formatter(String str){
		str=str.toUpperCase();
		str=str.replaceAll("--(.*?)\n", "");
		str=str.replace("/\\*", " /\\* ");
		str=str.replace("\\*/", " \\*/ ");
		str=str.replaceAll("\n"," ");
		str=str.replace("\r","");
		str=str.replaceAll("/\\*(.*?)\\*/", "");

		for(int i=0;i<10;i++)
			str=str.replace("  "," ");
		str=str.replace("(", " ( ");
		str=str.replace(")", " ) ");
		str=str.toUpperCase();

		return str;
	}

	public static void main(String[] args) throws IOException {

		Parse parse=new Parse();
		String SQLString="insert into  insert select from edw_metadata.notification_queue  ( src_sys_id, cycle_num, prod_year_month, prcs_action, prcs_status, prcs_name, prcs_type_code, prcs_ctrl_id )  with prctl  ( src, ym, cyc, prcscode, prcsname, retry, status, prcsid )  as  ( select src_sys_num src, prod_year_month ym, prcs_cyc_num cyc, prcs_type_code prcscode, prcs_name prcsname, retry_num retry, prcs_stat_code status, prcs_ctrl_sk prcsid from edw_metadata.prcs_ctrl where prcs_type_code = '<prcs_type>' and prcs_name = 'CPE_CUST_LOCN_SITE.SVC_ACCT_ALT_IDN_RELSP' and src_sys_num = <sys> and prcs_ctrl_sk not in  (  select file_prcs_ctrl_sk from edw_metadata.file_prcs_log a inner join edw_metadata.file_ref b on a.FILE_PRCS_ID=b.FILE_PRCS_ID where b.prcs_type_code ='<prcs_type>' and a.SYS_ID =<sys>  )   )  select <sys> src_sys_id, <cyc> cycle_num, '<rev_year_month>' prod_year_month, 'EMAIL' prcs_action, 'FF' prcs_status, 'CPE_CUST_LOCN_SITE.SVC_ACCT_ALT_IDN_RELSP' prcs_name, '<prcs_type>' prcs_type_code, <ctrl> prcs_ctrl_id from prctl where 1 <=  (  select count ( * )  from prctl  )  and cyc =  (  select case when <cyc> = 1 then  ( select max ( cyc )  cy from prctl where ym = cast ( extract ( year from add_months ( cast ( '<rev_year_month>' || '01' as date format 'YYYYMMDD' ) , -1 )  )  as char ( 4 )  )  || Substring ( '00' From 1 For 2-Chars ( Trim ( extract ( month from add_months (  cast ( '<rev_year_month>' || '01' as date format 'YYYYMMDD' ) , -1 )  )  )  )  )  || Trim ( extract ( month from add_months (  cast ( '<rev_year_month>' || '01' as date format 'YYYYMMDD' ) , -1 )  )  )   )  else <cyc> - 1 end cyc  )  and ym =  (  select case when <cyc> = 1 then cast ( extract ( year from add_months ( cast ( '<rev_year_month>' || '01' as date format 'YYYYMMDD' ) , -1 )  )  as char ( 4 )  )  || Substring ( '00' From 1 For 2-Chars ( Trim ( extract ( month from add_months (  cast ( '<rev_year_month>' || '01' as date format 'YYYYMMDD' ) , -1 )  )  )  )  )  || Trim ( extract ( month from add_months ( cast ( '<rev_year_month>' || '01' as date format 'YYYYMMDD' ) ,-1 )  )  )  else '<rev_year_month>' end )  and status in ( 'F', 'P', 'X' )  having count ( * )  <> 0;"; 
		String keywords[]=parse.getKeywords();
		SQLString=parse.formatter(SQLString);
		String a[]=parse.extractKeywords(SQLString, keywords);
		for (String string : a) {
			if(string!=null)
			System.out.println(string);
		}
		System.out.println(SQLString);
		//parse.resolver(SQLString);

		//parse.getTables("'EDW_TEMP'");

	}


}
