package gui;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;

import java.net.URL;
import java.util.ResourceBundle;

public class Controller implements Initializable
{
    public AnchorPane anchorPane;
    public static ChoiceBox<String> runDropdown;
    public Button runButton;
    public TextField repoPath;
    public TextField importStage;
    public TextField importPass;
    public TextField importFail;
    public Label repoPathLabel;
    public Label importStageLabel;
    public Label importPassLabel;
    public Label importFailLabel;
    public ProgressBar progressBarTotal;
    public ProgressBar progressBarCurrent;

    @FXML
    public static void populateRunOptions()
    {
        final String[] options = {"Repo Check", "Import", "Delete Matches", "Date Sort Folder"};
        runDropdown.setItems(FXCollections.observableArrayList(options));
    }

    @FXML
    public void onRun()
    {
        System.out.println("Ran");
    }

    @FXML
    void updateCurrentProgressBar(final double newValue)
    {
        this.progressBarCurrent.setProgress(newValue);
    }

    @FXML
    void updateTotalProgressBar(final double newValue)
    {
        this.progressBarTotal.setProgress(newValue);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources)
    {

    }
}