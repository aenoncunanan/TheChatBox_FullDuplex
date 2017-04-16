package ph.com.aenon;

import java.io.IOException;
import java.net.*;

/**
 * Created by aenon on 02/04/2017.
 */
public class groupReceiveThread extends Thread{

    MulticastSocket clientSocket = new MulticastSocket(4446);
    InetAddress address = InetAddress.getByName("230.0.0.1");

    DatagramPacket receivePacket;

    public static boolean isConnected;

    public groupReceiveThread() throws IOException {
        clientSocket.joinGroup(address);
        isConnected = true;
    }

    public void run(){
        while (isConnected){
            byte[] buf = new byte[1024];
            receivePacket = new DatagramPacket(buf, buf.length);

            try {
                clientSocket.receive(receivePacket);
            } catch (IOException e) {
                System.out.println("Unable to get data!");
                e.printStackTrace();
            }

            String received = new String(receivePacket.getData(), 0, receivePacket.getLength());

            System.out.println(received);
            GroupChat.convoMessage.appendText("\n" + received);

            if (!isConnected){
                try {
                    clientSocket.leaveGroup(address);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                clientSocket.close();
                System.out.println("Group Socket Closed!");
                break;
            }

        }
    }
}
