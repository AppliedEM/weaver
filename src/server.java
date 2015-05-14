package com.appliedenergetics.weaver;

import java.lang.*;
import java.io.*;
import java.net.*;

public class server extends Thread
{
	private connectionacceptor[] conns;
	private int[] ports;
	private boolean keeprunning;
	
	public server(int[] port)
	{
		keeprunning = true;
		ports = port;
		conns = new connectionacceptor[ports.length];
		for(int x=0; x < ports.length; x++)
		{
			conns[x] = new connectionacceptor(ports[x]);
		}
	}
	public server(int port)
	{
		this(new int[] {port});
	}
	public void run()
	{
		for(connectionacceptor i : conns)
		{
			i.start();
		}
	}
	public void close()
	{
		for(connectionacceptor i: conns)
		{
			i.finish();
		}
	}
	public void write(int ind, String stuff)
	{
		conns[ind].write(stuff, true);
	}
	public void write(String stuff)
	{
		for(connectionacceptor i: conns)
			i.write(stuff, true);
	}
	public String read(int index)
	{
		return conns[index].read();
	}
	public String read()
	{
		String output = "";
		for(connectionacceptor i: conns)
		{
			String thing = i.read();
			output += thing;
		}
		return output;
	}
	public int available()
	{
		int output = 0;
		for(connectionacceptor i: conns)
		{
			output = output + i.available();
		}
		return output;
	}
	
}