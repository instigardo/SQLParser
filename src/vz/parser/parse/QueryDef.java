package vz.parser.parse;

import java.io.IOException;

public class QueryDef {
	Parse parse=new Parse();
	SQLResolver resolver=new SQLResolver();
	
	public void qdInsert(String sqlText) throws IOException
	{
		
		String sqlW[]=parse.sqlWordExtractor(sqlText);
		String keyW[]=parse.extractKeywords(sqlText);
		
	}
	
}
