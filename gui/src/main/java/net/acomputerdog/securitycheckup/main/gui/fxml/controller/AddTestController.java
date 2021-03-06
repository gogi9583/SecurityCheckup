package net.acomputerdog.securitycheckup.main.gui.fxml.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.stage.Stage;
import net.acomputerdog.securitycheckup.main.gui.SecurityCheckupApplication;
import net.acomputerdog.securitycheckup.main.gui.fxml.window.AddTestWindow;
import net.acomputerdog.securitycheckup.test.Profile;
import net.acomputerdog.securitycheckup.test.Test;

import java.util.HashSet;
import java.util.Set;

public class AddTestController implements AddTestWindow {
    @FXML
    private Stage stage;
    @FXML
    private ComboBox<Test> testCombo;

    private SecurityCheckupApplication securityCheckupApplication;

    private Profile currentProfile;
    private Set<ProfileAddTestListener> profileAddTestListeners;

    @FXML
    private void initialize() {
        profileAddTestListeners = new HashSet<>();
    }

    public void initData(SecurityCheckupApplication application) {
        this.securityCheckupApplication = application;
    }

    @FXML
    private void onAddTest(ActionEvent actionEvent) {
        Test test = testCombo.getValue();

        if (test != null) {
            currentProfile.addTest(test);
            profileAddTestListeners.forEach(l -> l.onAddTest(currentProfile, test));
        }

        stage.hide();
    }

    @FXML
    private void onCancel(ActionEvent actionEvent) {
        stage.hide();
    }

    @Override
    public void show(Profile profile) {
        setProfile(profile);
        stage.show();
    }

    @Override
    public void setProfile(Profile profile) {
        this.currentProfile = profile;

        testCombo.getItems().clear();
        for (Test test : securityCheckupApplication.getTestRegistry().getTests()) {
            if (!profile.getTests().contains(test.getInfo().getId())) {
                testCombo.getItems().add(test);
            }
        }
    }

    @Override
    public void addAddTestListener(ProfileAddTestListener listener) {
        profileAddTestListeners.add(listener);
    }

    @Override
    public void removeAddTestListener(ProfileAddTestListener listener) {
        profileAddTestListeners.remove(listener);
    }

    @Override
    public Stage getStage() {
        return stage;
    }
}
