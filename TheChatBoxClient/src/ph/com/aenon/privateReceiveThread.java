package ph.com.aenon;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

/**
 * Created by aenon on 08/04/2017.
 */
public class privateReceiveThread extends Thread {

    DatagramPacket receivePacket;
    DatagramSocket clientSocket = new DatagramSocket(9876);

    byte[] receiveData = new byte[1024];

    String receivedSentence = "";

    public privateReceiveThread() throws SocketException {
    }

    public void run(){
        while (true){
            receiveData = new byte[1024];
            receivePacket = new DatagramPacket(receiveData, receiveData.length);

            InetAddress IPAddress = receivePacket.getAddress();
            int port = receivePacket.getPort();

            try {
                clientSocket.receive(receivePacket);
            } catch (IOException e) {
                e.printStackTrace();
            }
            receivedSentence = new String(receivePacket.getData(), 0, receivePacket.getLength());
            System.out.println(receivedSentence);
        }
    }

}
