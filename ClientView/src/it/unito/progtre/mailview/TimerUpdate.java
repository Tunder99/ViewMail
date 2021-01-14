package it.unito.progtre.mailview;

import javafx.application.Platform;
import java.util.TimerTask;

public class TimerUpdate extends TimerTask{

    private Model model;

    public TimerUpdate(Model model){
        this.model = model;
    }

    @Override
    public void run() {
        if(model.getStopped()){
            this.cancel();
            Platform.exit();
            System.exit(0);
        }
        if(model.getController() instanceof ControllerMail) {
            Platform.runLater(() -> {
                ((ControllerMail) model.getController()).updateEmail(false);
            });
        }
    }

}