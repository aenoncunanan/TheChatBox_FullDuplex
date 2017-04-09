package ph.com.aenon;

import java.io.IOException;
import java.net.*;

/**
 * Created by aenon on 02/04/2017.
 */
public class groupReceiveThread extends Thread{

    MulticastSocket socket = new MulticastSocket(4446);
    InetAddress address = InetAddress.getByName("230.0.0.1");

    DatagramPacket receivePacket;

    public static boolean isConnected;

    public groupReceiveThread() throws IOException {
        socket.joinGroup(address);
        isConnected = true;
    }

    public void run(){
        while (isConnected){
            byte[] buf = new byte[256];
            receivePacket = new DatagramPacket(buf, buf.length);
            try {
                socket.receive(receivePacket);
            } catch (IOException e) {
                System.out.println("Unable to get data!");
                e.printStackTrace();
            }

            String received = new String(receivePacket.getData(), 0, receivePacket.getLength());

            System.out.println(received);
            GroupChat.convoMessage.appendText("\n" + received);

            if (!isConnected){
                try {
                    socket.leaveGroup(address);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                socket.close();
                System.out.println("Group Socket Closed!");
                break;
            }

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
//                GroupChat.convoMessage.appendText("\n" + received);
//            } catch (IOException e) {
//                System.out.println(e);
//                e.printStackTrace();
//            }
//
//        }

    }
}
