package de.unistgt.ipvs.vs.ex1.server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.Socket;
import java.rmi.RemoteException;

import de.unistgt.ipvs.vs.ex1.common.ICalculation;

/**
 * Add fields and methods to this class as necessary to fulfill the assignment.
 */
public class CalculationSession implements Runnable,Serializable  {
        
	protected Socket client; //socket for client
	private String RequestCalc; 
	private String request;
	private int task = 0; //holds previous operations
	private ObjectInputStream oisserver =null; //input object stream for exchange of information between client and server sockets
	private ObjectOutputStream oosserver = null; //output object stream for exchange of information between client and server sockets
	private int calcresult;
	private int interimresult = 0;
	private CalculationImpl calcobj;
	
	public CalculationSession(Socket newclient) {
	
		this.client = newclient;
		System.out.println("calculate session object created"  );
	}
	
	public void run() {
		
		try {
			//object streams creation for communication
			oosserver = new ObjectOutputStream(client.getOutputStream());
			oisserver = new ObjectInputStream(client.getInputStream());
			
			//ready message sent to client as connection established and server ready to process requests
			oosserver.writeObject("<08:RDY>");

			//creating new calculate object
			calcobj = new CalculationImpl();
			
			while(true)
			{
				//reading request from client
				request = (String) oisserver.readObject();
				System.out.println("message received in server:"+request);
				
				//sending OK message to server indicating message received
				oosserver.writeObject("<07:OK>");
				
				//checking if there was a client disconnect, then closing the streams and socket created for client
				if(request.contains("EXIT"))
				{
			          oisserver.close();
			          oosserver.close();
			          client.close();
			          return;			   
				}
				else
				{
					String twodigitregex = "^[0-9]{2}$"; 
					String length;
					
					//extracting the length sent with the message
					length = request.substring((request.indexOf("<")+1),request.indexOf(":"));
					System.out.println("length "+length);
					
					//checking if the length was sent properly, if not sending error
					if(!(length.matches(twodigitregex)))
					{
						//send error message for invalid length of message
				          String response = "ERR "+ length;
				          oosserver.writeObject("<" + (response.length() + 5) + ":" + response + ">");
					}
					
					//to extract the exact request from the main string
					RequestCalc = (request.substring((request.indexOf(":")+1),request.indexOf(">"))).trim();					
					System.out.println("RequestCalc "+RequestCalc);
					
					String[] Parameters = RequestCalc.split("\\s+");
					
					//code to process request
				    String positiveregex = "\\d+";
				    String negitiveregex = "-\\d+";
				    String response;

				    for (int counter =0; counter < Parameters.length; counter++)
				    {
				    	
				      System.out.println("Inside main for");
				      
				      //checking if invalid content was sent in message and sending error if so
				      if (!(Parameters[counter].equalsIgnoreCase("ADD") || 
				      Parameters[counter].equalsIgnoreCase("SUB") ||
				      Parameters[counter].equalsIgnoreCase("MUL") ||
				      Parameters[counter].equalsIgnoreCase("RES") ||
				      Parameters[counter].matches(positiveregex) ||
				      Parameters[counter].matches(negitiveregex)))
				      {
				        response = "ERR";
				        oosserver.writeObject("<" + (response.length() + Parameters[counter].length() + 6) + ":" + response + " " + Parameters[counter] + ">");
				        System.out.println("Invalid content error");
				        Parameters[counter]= "NULL";
				      }
				      System.out.println(Parameters[counter]);
				      
				      //if content was ADD, SUB, MUL storing the operation	
				      if (Parameters[counter].equalsIgnoreCase("ADD"))
				      {	
				    	//Acknowledging the content with OK
				    	oosserver.writeObject("<11:OK ADD>");				    
				    	task = 1;
				          
				      }
				      else if (Parameters[counter].equalsIgnoreCase("SUB"))
				      {
				    	//Acknowledging the content with OK
				    	oosserver.writeObject("<11:OK SUB>");
				    	task = 2;
				        
				      } 
				      else if (Parameters[counter].equalsIgnoreCase("MUL"))
				      {
				    	//Acknowledging the content with OK
				    	oosserver.writeObject("<11:OK MUL>");	
				    	task = 3;
				        	
				      } 
				      //if the content was RES sending result out
				      else if (Parameters[counter].equalsIgnoreCase("RES"))  
				      {
				    	// Calling the RES method
			        	calcresult = calcobj.getResult(); 	
			        	oosserver.writeObject("<"+(Integer.toString(calcresult).length()+12)+":OK RES "+calcresult+">");
			        	System.out.println(" final Result = "+ calcresult);
				      }
				       
				      else if(Parameters[counter].equalsIgnoreCase("NULL"))
				      {
				        // Do Nothing
				      } 
				      else
				      {
				    	  //if content is a number obtaining the integer value from the request String
				    	  int value = Integer.parseInt(Parameters[counter]);
			    		  
				    	  //Acknowledging the content with OK
			    		  oosserver.writeObject("<"+(Parameters[counter].length()+8)+":OK "+Parameters[counter]+">");
			    		  
				    	  switch(task)
				    	  {
				    	  case 1:
				    		  // Calling the ADD method
				    		  calcobj.add(value);
				    		  break;
				    		  
				    	  case 2:
				    		  //Calling the SUB method
				    		  calcobj.subtract(value);
				    		  break;
				    		  
				    	  case 3:
				    		  //Call for MUL method
				    		  calcobj.multiply(value);	
				    		  break;
				    	  }
				      }
				      
				    }
				    
				    //message processed
				    oosserver.writeObject("<08:FIN>");				    
					
				}
			}
		} catch (IOException | ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
				
         }
}