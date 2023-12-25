package ru.liberation.semenov.controllers;

import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseButton;
import javafx.stage.Stage;
import javafx.util.Duration;

import ru.liberation.semenov.Action;
import ru.liberation.semenov.Network;
import ru.liberation.semenov.Utils;
import ru.liberation.semenov.enums.Response;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class LobbyController implements Initializable {

    private static final String PATH_TO_ROOM_CREATOR_PAGE = "/room_creator.fxml";
    private static final String PATH_TO_ROOM_PAGE = "/room.fxml";

    @FXML
    private ListView<String> filesList;
    @FXML
    private TextField currentPath;

    private Network network;
    private String nickname;

    private final Action lobbyAction = message -> {
        String status = Utils.getStatus(message);
        if (status.equals(Response.REMOVE_ROOM.name())) {
            removeRoom(Utils.getArgs(message)[0]);
        } else if (status.equals(Response.ADD_ROOM.name())) {
            Platform.runLater(() -> addRoom(Utils.getArgs(message)[0]));
        } else if (status.equals(Response.CONNECTED.name())) {
            connectToRoom(Utils.getArgs(message));
        }
    };

    private void connectToRoom(String[] args) {
        // Здесь будет запуск UI игры уже
        String roomName = args[0];
        if (args.length == 2) {
            String enemy = args[1];
            Platform.runLater(() -> enterToExistedRoom(roomName, enemy));
        } else {
            Platform.runLater(() -> enterToCreatedRoom(roomName));
        }
    }

    private void enterToCreatedRoom(String roomName) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(PATH_TO_ROOM_PAGE));
            Stage stage = new Stage();
            stage.setScene(new Scene(fxmlLoader.load()));
            RoomController controller = fxmlLoader.getController();
            controller.setFirstPlayer(nickname);
            controller.setNetwork(network);
            stage.setTitle("Комната " + roomName);
            stage.setAlwaysOnTop(true);
            stage.setOnCloseRequest(event -> {
                network.sendChatMessage(nickname, "*покинул игру*");
            });
            network.connectToCreatedRoom(nickname, controller);
            stage.show();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void enterToExistedRoom(String roomName, String enemy) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(PATH_TO_ROOM_PAGE));
            Stage stage = new Stage();
            stage.setScene(new Scene(fxmlLoader.load()));
            RoomController controller = fxmlLoader.getController();
            controller.setFirstPlayer(nickname);
            controller.setSecondPlayer(enemy);
            controller.setNetwork(network);
            stage.setTitle("Комната " + roomName);
            stage.setAlwaysOnTop(true);
            stage.setOnCloseRequest(event -> {
                network.sendChatMessage(nickname, "*покинул игру*");
            });
            network.connectToExistedRoom(nickname, enemy, controller);
            stage.show();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void removeRoom(String roomName) {
        filesList.getItems().remove(roomName);
    }

    private void addRoom(String roomName) {
        filesList.getItems().add(roomName);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        configureDoubleClickOpen();
    }

    public void initLobby(String[] rooms) {
        currentPath.setText(nickname + ", выбирай комнату");
        for (String room : rooms) {
            filesList.getItems().add(room);
        }
        network.getDefaultHandler().setAction(lobbyAction);
        filesList.setFocusTraversable(false);
    }

    @FXML
    private void exitApplication() {
        network.close();
        Platform.exit();
    }

    private void configureDoubleClickOpen() {
        PauseTransition doubleClickInterval = new PauseTransition(Duration.millis(300));
        IntegerProperty clicks = new SimpleIntegerProperty(0);

        doubleClickInterval.setOnFinished(event -> {
            if (clicks.get() >= 2) {
                String room = filesList.getFocusModel().getFocusedItem();

                if (room != null) {
                    connectToRoom(filesList.getFocusModel().getFocusedItem());
                }
            }
            clicks.set(0);
        });

        filesList.setOnMouseClicked(event -> {
            if (event.getButton() == MouseButton.PRIMARY) {
                clicks.set(clicks.get() + 1);
                doubleClickInterval.play();
            }
        });
    }

    private void connectToRoom(String room) {
        network.connect(room, nickname);
    }

    public void setNetwork(Network network) {
        this.network = network;
    }

    @FXML
    public void createRoom(ActionEvent event) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(PATH_TO_ROOM_CREATOR_PAGE));
            Stage stage = new Stage();
            stage.setScene(new Scene(fxmlLoader.load()));
            RoomCreatorController controller = fxmlLoader.getController();
            controller.setNetwork(network);
            controller.setRooms(filesList.getItems());
            controller.setNickname(nickname);
            stage.setTitle("Создание комнаты");
            stage.show();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }
}
