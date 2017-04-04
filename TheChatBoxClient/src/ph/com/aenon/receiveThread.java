package ph.com.aenon;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

/**
 * Created by aenon on 02/04/2017.
 */
public class receiveThread extends Thread{

    DatagramSocket clientSocket = null;
    DatagramPacket receivePacket = null;
    byte[] receiveData = null;
    int port = 1;

    public void run(){

        try {
            clientSocket = new DatagramSocket(port);
        } catch (SocketException e) {
            e.printStackTrace();
        }

        while(true){

            try {
                receiveData = new byte[1024];
                receivePacket = new DatagramPacket(receiveData, receiveData.length);

                clientSocket.receive(receivePacket);
                String received = new String(receivePacket.getData());
                System.out.println(received);
                Chat.convoMessage.appendText("\n" + received);
            } catch (IOException e) {
                System.out.println(e);
                e.printStackTrace();
            }

        }

    }
}
