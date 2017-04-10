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
    DatagramSocket clientSocket;

    byte[] receiveData;

    String receivedSentence = "";

    public static boolean isConnected = true;

    public privateReceiveThread(DatagramPacket receivePacket, DatagramSocket clientSocket, byte[] receiveData) throws SocketException {
        this.receivePacket = receivePacket;
        this.clientSocket = clientSocket;
        this.receiveData = receiveData;
    }

    public void run(){
        while (isConnected){
            receiveData = new byte[1024];
            receivePacket = new DatagramPacket(receiveData, receiveData.length);

            try {
                clientSocket.receive(receivePacket);

                receivedSentence = new String(receivePacket.getData(), 0, receivePacket.getLength());

                System.out.println(receivedSentence);
                PrivateChat.convoMessage.appendText("\n" + receivedSentence);
            } catch (IOException e) {
                //e.printStackTrace();
                System.out.println("Didn't get anything!");
            }

            if (!isConnected){
                clientSocket.close();
                System.out.println("Private Socket Closed!");
                break;
            }
        }
    }

}
