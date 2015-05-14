package com.appliedenergetics.weaver;

public class main
{
	public static void delay(int millis)
	{
		try
		{
			Thread.sleep(millis);
		}
		catch(Exception e)
		{
			System.out.println("exception in main loop");
		}
	}
	public static void debug1()
	{
		connectionacceptor c = new connectionacceptor(13370, true);
		c.write("poop", true);
		c.start();
		System.out.println("f1");
		String stuff;
		while(true)
		{
			stuff = c.read();
			if(stuff.length() != 0)
			{
				System.out.println(stuff);
			}
			
		}
	}
	public static void debug2()
	{
		connectionacceptor i = new connectionacceptor(13370);
		i.write("dude", true);
		i.start();
		while(true)
		{
			if(i.available() > 0)
				System.out.println(i.read());
			delay(1000);
			if(i.writebuffer.length() == 0)
			{
				i.write("poop", true);
			}
		}
	}
	public static void debug3()
	{
		server s = new server(13370);
		s.write("dude");
		s.start();
		while(true)
		{
			delay(1000);
			if(s.available() > 0)
				System.out.println(s.read());
		}
		
	}
	public static void debug4()
	{
		//String[] ips = weaver.scansubnet("192.168.1.1", 80, 80);
		//tester.printarray(ips);
	}
	public static void debug5()
	{
		int[] ports = weaver.portscan("192.168.1.1", 0, 100);
		System.out.println(ports.length);
	}
	public static void debug6()
	{
		System.out.println(weaver.getip());
	}
	public static void main(String[] args)
	{
		debug6();
	}
}