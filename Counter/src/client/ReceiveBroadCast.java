package client;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

import net.sf.jcarrierpigeon.WindowPosition;
import net.sf.jtelegraph.Telegraph;
import net.sf.jtelegraph.TelegraphQueue;
import net.sf.jtelegraph.TelegraphType;

public class ReceiveBroadCast {
	private int PORT = 8071;
	public ReceiveBroadCast(){
		//System.out.println("Running DHCP server. ");
    	try{
            //System.out.println("DHCP Server offer starting.");
            DatagramSocket socket = new DatagramSocket(PORT, InetAddress.getByName("0.0.0.0"));                                
            while(true){
                byte[] buf = new byte[256];
                DatagramPacket packet = new DatagramPacket(buf, buf.length);
                socket.receive(packet);
                String fromClient = new String(packet.getData()).trim();                    
                Telegraph telegraph = new Telegraph("Queue Management System", fromClient, TelegraphType.NOTIFICATION_INFO, WindowPosition.BOTTOMRIGHT, 4000);
        		TelegraphQueue queue = new TelegraphQueue();
        		queue.add(telegraph);                
            }
        }catch(Exception e) { System.out.println("Error while receiving broadcast"+e.getMessage()); }
	}
}
