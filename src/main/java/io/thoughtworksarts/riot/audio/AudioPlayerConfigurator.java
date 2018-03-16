package io.thoughtworksarts.riot.audio;

import com.synthbot.jasiohost.AsioDriver;
import io.thoughtworksarts.riot.branching.BranchingLogic;
import io.thoughtworksarts.riot.utilities.OSChecker;
import javafx.scene.control.*;
import java.util.List;

public class AudioPlayerConfigurator {

    private AudioPlayerConfigurator(){}

    public static RiotAudioPlayer getConfiguredRiotAudioPlayer(BranchingLogic branchingLogic) throws Exception{
        RiotAudioPlayer audioPlayer;
        String driverName="";
        if( !OSChecker.isWindows()){
            audioPlayer = new JavaSoundAudioPlayer();
        }else{
            driverName =selectDriverName(getDriversAvailable());
            audioPlayer =new AudioPlayer();
        }
        audioPlayer.initialise(driverName,branchingLogic.getAudioPath());
        return audioPlayer;
    }

    private static String selectDriverName(List<String> driverNameList) {
        ChoiceDialog<String> dialog = new ChoiceDialog<>(driverNameList.get(0),driverNameList);
        dialog.setTitle("RIOT");
        dialog.setHeaderText("Please select the audio driver to use:");
        dialog.showAndWait();
        return dialog.getSelectedItem();
    }

    private static List<String> getDriversAvailable() {
        List<String> driverNameList = AsioDriver.getDriverNames();
        if(driverNameList == null || driverNameList.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error Loading Asio Driver");
            alert.setHeaderText("Could not find any ASIO drivers");
            alert.setContentText("Could not find any ASIO drivers installed on this machine.\n"+
                    " You must have ASIO drivers installed to run this program.");
            alert.showAndWait();
            throw new RuntimeException("No Asio Driver Found");
        }
        return driverNameList;
    }
}
