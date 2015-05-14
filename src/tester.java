package com.appliedenergetics.weaver;

import java.lang.*;
import java.io.*;
import java.net.*;

public class tester
{
	public static void debug0()
	{	
		int p1 = 13370;
		int p2 = 12345;
		weaver.createproxyport(p1, p2);
	}
	public static void debug1()
	{
		int port = 13370;
		connectionacceptor ca = new connectionacceptor(port);
		ca.start();
		while(ca.accepted == false)
		{
			try
			{
				Thread.sleep(100);
			}
			catch(Exception e)
			{
			}
		}
		weaver.printinputstream(ca.getconnection().istream);
	}
	public static void debug2()
	{
		String url = "192.168.1.10";
		int port1 = 0;
		int port2 = 5000;
		printarray(weaver.portscan(url, port1, port2));
	}
	public static void debug3()
	{
		int port = 13370;
		int max = 4;
		chatroom chat = new chatroom(port, max);
		chat.start();
	}
	public static void debug4()
	{
		String localhost = "127.0.0.1";
		weaver.porttoconnection(13370, localhost, 13371);
	}
	public static void main(String[] args)
	{
		debug4();
	}
	public static void printarray(boolean[] in)
	{
		for(int x = 0; x < in.length; x++)
		{
			System.out.println(x + ": " + in[x]);
		}
	}
	public static void printarray(int[] in)
	{
		for(int i: in)
		{
			System.out.println(i);
		}
	}
	public static void printarray(String[] in)
	{
		for(String i: in)
		{
			System.out.println(i);
		}
	}
}
