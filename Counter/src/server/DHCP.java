package server;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class DHCP extends Thread{
    private static int  PORT = 8068;
    private static DHCP d;
    private boolean threadRunning = false;
    private DHCP(){
    	// Singleton set
    }
    
    public static DHCP getInstance(){
    	if(d==null)
    		d = new DHCP();
    	return d;
    }
    
    public void run(){
    	if(threadRunning)
    		return;
    	else
    		threadRunning = true;
    		System.out.println("Running DHCP server. ");
	    	try{
	            System.out.println("DHCP Server offer starting.");
	            DatagramSocket socket = new DatagramSocket(PORT, InetAddress.getByName("0.0.0.0"));                                
	            while(true){
	                byte[] buf = new byte[256];
	                DatagramPacket packet = new DatagramPacket(buf, buf.length);
	                socket.receive(packet);
	                String fromClient = new String(packet.getData()).trim();                    
	                if(fromClient.equals("DHCPDISCOVER")){
	                    System.out.println("DHCP Discover message received");
	                    byte[] b = "DHCPOFFER".getBytes();                        
	                    DatagramPacket pkt = new DatagramPacket(b, b.length,packet.getAddress(),packet.getPort());
	                    socket.send(pkt);
	                    System.out.println("Sent DHCP Offer.");
	                }                
	            }
	        }catch(Exception e) { System.out.println("Error trying to send DHCP OFFER or DHCP RESPONSE "+e.getMessage()); }
    	
    }
    
}
