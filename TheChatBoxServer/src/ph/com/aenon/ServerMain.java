package ph.com.aenon;

import java.net.*;
import java.util.ArrayList;

public class ServerMain {

    private static DatagramSocket serverSocket;
    private static DatagramPacket receivePacket;
    private static DatagramPacket sendPacket;

    private static InetAddress groupAddress;
    private static int groupPort = 4446;

    private static ArrayList<String> addressList = new ArrayList<String>();
    private static ArrayList<String> nameList = new ArrayList<String>();

    public static void main(String[] args) throws Exception{
        groupAddress = InetAddress.getByName("230.0.0.1");

        System.out.println("UDP Server Online!");
        System.out.println("");

        serverSocket = new DatagramSocket(9876);

        String preCode = new String(".,paSs,#");
        String codeOnline = new String("ok");
        String codeOffline = new String("OFFLINE321.*");
        String codeList = new String("rqstList132.*0");
        String codePrivate = new String("prvtmsg.*^");
        boolean msgIsPrivate = false;

        String addressToChat = "";
        String messageToSend = "";

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

            String privateRqst = "";
            int privateOffset = 0;
            for (privateOffset = 0; privateOffset < codePrivate.length() && sentence.length() >= codePrivate.length(); privateOffset++){
                privateRqst = privateRqst + sentence.charAt(privateOffset);
            }

            //Check if client is connecting
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
                        addressList.add(IPAddress.toString().split("/")[1]);
                        System.out.println(addressList);
                        nameList.add(clientName);
                        System.out.println("");
                        System.out.println(clientName + "(" + IPAddress.toString().split("/")[1] + ")" + " goes online!");

                        toSend = "matched!";
                        sendData = toSend.getBytes();

                        sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, port);
                        serverSocket.send(sendPacket);

                        toSend = clientName + " goes online!";
                        sendData = toSend.getBytes();

                        sendPacket = new DatagramPacket(sendData, sendData.length, groupAddress, groupPort);
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
            //Check if client is disconnecting
            }else if (offline.equals(codeOffline)){

                String name = "";
                for (int i = offset; i < sentence.length(); i++){
                    name = name + sentence.charAt(i);
                }

                boolean flag = false;
                int c;

                for (c = 0; c < addressList.size() && !flag; c++){
                    if (IPAddress.toString().split("/")[1].equals(addressList.get(c))) {
                        if (name.equals(nameList.get(c))){
                            flag = true;
                        }
                    }
                }

                if (flag){
                    System.out.println("\nCurrent adresses: " + addressList);
                    System.out.println("Current users: " + nameList);

                    if(c == 1){
                        System.out.println(nameList.get(0) + "(" + addressList.get(0) + ")" + " went offline!");
                        String toSend = nameList.get(0) + " went offline!";

                        sendData = toSend.getBytes();

                        sendPacket = new DatagramPacket(sendData, sendData.length, groupAddress, groupPort);
                        serverSocket.send(sendPacket);

                        nameList.remove(0);
                        addressList.remove(0);
                    }else if (c < nameList.size() || c == nameList.size() || c > nameList.size()){
                        System.out.println(nameList.get(c-1) + "(" + addressList.get(c-1) + ")" + " went offline!");
                        String toSend = nameList.get(c-1) + " went offline!";

                        sendData = toSend.getBytes();

                        sendPacket = new DatagramPacket(sendData, sendData.length, groupAddress, groupPort);
                        serverSocket.send(sendPacket);

                        nameList.remove(c-1);
                        addressList.remove(c-1);
                    }

                    System.out.println("Current adresses: " + addressList);
                    System.out.println("Current users: " + nameList);
                }
            //Send online user's lists
            } else if (sentence.equals(codeList)) {
                String toSend = "";
                for (int c = 0; c < addressList.size(); c++){
                    toSend = toSend + addressList.get(c);

                    if (c < addressList.size()-1) {
                        toSend = toSend + ";";
                    }
                }
                toSend = toSend + "|";
                for (int c = 0; c < nameList.size(); c++){
                    toSend = toSend + nameList.get(c);

                    if (c < addressList.size()-1) {
                        toSend = toSend + ";";
                    }
                }
                System.out.println("");
                System.out.println("Requesting for online list: " + toSend);
                sendData = toSend.getBytes();

                sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, port);
                serverSocket.send(sendPacket);
            //If client is sending a private message
            } else if (privateRqst.equals(codePrivate)){
                String useraddress = "";
                String message = "";
                boolean done = false;
                for (int i = privateOffset+1; i < sentence.length(); i++){
                    if (!done){
                        if (sentence.charAt(i) != ';'){
                            useraddress = useraddress + sentence.charAt(i);
                        }
                        if (sentence.charAt(i) == ';'){
                            done = true;
                        }
                    }else if (done){
                        if (i < sentence.length()){
                            message = message + sentence.charAt(i);
                        }
                    }
                }
                System.out.println("userAddress: " + useraddress);
                System.out.println("message: " + message);

                sendData = message.getBytes();

                sendPacket = new DatagramPacket(sendData, sendData.length, InetAddress.getByName(useraddress), port);
                serverSocket.send(sendPacket);

            //If client is sending a group message
            } else{
                boolean flag = false;
                int c;

                for (c = 0; c < addressList.size() && !flag; c++){
                    if (IPAddress.toString().split("/")[1].equals(addressList.get(c))) {
                        flag = true;
                    }
                }

                String toSend = "";

                if (flag){
                    if(c == 1){
                        System.out.println(nameList.get(0) + "(" + addressList.get(0) + "): " + sentence);
                        toSend = nameList.get(0) + ": " + sentence;
                    }else if (c < nameList.size() || c == nameList.size() || c > nameList.size()){
                        System.out.println(nameList.get(c-1) + "(" + addressList.get(c-1) + "): " + sentence);
                        toSend = nameList.get(c-1) + ": " + sentence;
                    }
                }

                sendData = toSend.getBytes();

                sendPacket = new DatagramPacket(sendData, sendData.length, groupAddress, groupPort);
                serverSocket.send(sendPacket);
            }

        }
    }
}