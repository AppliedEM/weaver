package com.appliedenergetics.weaver;

import java.lang.*;
import java.io.*;
import java.net.*;

public class connection
{
	public InputStream istream;
	public OutputStream ostream;
	public Socket sock;
	public ServerSocket servsock;
	
	public connection(InputStream i, OutputStream o)
	{
		istream = i;
		ostream = o;
	}
	public void initialize(Socket s)
	{
		try
		{
			sock = s;
			s.setReuseAddress(true);
			istream = s.getInputStream();
			ostream = s.getOutputStream();
		}
		catch(Exception e)
		{
			System.out.println("could not create socket");
		}
	}
	public connection(Socket s)
	{
		initialize(s);
	}
	public connection(ServerSocket s)
	{
		try
		{
			servsock = s;
			sock = s.accept();
		}
		catch(Exception e)
		{
			System.out.println("could not bind to port");
		}
		initialize(sock);
	}
	public void setstreams(InputStream i, OutputStream o)
	{
		istream = i;
		ostream = o;
	}
	public boolean close()
	{
		try
		{
			istream.close();
			ostream.close();
			servsock.close();
			sock.close();
			if(sock != null)
			{
				sock.setReceiveBufferSize(0);
			}
			return true;
		}
		catch(Exception e)
		{
			return false;
		}
	}
	public int available()
	{
		try
		{
			return istream.available();
		}
		catch(Exception e)
		{
			System.out.println("Socket: cannot get bytes in istream");
			return -1;
		}
	}
	public int read()
	{
		try
		{
			return istream.read();
		}
		catch(Exception e)
		{
			return -1;
		}
	}
	public String readline()
	{
		return readline(0);
	}
	public String readline(int maxlen)
	{
		String output = "";
		int counter = 0;
		while(available() > 0 && (maxlen == 0 || counter > maxlen))
		{
			output += (char)read();
			counter++;
		}
		return output;
	}
	public boolean write(int in)
	{
		try
		{
			ostream.write(in);
			return true;
		}
		catch(Exception e)
		{
			return false;
		}
	}
	public boolean write(byte[] in)
	{
		try
		{
			ostream.write(in, 0, in.length);
			return true;
		}
		catch(Exception e)
		{
			return false;
		}
	}
	public boolean write(String in)
	{
		return write(in.getBytes());
	}
	public static int[] getints(String in)
	{
		int[] output = new int [in.length()];
		for(int x = 0; x < output.length; x++)
		{
			output[x] = (int)(in.charAt(x));
		}
		return output;
	}
	public boolean isconnected()
	{
		return !sock.isConnected();
	}
}