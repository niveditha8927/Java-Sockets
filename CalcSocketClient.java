package de.unistgt.ipvs.vs.ex1.client;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.Socket;

/**
 * Implement the connectTo-, disconnect-, and calculate-method of this class
 * as necessary to complete the assignment. You may also add some fields or methods.
 */
public class CalcSocketClient implements Serializable{
	private Socket cliSocket;
	private int    rcvdOKs;		// --> Number of valid message contents
	private int    rcvdErs;		// --> Number of invalid message contents
	private int    calcRes;		// --> Calculation result (cf.  'RES')
	public	ObjectInputStream oisclient = null; //input object stream for exchange of information between client and server sockets
	public	ObjectOutputStream oosclient = null;//output object stream for exchange of information between client and server sockets
	
	public CalcSocketClient() {
		this.cliSocket = null;
		this.rcvdOKs   = 0;
		this.rcvdErs   = 0;
		this.calcRes   = 0;
	}
	
	public int getRcvdOKs() {
		return rcvdOKs;
	}

	public int getRcvdErs() {
		return rcvdErs;
	}

	public int getCalcRes() {
		return calcRes;
	}

	public boolean connectTo(String srvIP, int srvPort)   {
               
		//Solution here
		try {
			
			//Solution here
			//creating socket to communicate with server
			cliSocket = new Socket(srvIP,srvPort);
			
			//creating object streams
			oosclient = new ObjectOutputStream(cliSocket.getOutputStream());
			oisclient = new ObjectInputStream(cliSocket.getInputStream());
           
			try {
			//reading ready message from server to check if connection is established
			String message = (String) oisclient.readObject();
			System.out.println("recieved Message from server: " + message);
            
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
       
		}
		catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
        
		return true;
	}
	 
	public boolean disconnect()  {
               
	    //Solution here
		
		try {
        System.out.println("Sending close request to Socket Server");
        
	    //sending a message to closer socket and streams at server end
        oosclient.writeObject("EXIT");
        oisclient.close();
        oosclient.close();
        cliSocket.close();
        
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return true;
	}

	public boolean calculate(String request) throws IOException {
               
		if (cliSocket == null) {
			System.err.println("Client not connected!");
			return false;
		}
		
		//Solution here
		
		//writing the request to server
		oosclient.writeObject(request);
		System.out.println("request to server"+request);
		String response ="";
		
		//until FIN is sent by server processing every OK, ERR and RES message
		do {
		 try {
			 
			 response = (String) oisclient.readObject();
				
				System.out.println("response from server"+response);
				
				if(response.contains("RES"))
				{
					String temp = response.substring((response.indexOf("RES")+4),response.length()-1);
					System.out.println(temp);
					calcRes = Integer.parseInt(temp);
					rcvdOKs++;
				}
				else if(response.contains("OK"))
				{
					rcvdOKs++;
				}
				else if(response.contains("ERR"))
				{
					rcvdErs++;
				}
				else
				{
					//Do Nothing
				}
			            
		} catch (IOException | ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		 }while(!(response.contains("FIN")));
		return true;
	}
}
