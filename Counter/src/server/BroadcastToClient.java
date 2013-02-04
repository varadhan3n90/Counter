package server;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class BroadcastToClient {	
	private int PORT = 8071; // Clients should listen to this port for broadcast. Hardcoded
	public BroadcastToClient(String value){		
    	try{
    		DatagramSocket serverSocket = new DatagramSocket();
            serverSocket.setSoTimeout(6000);
            serverSocket.setBroadcast(true);
            InetAddress group = InetAddress.getByName("255.255.255.255");
            //System.out.println("Trying to find DHCP Servers");
            byte[] b = value.getBytes();
            DatagramPacket dpkt = new DatagramPacket(b, b.length, group, PORT);
            serverSocket.send(dpkt);
        }catch(Exception e) { System.out.println("Error trying to broadcast"+e.getMessage()); }
	}
}
