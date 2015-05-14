package com.appliedenergetics.weaver;

//a hyperthreaded class that allows you to read data from a connection in a non-blocking manner
class connectionreader extends Thread
{
	private static int intervallength = 300;
	private connection con;
	private int timeout;
	private String output;
	
	public connectionreader(connection con, int timeoutmillis, String output)
	{
		con = con;
		timeout = timeoutmillis;
		output = output;
	}
	public static void delay(int millis)
	{
		try
		{
			Thread.sleep(millis);
		}
		catch(Exception e)
		{
			System.out.println("could not delay connectionlistener");
		}
	}
	public void run()
	{
		if(con.available() > 0)
		{
			output = con.readline();
		}
		int intervals = (int)(timeout/intervallength);
		for(int x= 0; x< intervals; x++)
		{
			delay(intervallength);
			if(con.available() > 0)
				output = con.readline();
		}
	}
}