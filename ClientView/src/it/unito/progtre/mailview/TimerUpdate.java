package it.unito.progtre.mailview;

import javafx.application.Platform;

import java.util.TimerTask;

public class TimerUpdate extends TimerTask{

    private ControllerMail controller;

    public TimerUpdate(ControllerMail controller){
        this.controller = controller;
    }

    @Override
    public void run() {
        Platform.runLater(() -> {
            controller.updateEmail();
        });
    }

}