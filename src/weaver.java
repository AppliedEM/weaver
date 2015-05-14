package com.appliedenergetics.weaver;

import java.lang.*;
import java.io.*;
import java.net.*;
import java.util.ArrayList;

//a hyperthreading class that wraps up the process of acception a connection from a distant point without blocking the execution of the program. also can handle basic data transfer
class connectionacceptor extends Thread
{
	public boolean accepted;
	public String writebuffer;
	public String readbuffer;
	public connection con;
	public boolean closeafterwrite;
	public boolean keepopen;
	public int port;
	public boolean running;
	
	public connectionacceptor(int p)
	{
		accepted = false;
		port = p;
		writebuffer = "";
		closeafterwrite = false;
		keepopen = true;
		readbuffer = "";
	}
	public connectionacceptor(int p, boolean keep)
	{
		this(p);
		keepopen = keep;
	}
	public void startconnection()
	{
		con = weaver.createsocket(port);
		accepted = true;
		if(con.available() > 0)
		{
			readbuffer += con.readline();
		}
		con.write(writebuffer);
		if(con.available() > 0)
		{
			readbuffer += con.readline();
		}
		if(closeafterwrite)
			con.close();
	}
	public void run()
	{
		do
		{
			startconnection();
		}
		while(keepopen && closeafterwrite);
	}
	public int available()
	{
		return readbuffer.length();
	}
	public connection getconnection()
	{
		return con;
	}
	public String read()
	{
		String stuff = readbuffer;
		readbuffer = "";
		return stuff;
	}
	public void write(String in)
	{
		writebuffer += in;
	}
	public void write(String in, boolean closeafterfinish)
	{
		closeafterwrite = closeafterfinish;
		write(in);
	}
	public String[] getbuffers()
	{
		return new String[] {readbuffer, writebuffer};
	}
	public void close()
	{
		accepted = false;
		con.close();
	}
	public void finish()
	{
		keepopen = false;
		close();
	}
	public boolean isconnected()
	{
		try
		{
			return con.isconnected();
		}
		catch(Exception e)
		{
			return false;
		}
	}
}

//a hyperthreading class that wraps up the connecting of the input stream of one connection to the output stream of another connection. 
class linkerthread extends Thread
{
	public InputStream istream;
	public OutputStream ostream;
	public boolean active;
	
	public linkerthread(InputStream i, OutputStream o)
	{
		super("linkerthread");
		istream = i;
		ostream = o;
		active = true;
	}
	public void run()
	{
		int lastbyte = 0;
		while(lastbyte != -1)
		{
			try
			{
				lastbyte = istream.read();
				ostream.write(lastbyte);
			}
			catch(Exception e)
			{
			}
		}
		active = false;
		try
		{	
			ostream.close();	
		}
		catch(Exception e)
		{
		}
	}
}

//Defines a connection to a certain point in the net. contains an input stream and an output stream, as well as a series of methods that allow for easy manipulation of them.

public class weaver
{
	private final static int delayms = 500;
	private final static int timeoutmillis = 200;
	public int devicesscanned = 0;

	
	public static String getip()
	{
		try
		{
			return InetAddress.getLocalHost().getHostName();
		}
		catch(Exception e)
		{
			
		}
		return "";
	}
	public static String tostring(int[] in)
	{
		String out = "";
		out += in[0];
		for(int x= 1; x < in.length; x++)
		{
			out += ".";
			out += in[x];
		}
		return out;
		
	}
	//varies the last byte of the ip on the port range provided to scan the entire subnet.
	public String[] scansubnet(String ip, int port1, int port2)
	{
		devicesscanned = 0;
		String[] bytess = ip.split("\\.");
		int[] bytes = new int[bytess.length];
		ArrayList<String> output = new ArrayList();
		for(int x = 0;x < bytes.length; x++)
		{
			bytes[x] = Integer.parseInt(bytess[x]);
		}
		
		for(int x = 0; x < 256; x++)
		{
			String newip = tostring(new int[] {bytes[0], bytes[1], bytes[3], x});
			int[] openports = portscan(newip, port1, port2);
			if(openports.length > 0)
			{
				output.add(newip);
			}
			devicesscanned++;
		}
		String[] out = new String[output.size()];
		out = output.toArray(out);
		return out;
	}
	//scans a single port at a given url
	public static boolean portscan(String url, int port)
	{
		if(portscan(url, port, port).length > 0)
			return true;
		return false;
	}
	//scans the ports on a host over a range and returns an array containing the port numbers of the open ports on the target machine.
	public static int[] portscan(String url, int port1, int port2)
	{
		boolean[] output = new boolean[port2-port1+1];
		int numberopen = 0;
		for(int x = 0; x < output.length; x++)
		{
			//System.out.println("scanning port " + (port1 + x));
			connection c = connect(url, port1+x);
			if(c != null)
			{
				c.close();
				output[x] = true;
				numberopen++;
				c.close();
			}
			else
				output[x] = false;
		}
		int[] output2 = new int[numberopen];
		int index = 0;
		for(int x = 0; x < output.length; x++)
		{
			if(output[x])
			{
				output2[index] = x;
				index++;
			}
		}
		return output2;
	}
	
	//creates a link between two distant ports together on two different machine. untested
	public static void connectiontoconnection(String url1, int port1, String url2, int port2)
	{
		connection c1 = connect(url1, port1);
		connection c2 = connect(url2, port2);
		linkconnections(c1, c2);
	}

	//creates a link from port1 to a distant port on another machine. untested
	public static void porttoconnection(int port1, String url, int port2)
	{
		connection c1 = createsocket(port1);
		connection c2 = connect(url, port2);
		linkconnections(c1, c2);
	}
	//creates a link from port1 to port2
	public static void createproxyport(int port1, int port2)
	{
		connectionacceptor ca = new connectionacceptor(port1);
		connectionacceptor cb = new connectionacceptor(port2);
		ca.start();
		cb.start();
		while(ca.accepted == false || cb.accepted == false)
		{
			try
			{
				Thread.sleep(delayms);
			}
			catch(Exception e)
			{
			}
		}
		ca.stop();
		cb.stop();
		linkconnections(ca.getconnection(), cb.getconnection());
	}
	public static void linkconnections(connection c1, connection c2)
	{
		linkerthread l1 = new linkerthread(c1.istream, c2.ostream);
		linkerthread l2 = new linkerthread(c2.istream, c1.ostream);
		l1.start();
		l2.start();
		while(l1.active == true && l2.active == true)
		{
			try
			{
				Thread.sleep(delayms);
			}
			catch(Exception e)
			{
			}
		}
		try
		{
			l1.stop();
		}
		catch(Exception e)
		{
		}
		try
		{
			l2.stop();
		}
		catch(Exception e)
		{
		}
	}
	public static connection connect(String url, int port)
	{
		return connect(url, port, timeoutmillis);
	}
	public static connection connect(String url, int port, int timeout)
	{
		try
		{
			/*InetAddress address = InetAddress.getByName(url);
			Socket soc = new Socket(address, port);
			*/
			Socket soc = new Socket();
			InetSocketAddress addr = new InetSocketAddress(url, port);
			soc.connect(addr, timeout);
			OutputStream out = soc.getOutputStream();
			InputStream in = soc.getInputStream();
			return new connection(in, out);
		}
		catch(Exception e)
		{
			//System.out.println("error connecting to server");
			return null;
		}
		
	}
	public static connection createsocket(int port)
	{
		try
		{
			ServerSocket soc = new ServerSocket();
			soc.setReuseAddress(true);
			soc.bind(new InetSocketAddress("127.0.0.1", 13370));
			
			/*Socket skt = soc.accept();
			InputStream in = skt.getInputStream();
			OutputStream out = skt.getOutputStream();*/
			connection con = new connection(soc);
			return con;
		}
		catch(Exception e)
		{
			System.out.println("could not create socket");
		}
		return null;
	}
	public static void printinputstream(InputStream in)
	{
		int lastbyte = 0;
		
		while(lastbyte != -1)
		{
			try
			{
				lastbyte = in.read();
			}
			catch(Exception e)
			{
				System.out.println("could not print stream");
			}
			System.out.print((char)lastbyte);
		}
	}
}
