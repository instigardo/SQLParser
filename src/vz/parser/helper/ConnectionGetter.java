package vz.parser.helper;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class ConnectionGetter
{
	Connection con;
	public ConnectionGetter()
	{
	try{
		Class.forName("com.teradata.jdbc.TeraDriver");
		con = DriverManager.getConnection("jdbc:teradata://138.83.86.91", "guptde2", "k3CniDkB" );

		//System.out.println("sddsad");
		
		} 
	catch (Exception e)
	{
			 //TODO Auto-generated catch block
			System.out.println("Connection Error");
		}
	}

	public Connection getConnection()
	{
		return con;
	}
	public static void main(String[] args) {
		ConnectionGetter cg=new ConnectionGetter();
		
	}
	}

