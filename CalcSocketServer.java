package de.unistgt.ipvs.vs.ex1.server;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;


/**
 * Extend the run-method of this class as necessary to complete the assignment.
 * You may also add some fields, methods, or further classes.
 */
public class CalcSocketServer extends Thread {
	private ServerSocket srvSocket;
	private int port;
	private Socket newclient; //socket for new client 

	public CalcSocketServer(int port) {
		this.srvSocket = null;
		try {
			srvSocket = new ServerSocket(port);
		} catch (IOException e) {
			e.printStackTrace();
		}
		this.port = port;
	}
	
	@Override
	public void interrupt() {
		try {
			if (srvSocket != null) srvSocket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void run() {
           
		if (port <= 0) {
			System.err.println("Well Wrong number of arguments.\nUsage: SocketServer <listenPort>\n");
			System.exit(-1);
		}
		
		// Start listening server socket ..
		while(true)
		{
		
		try {
			//server accepts request from client and creates a new socket object for client
			newclient = srvSocket.accept();
			
			//new calculation session thread created for every new client
			Thread CS = new Thread(new CalculationSession(newclient));	
			
			//starting the thread
			CS.start();
			

		} catch (IOException e) {
			
			e.printStackTrace();
		}
		}
	}
        
        public void waitUnitlRunnig(){
            while(this.srvSocket == null){
                try {
                    Thread.sleep(1);
                } catch (InterruptedException ex) {
                }
            }
        }

}


