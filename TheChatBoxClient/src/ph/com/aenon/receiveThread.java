package ph.com.aenon;

import java.io.IOException;
import java.net.*;

/**
 * Created by aenon on 02/04/2017.
 */
public class receiveThread extends Thread{

    MulticastSocket socket = new MulticastSocket(4446);
    InetAddress address = InetAddress.getByName("230.0.0.1");

    DatagramPacket packet;

    public receiveThread() throws IOException {
        socket.joinGroup(address);
    }

    public void run(){
        while (true){
            byte[] buf = new byte[256];
            packet = new DatagramPacket(buf, buf.length);
            try {
                socket.receive(packet);
            } catch (IOException e) {
                e.printStackTrace();
            }

            String received = new String(packet.getData(), 0, packet.getLength());

            System.out.println(received);
            Chat.convoMessage.appendText("\n" + received);
        }


//        try {
//            clientSocket = new DatagramSocket(port);
//        } catch (SocketException e) {
//            e.printStackTrace();
//        }
//
//        while(true){
//
//            try {
//                receiveData = new byte[1024];
//                receivePacket = new DatagramPacket(receiveData, receiveData.length);
//
//                clientSocket.receive(receivePacket);
//                String received = new String(receivePacket.getData());
//                System.out.println(received);
//                Chat.convoMessage.appendText("\n" + received);
//            } catch (IOException e) {
//                System.out.println(e);
//                e.printStackTrace();
//            }
//
//        }

    }
}
