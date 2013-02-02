package client;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class DHCPDiscover {
	public String getServerIP(){
        int retry = 0;
        while(retry<3){
        try{    
        	int PORT = 8068;
            DatagramSocket serverSocket = new DatagramSocket();
            serverSocket.setSoTimeout(6000);
            serverSocket.setBroadcast(true);
            InetAddress group = InetAddress.getByName("255.255.255.255");
            System.out.println("Trying to find DHCP Servers");
            byte[] b = "DHCPDISCOVER".getBytes();
            DatagramPacket dpkt = new DatagramPacket(b, b.length, group, PORT);
            serverSocket.send(dpkt);
            byte[] buf = new byte[256];
            DatagramPacket packet = new DatagramPacket(buf, buf.length);
            serverSocket.receive(packet);
            System.out.println(new String(packet.getData()).trim()+" obtained from DHCP Server "+packet.getAddress().getHostAddress());
            String dhcpServerAddress = packet.getAddress().getHostAddress();
            serverSocket.close();
            System.out.println("Server ip: "+dhcpServerAddress);
            return dhcpServerAddress;          

        }catch(Exception e) { 
        	retry++;
        	System.out.println("No of retry: "+retry);//System.out.println(e.getMessage());

            }
        }
        if(retry==3){
                System.out.println("Failed to get offer.");
                //waitForOffer();
        }
        return null;
     }
	
	public void waitForOffer(){
        try{
             DatagramSocket socket = new DatagramSocket(8069, InetAddress.getByName("0.0.0.0"));
             //while(true){
                    byte[] buf = new byte[256];
                    DatagramPacket packet = new DatagramPacket(buf, buf.length);
                    socket.receive(packet);
                    String fromClient = new String(packet.getData()).trim();
                    if(fromClient.equals("QNE")){
                        System.out.println("Queue is not empty");
                        //String dhcpServerAddress = packet.getAddress().getHostAddress();
                    }
                    
            //}
        }catch(Exception e) { e.printStackTrace(); }
    }
}
