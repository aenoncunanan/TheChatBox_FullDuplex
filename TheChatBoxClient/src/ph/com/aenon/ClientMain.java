package ph.com.aenon;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.io.IOException;
import java.net.*;
import java.util.ArrayList;

import static javafx.geometry.Pos.*;

public class ClientMain extends Application{

    private static double displayWidth = 400;
    private static double displayHeight = 500;

    static Stage stage;
    static Scene logInScene;

    private static String ServerIP = ""; //Server's IP Address
    public static String name = ""; //User's Screen Name

    public static DatagramSocket clientSocket;
    public static DatagramPacket sendPacket;
    public static DatagramPacket receivePacket;
    public static InetAddress IPAddress;
    public static int portNumber = 9876;

    public static byte[] sendData = new byte[1024];
    public static byte[] receiveData = new byte[1024];

    private static String serverPassword = "";
    private static String codeOffline = "OFFLINE321.*";

    private static boolean flag = false;

    private static TextField user;
    private static PasswordField pass;
    private static TextField server;
    private static Text message;

    private static String receivedSentence;

    private static ArrayList<String> addressList = new ArrayList<String>();
    private static ArrayList<String> nameList = new ArrayList<String>();

    public void start(Stage primaryStage) throws Exception{
        logInScene = new Scene(createLogInContent());
        stage = primaryStage;
        stage.setTitle("The ChatBox (Client): LogIn");
        stage.setScene(logInScene);
        stage.setResizable(false);
        stage.show();
    }

    private Parent createLogInContent() throws IOException{
        Pane rootNode = new Pane();
        rootNode.setPrefSize(displayWidth, displayHeight);

        //Set the background image
        ImageView imgBackground = Util.loadImage2View("res//The-ChatBox-LogIn.png", displayWidth, displayHeight);
        if (imgBackground != null) {
            rootNode.getChildren().add(imgBackground);
        }

        //Set the title gridPane's properties
        GridPane gridTitle = new GridPane();
        gridTitle.setAlignment(CENTER);
        gridTitle.setHgap(10);
        gridTitle.setVgap(10);
        gridTitle.setPadding(new Insets(25, 25, 25, 25));
        //gridTitle.setGridLinesVisible(true);

        //Create new text for Welcome
        Text scenetitle = new Text("Welcome!");
        scenetitle.setFont(Font.font("Arial Rounded MT Bold", FontWeight.NORMAL, 20));
        scenetitle.setFill(Color.web("#34675C"));
        gridTitle.add(scenetitle, 0, 0);

        //Set the Main Content's gridPane's properties
        GridPane gridClient = new GridPane();
        gridClient.setAlignment(CENTER);
        gridClient.setHgap(10);
        gridClient.setVgap(10);
        gridClient.setPadding(new Insets(25, 25, 25, 25));
        //gridClient.setGridLinesVisible(true);

        //Create Name Label Contents
        Text userName = new Text("Screen Name: ");
        userName.setFont(Font.font("Arial Rounded MT Bold", FontWeight.NORMAL, 15));
        userName.setFill(Color.WHITE);
        gridClient.add(userName, 0, 1);

        //Create Text Field for Name
        user = new TextField();
        user.setPromptText("Enter your name");
        gridClient.add(user, 1, 1);

        //Create Server Label Contents
        Text serverAdd = new Text("Server's IP Address: ");
        serverAdd.setFont(Font.font("Arial Rounded MT Bold", FontWeight.NORMAL, 15));
        serverAdd.setFill(Color.WHITE);
        gridClient.add(serverAdd, 0, 2);

        //Create Text Field for Server
        server = new TextField();
        server.setPromptText("Enter the IP Address");
        gridClient.add(server, 1, 2);

        //Create Password Label Contents
        Text password = new Text("Server's Pasword: ");
        password.setFont(Font.font("Arial Rounded MT Bold", FontWeight.NORMAL, 15));
        password.setFill(Color.WHITE);
        gridClient.add(password, 0, 3);

        //Create Text Field for Password
        pass = new PasswordField();
        pass.setPromptText("Enter the Password");
        gridClient.add(pass, 1, 3);

        //Set the Buttons gridPane's properties
        GridPane gridButton = new GridPane();
        gridButton.setAlignment(CENTER);
        gridButton.setVgap(10);
        gridButton.setPadding(new Insets(25, 25, 25, 25));
        //gridButton.setGridLinesVisible(true);

        //Create Connect Button
        Button btn = new Button("Connect");
        btn.setDefaultButton(true);
        HBox hbBtn = new HBox(10);
        hbBtn.setAlignment(Pos.BOTTOM_RIGHT);
        hbBtn.getChildren().add(btn);
        gridButton.add(hbBtn, 0, 0);

        //Set the Buttons gridPane's properties
        GridPane gridMessage = new GridPane();
        gridMessage.setAlignment(CENTER);
        gridMessage.setVgap(10);
        gridMessage.setPadding(new Insets(25, 25, 25, 25));
        //gridMessage.setGridLinesVisible(true);

        message = new Text();
        gridMessage.add(message, 0, 0);

        gridButton.setTranslateX(150);
        gridButton.setTranslateY(330);

        gridTitle.setTranslateX(130);
        gridTitle.setTranslateY(180);

        gridClient.setTranslateX(25);
        gridClient.setTranslateY(200);

        gridMessage.setTranslateX(115);
        gridMessage.setTranslateY(380);

        btn.setOnAction(event -> {
            message.setFill(Color.FIREBRICK);

            //Check if the text field for the username and password is not empty
            if ((user.getText() != null && !user.getText().isEmpty()) && (pass.getText() != null && !pass.getText().isEmpty()) && (server.getText() != null && !server.getText().isEmpty())) {
                ServerIP = server.getText();
                name = user.getText();
                serverPassword = pass.getText();

                try {
                    connectToServer();

                    if (flag){
                        onGroupChat();
                    }else{
                        message.setText(
                                "Invalid ScreenName or"+
                                "\nPassword mismatched!"
                        );
                    }
                } catch (Exception e) {
                    message.setText("Invalid IP Address!");
                }
            } else {
                message.setText("All fields must be filled up!");   //Prompt a message if the text fields are empty
            }

        });

        rootNode.getChildren().addAll(gridTitle, gridClient, gridButton, gridMessage);
        return rootNode;
    }

    public static void onLogIn(){
        try {
            goOffline();
        } catch (Exception e) {}

        user.clear();
        pass.clear();
        server.clear();
        message.setText("");

        stage.setTitle("The ChatBox (Client): Login");                      //Name the title of the Login stage
        stage.setScene(logInScene);                                 //Set the scene for the Login stage
    }

    public static void onPrivateChat() throws IOException {
        getOnlineUsers();

        StackPane privateScene = new StackPane();

        //Set the background image
        ImageView imgBackground = Util.loadImage2View("res//The-ChatBox-Private.png", displayWidth, displayHeight);
        if (imgBackground != null) {
            privateScene.getChildren().add(imgBackground);
        }

        GridPane gridPane = new GridPane();
        gridPane.setAlignment(CENTER);
        gridPane.setTranslateX(0);
        gridPane.setTranslateY(10);

        Label text = new Label(name + "! Chat with ");
        text.setFont(Font.font("Arial Rounded MT Bold", FontWeight.NORMAL, 20));
        text.setTextFill(Color.WHITE);
        gridPane.add(text, 0, 0);

        ComboBox onlineUsers = new ComboBox();
        for (int i = 0; i < nameList.size(); i++){
            onlineUsers.getItems().add(nameList.get(i));
        }
        onlineUsers.setValue("Select a user");
        onlineUsers.setMaxWidth(250);
        gridPane.add(onlineUsers, 1, 0);

        Button goBtn = new Button("Go");
        goBtn.setDefaultButton(true);
        goBtn.setPrefHeight(20);
        HBox hbBtn = new HBox(4);
        hbBtn.setAlignment(Pos.TOP_CENTER);
        hbBtn.getChildren().add(goBtn);
        gridPane.add(goBtn, 2, 0);

        privateScene.getChildren().add(gridPane);

        Scene privateWindow = new Scene(privateScene, displayWidth, displayHeight);

        Stage privateStage = new Stage();
        privateStage.setTitle("The ChatBox (Client): PrivateChat");
        privateStage.setScene(privateWindow);
        privateStage.setResizable(false);
        privateStage.show();

        goBtn.setOnAction(event -> {
            if (!onlineUsers.getValue().toString().equals("Select a user") && !onlineUsers.getValue().toString().equals(name)){
                //Get the userToChat's ip address
                String userToChat = onlineUsers.getValue().toString();
                String addressToChat = "";

                boolean flag = false;
                int c;

                for (c = 0; c < nameList.size() && !flag; c++){
                    if (userToChat.equals(nameList.get(c))){
                        flag = true;
                    }
                }

                if (flag){
                    if(c == 1){
                        addressToChat = addressList.get(0);
                    }else if (c < nameList.size() || c == nameList.size() || c > nameList.size()){
                        addressToChat = addressList.get(c-1);
                    }
                }
                //End of getting the userToChat's ip address

                String toChat = userToChat + ";" + addressToChat;

                PrivateChat privatechat = new PrivateChat(toChat);
                privateStage.setTitle("The ChatBox (Client): PrivateChat");
                privateStage.setScene(
                        new Scene(privatechat.main(), displayWidth, displayHeight)
                );
                privateReceiveThread privateReceiveThread = null;
                try {
                    privateReceiveThread = new privateReceiveThread();
                } catch (SocketException e) {
                    e.printStackTrace();
                }
                privateReceiveThread.start();
            }
        });

    }

    private static void getOnlineUsers() throws IOException {
        nameList.clear();
        addressList.clear();

        String toSend = "rqstList132.*0";
        sendData = new byte[1024];
        sendData = toSend.getBytes();
        sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, portNumber);
        clientSocket.send(sendPacket);

        receiveData = new byte[1024];
        receivePacket = new DatagramPacket(receiveData, receiveData.length);
        clientSocket.receive(receivePacket);
        receivedSentence = new String(receivePacket.getData(), 0, receivePacket.getLength());

        String temp = "";
        boolean flag = false;

        for (int c = 0; c < receivedSentence.length(); c++){
            if (!flag){
                if (receivedSentence.charAt(c) != ';' && receivedSentence.charAt(c) != '|'){
                    temp = temp + receivedSentence.charAt(c);
                }

                if (receivedSentence.charAt(c) == ';'){
                    addressList.add(temp);
                    temp = "";
                }else if (receivedSentence.charAt(c) == '|'){
                    addressList.add(temp);
                    temp = "";
                    flag = true;
                }
            }else{
                if (receivedSentence.charAt(c) != ';' && receivedSentence.charAt(c) != '|'){
                    temp = temp + receivedSentence.charAt(c);
                }

                if (receivedSentence.charAt(c) == ';'){
                    nameList.add(temp);
                    temp = "";
                }else if (c == receivedSentence.length()-1){
                    nameList.add(temp);
                    temp = "";
                }
            }
        }
    }

    public static void onGroupChat() throws Exception {
        groupReceiveThread thread = new groupReceiveThread();
        thread.start();

        GroupChat groupchat = new GroupChat();
        stage.setTitle("The ChatBox (Client): GroupChat");
        stage.setScene(
                new Scene(groupchat.main(), displayWidth, displayHeight)
        );
        stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent event) {
                try {
                    goOffline();
                } catch (Exception e) {}
                Platform.exit();
            }
        });
    }

    public static void sendMessage()throws Exception{
        sendData = new byte[1024];
        sendData = GroupChat.msg.getBytes();
        sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, portNumber);
        clientSocket.send(sendPacket);
    }

    public static void sendPrivateMessage(String toSend)throws Exception{
        sendData = new byte[1024];
        sendData = toSend.getBytes();
        sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, portNumber);
        clientSocket.send(sendPacket);
    }

    public static void goOffline()throws Exception{
        String toSend = codeOffline + name;
        sendData = toSend.getBytes();

        sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, portNumber);
        clientSocket.send(sendPacket);

        clientSocket.close();
        groupReceiveThread.isConnected = false;
    }

    public static void connectToServer() throws Exception{
        clientSocket = new DatagramSocket();
        IPAddress = InetAddress.getByName(ServerIP);

        sendData = new byte[1024];
        String toSend = ".,paSs,#" + serverPassword;
        sendData = toSend.getBytes();
        sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, portNumber);
        clientSocket.send(sendPacket);

        clientSocket.setSoTimeout(2000); //to stop listening to a wrong IP Address
        receiveData = new byte[1024];
        receivePacket = new DatagramPacket(receiveData, receiveData.length);
        clientSocket.receive(receivePacket);
        receivedSentence = new String(receivePacket.getData(), 0, receivePacket.getLength());

        if (receivedSentence.equals("prematched!")){
            sendData = new byte[1024];
            toSend = name;
            sendData = toSend.getBytes();
            sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, portNumber);
            clientSocket.send(sendPacket);

            receiveData = new byte[1024];
            receivePacket = new DatagramPacket(receiveData, receiveData.length);
            clientSocket.receive(receivePacket);
            receivedSentence = new String(receivePacket.getData(), 0, receivePacket.getLength());

            if (receivedSentence.equals("matched!")){
                flag = true;
            }

        }else if (receivedSentence.equals("mismatched!")){
            flag = false;
        }
    }

    public static void main(String[] args) throws Exception{
        launch(args);
    }
}
