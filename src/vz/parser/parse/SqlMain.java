package vz.parser.parse;

import java.io.IOException;

import vz.parser.helper.ReadExcel;

public class SqlMain {

	public static void main(String[] args) throws IOException {
		SQLResolver resolver=new SQLResolver();
		Queries query=new Queries();
		ReadExcel rx=new ReadExcel();
		rx.excel();
		String[] id=rx.getSqlId();
		
int i=1;
		
			System.out.println("start");
			
			resolver.setSqlId(i);
			query.parseInit(i);
			System.out.println("end");


			//resolve.getSql(i);
			//resolve.processSql();

			
		}
		
	}

