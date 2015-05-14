package com.appliedenergetics.weaver;

class connectionlistener extends Thread
{
	public connection con;
	public chatroom chat;
	public boolean cont;
	
	public connectionlistener(chatroom ch, connection c)
	{
		con = c;
		chat = ch;
		cont = true;
	}
	public void run()
	{
		while(cont)
		{
			int b = con.read();
			synchronized(chat)
			{
				chat.broadcast(b);
			}
		}
	}
	public boolean close()
	{
		cont = false;
		return con.close();
	}
}

public class chatroom extends Thread
{
	int port;
	int maxchatters;
	connectionlistener[] cons;
	int index;
	
	
	public chatroom(int p, int max)
	{
		port = p;
		maxchatters = max;
		cons = new connectionlistener[max];
		index = 0;
	}
	public void run()
	{
		while(true)
		{
			createsocket();
			index++;
			if(index >= maxchatters)
			{
				index = 0;
			}
		}
	}
	public void createsocket()
	{
		if(cons[index] != null)
			cons[index].close();
		System.out.println("waiting for connection");
		connection c = weaver.createsocket(port);
		System.out.println("accepted connection");
		cons[index] = new connectionlistener(this, c);
		cons[index].start();
		System.out.println("created listener");
	}
	public void broadcast(int b)
	{
		for(int x = 0; x< cons.length; x++)
		{
			cons[x].con.write(b);
		}
		System.out.println("broadcasted " + (char)b);
	}
	
}
