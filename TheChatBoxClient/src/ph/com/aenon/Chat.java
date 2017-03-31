package ph.com.aenon;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

import static javafx.geometry.Pos.BASELINE_LEFT;
import static javafx.geometry.Pos.CENTER;

/**
 * Created by aenon on 30/03/2017.
 */
public class Chat {

    private static double displayWidth = 400;
    private static double displayHeight = 500;

    private static TextField message;
    public static String msg;

    public static TextArea convoMessage;

    public Parent main(){
        Pane rootNode = new Pane();
        rootNode.setPrefSize(displayWidth, displayHeight);

        ImageView imgBackground = Util.loadImage2View("res//The-ChatBox-LoggedIn.png", displayWidth, displayHeight);
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
        Text scenetitle = new Text("You are logged in as: " + ClientMain.name);
        scenetitle.setFont(Font.font("Arial Rounded MT Bold", FontWeight.NORMAL, 13));
        scenetitle.setFill(Color.BLACK);
        gridTitle.add(scenetitle, 0, 0);

        //Set the Buttons gridPane's properties
        GridPane gridButton = new GridPane();
        gridButton.setAlignment(CENTER);
        gridButton.setHgap(5);
        gridButton.setPadding(new Insets(25, 25, 25, 25));
        //gridButton.setGridLinesVisible(true);

        //Create LogOut Button
        Button logOutBtn = new Button("Log Out");
        logOutBtn.setTooltip(
                new Tooltip("Log Out")
        );
        HBox hbBtn1 = new HBox(4);
        hbBtn1.setAlignment(Pos.TOP_CENTER);
        hbBtn1.getChildren().add(logOutBtn);
        gridButton.add(hbBtn1, 0, 0);

        //Create Private Button
        Button privateBtn = new Button("Private");
        privateBtn.setTooltip(
                new Tooltip("Enter Private Chat Room")
        );
        HBox hbBtn2 = new HBox(4);
        hbBtn2.setAlignment(Pos.TOP_CENTER);
        hbBtn2.getChildren().add(privateBtn);
        gridButton.add(hbBtn2, 1, 0);

        gridTitle.setTranslateX(0);
        gridTitle.setTranslateY(120);

        gridButton.setTranslateX(240);
        gridButton.setTranslateY(115);

        logOutBtn.setOnAction(event -> {
            ClientMain.onLogIn();
        });

        privateBtn.setOnAction(event -> {
            //open another window to establish private communication with another client
        });

        //Set the conversation gridPane's properties
        GridPane gridConvo = new GridPane();
        gridConvo.setAlignment(BASELINE_LEFT);
        //gridTitle.setGridLinesVisible(true);

        //Create Text Area for conversation
        convoMessage = new TextArea("Start a conversation!");
        convoMessage.setFont(Font.font("Arial", FontWeight.NORMAL, 12));
        convoMessage.setWrapText(true);
        convoMessage.setEditable(false);
        convoMessage.setPrefHeight(255);
        convoMessage.setPrefWidth(355);
        convoMessage.setMaxWidth(355);
        convoMessage.setStyle("-fx-background-color: white;");
        gridConvo.add(convoMessage, 0, 0);

        //Set the message to send gridPane's properties
        GridPane gridMessage = new GridPane();
        gridMessage.setAlignment(CENTER);
        gridMessage.setHgap(10);
        gridMessage.setVgap(10);
        gridMessage.setPadding(new Insets(25, 25, 25, 25));
        //gridTitle.setGridLinesVisible(true);

        //Create Text Field for Name
        message = new TextField();
        message.setPromptText("Enter your message");
        message.setPrefHeight(20);
        message.setPrefWidth(300);
        gridMessage.add(message, 0, 0);

        //Create Send Button
        Button sendBtn = new Button("Send");
        sendBtn.setDefaultButton(true);
        sendBtn.setPrefHeight(20);
        HBox hbBtn3 = new HBox(4);
        hbBtn3.setAlignment(Pos.TOP_CENTER);
        hbBtn3.getChildren().add(sendBtn);
        gridMessage.add(hbBtn3, 1, 0);

        gridConvo.setTranslateX(25);
        gridConvo.setTranslateY(180);

        gridMessage.setTranslateX(0);
        gridMessage.setTranslateY(420);

        sendBtn.setOnAction(event -> {
            if (!message.getText().isEmpty()){
                msg = message.getText();
                try {
                    ClientMain.sendMessage();
                } catch (Exception e) {}
            }
            message.clear();
        });

        rootNode.getChildren().addAll(gridTitle, gridButton, gridConvo, gridMessage);
        return rootNode;
    }

}
