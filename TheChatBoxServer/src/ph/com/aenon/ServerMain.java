package ph.com.aenon;

import java.io.IOException;
import java.net.*;
import java.util.ArrayList;

public class ServerMain {

    private static DatagramSocket serverSocket;
    private static DatagramPacket receivePacket;
    private static DatagramPacket sendPacket;

    private static byte[] receiveData;
    private static byte[] sendData;

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

        while(true){
            receiveData = new byte[1024];
            sendData = new byte[1024];

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

            //If client is connecting
            if (checkCode.equals(preCode)){
                clientConnecting(preCode, codeOnline, receiveData, IPAddress, port, sentence);
            //If client is disconnecting
            }else if (offline.equals(codeOffline)){
                clientDisconnecting(IPAddress.toString().split("/")[1], sentence, offset);
            //Send online user's lists
            } else if (sentence.equals(codeList)) {
                clientGoingPrivate(IPAddress, port);
            //If client is sending a private message
            } else if (privateRqst.equals(codePrivate)){
                clientSendingPrivateMsg(port, sentence, privateOffset);
            //If client is sending a group message
            } else{
                clientSendingGroupMsg(IPAddress.toString().split("/")[1], sentence);
            }

        }
    }

    private static void clientConnecting(String preCode, String codeOnline, byte[] receiveData, InetAddress IPAddress, int port, String sentence) throws IOException {
        if (sentence.equals(preCode + codeOnline)){
            String toSend = "prematched!";

            sendMessage(toSend, IPAddress, port);

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
                nameList.add(clientName);
                System.out.println("");
                System.out.println(clientName + "(" + IPAddress.toString().split("/")[1] + ")" + " goes online!");
                System.out.println("Current address/es: " + addressList);
                System.out.println("Current user/s: " + nameList);

                toSend = "matched!";
                sendMessage(toSend, IPAddress, port);

                toSend = clientName + " goes online!";
                sendMessage(toSend, groupAddress, groupPort);

            }else if (flag){
                toSend = "mismatched!";
                sendMessage(toSend, IPAddress, port);
            }
        }else{
            String toSend = "mismatched!";
            sendMessage(toSend, IPAddress, port);
        }
    }

    private static void clientDisconnecting(String IPAddress, String sentence, int offset) throws IOException {
        String name = "";
        for (int i = offset; i < sentence.length(); i++){
            name = name + sentence.charAt(i);
        }
        boolean flag = false;
        int c;

        for (c = 0; c < addressList.size() && !flag; c++){
            if (IPAddress.equals(addressList.get(c))) {
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

                sendMessage(toSend, groupAddress, groupPort);

                nameList.remove(0);
                addressList.remove(0);
            }else if (c < nameList.size() || c == nameList.size() || c > nameList.size()){
                System.out.println(nameList.get(c-1) + "(" + addressList.get(c-1) + ")" + " went offline!");
                String toSend = nameList.get(c-1) + " went offline!";

                sendMessage(toSend, groupAddress, groupPort);

                nameList.remove(c-1);
                addressList.remove(c-1);
            }

            System.out.println("Current adresses: " + addressList);
            System.out.println("Current users: " + nameList);
        }
    }

    private static void clientGoingPrivate(InetAddress IPAddress, int port) throws IOException {
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

        sendMessage(toSend, IPAddress, port);
    }

    private static void clientSendingPrivateMsg(int port, String sentence, int privateOffset) throws IOException {
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

        boolean flag = false;
        int c;

        for (c = 0; c < addressList.size() && !flag; c++){
            if (useraddress.equals(addressList.get(c))) {
                flag = true;
            }
        }

        if (flag){
            if(c == 1){
                System.out.println(nameList.get(0) + "(" + addressList.get(0) + "): " + message);
                message = nameList.get(0) + ": " + message;
            }else if (c < nameList.size() || c == nameList.size() || c > nameList.size()){
                System.out.println(nameList.get(c-1) + "(" + addressList.get(c-1) + "): " + message);
                message = nameList.get(c-1) + ": " + message;
            }
        }

        sendMessage(message, InetAddress.getByName(useraddress), port);
    }

    private static void clientSendingGroupMsg(String IPAddress, String sentence) throws IOException {
        boolean flag = false;
        int c;

        for (c = 0; c < addressList.size() && !flag; c++){
            if (IPAddress.equals(addressList.get(c))) {
                flag = true;
            }
        }

        String toSend = "";

        if (flag){
            if(c == 1){
                System.out.println("");
                System.out.println(nameList.get(0) + "(" + addressList.get(0) + "): " + sentence);
                toSend = nameList.get(0) + ": " + sentence;
            }else if (c < nameList.size() || c == nameList.size() || c > nameList.size()){
                System.out.println("");
                System.out.println(nameList.get(c-1) + "(" + addressList.get(c-1) + "): " + sentence);
                toSend = nameList.get(c-1) + ": " + sentence;
            }
        }

        sendMessage(toSend, groupAddress, groupPort);
    }

    private static void sendMessage(String message, InetAddress IPAddress, int port) throws IOException {
        sendData = message.getBytes();
        sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, port);
        serverSocket.send(sendPacket);
    }
}