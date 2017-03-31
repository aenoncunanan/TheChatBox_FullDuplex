package ph.com.aenon;

import java.net.*;
import java.util.ArrayList;

public class ServerMain {

    private static DatagramSocket serverSocket;
    private static DatagramPacket receivePacket;
    private static DatagramPacket sendPacket;

    private static ArrayList<String> addressList = new ArrayList<String>();
    private static ArrayList<String> nameList = new ArrayList<String>();

    public static void main(String[] args) throws Exception{
        System.out.println("UDP Server Online!");
        System.out.println("");

        serverSocket = new DatagramSocket(9876);

        String preCode = new String(".,paSs,#");
        String codeOnline = new String("ok");
        String codeOffline = new String("OFFLINE321.*");

        while(true){
            byte[] receiveData = new byte[1024];
            byte[] sendData = new byte[1024];

            receivePacket = new DatagramPacket(receiveData, receiveData.length);
            serverSocket.receive(receivePacket);

            InetAddress IPAddress = receivePacket.getAddress();
            int port = receivePacket.getPort();

            String sentence = new String(receivePacket.getData(), 0, receivePacket.getLength());

            String checkCode = "";
            for (int i = 0; i < preCode.length() && sentence.length() >= preCode.length(); i++){
                checkCode = checkCode + sentence.charAt(i);
            }

            String offline = "";
            int offset = 0;
            for (offset = 0; offset < codeOffline.length() && sentence.length() >= codeOffline.length(); offset++){
                offline = offline + sentence.charAt(offset);
            }

            if (checkCode.equals(preCode)){
                if (sentence.equals(preCode + codeOnline)){
                    String toSend = "prematched!";
                    sendData = toSend.getBytes();

                    sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, port);
                    serverSocket.send(sendPacket);

                    receivePacket = new DatagramPacket(receiveData, receiveData.length);
                    serverSocket.receive(receivePacket);

                    String clientName = new String(receivePacket.getData(), 0, receivePacket.getLength());
                    boolean flag = false;
                    int c = 0;

                    for (c = 0; c < nameList.size() && !flag; c++){
                        if (clientName.equals(nameList.get(c))) {
                            flag = true;
                        }
                    }

                    if (!flag){
                        addressList.add(IPAddress.toString());
                        nameList.add(clientName);
                        System.out.println("");
                        System.out.println(clientName + "(" + IPAddress + ")" + " goes online!");

                        toSend = "matched!";
                        sendData = toSend.getBytes();

                        sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, port);
                        serverSocket.send(sendPacket);
                    }else if (flag){
                        toSend = "mismatched!";
                        sendData = toSend.getBytes();

                        sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, port);
                        serverSocket.send(sendPacket);
                    }
                }else{
                    String toSend = "mismatched!";
                    sendData = toSend.getBytes();

                    sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, port);
                    serverSocket.send(sendPacket);
                }
            }else if (offline.equals(codeOffline)){

                String name = "";
                for (int i = offset; i < sentence.length(); i++){
                    name = name + sentence.charAt(i);
                }

                boolean flag = false;
                int c;

                for (c = 0; c < addressList.size() && !flag; c++){
                    if (IPAddress.toString().equals(addressList.get(c))) {
                        if (name.equals(nameList.get(c))){
                            flag = true;
                        }
                    }
                }

                if (flag){
                    System.out.println(addressList);
                    System.out.println(nameList);

                    if(c == 1){
                        System.out.println(nameList.get(0) + "(" + addressList.get(0) + ")" + " went offline!");
                        nameList.remove(0);
                        addressList.remove(0);
                    }else if (c < nameList.size() || c == nameList.size() || c > nameList.size()){
                        System.out.println(nameList.get(c-1) + "(" + addressList.get(c-1) + ")" + " went offline!");
                        nameList.remove(c-1);
                        addressList.remove(c-1);
                    }

                    System.out.println(addressList);
                    System.out.println(nameList);
                }
            } else{
                System.out.println(IPAddress + ": " + sentence);
                String toSend = IPAddress + ": " + sentence;
                sendData = toSend.toUpperCase().getBytes();

                sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, port);
                serverSocket.send(sendPacket);
            }

        }
    }
}