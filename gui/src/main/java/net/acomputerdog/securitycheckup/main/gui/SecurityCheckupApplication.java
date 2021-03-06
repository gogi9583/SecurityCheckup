package net.acomputerdog.securitycheckup.main.gui;

import javafx.application.Application;
import javafx.concurrent.Worker;
import javafx.fxml.FXMLLoader;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.stage.Stage;
import net.acomputerdog.securitycheckup.main.gui.fxml.controller.*;
import net.acomputerdog.securitycheckup.main.gui.fxml.panel.ProfileInfoPanel;
import net.acomputerdog.securitycheckup.main.gui.fxml.panel.RunInfoPanel;
import net.acomputerdog.securitycheckup.main.gui.fxml.window.*;
import net.acomputerdog.securitycheckup.main.gui.runner.TestRunner;
import net.acomputerdog.securitycheckup.main.gui.util.AlertUtils;
import net.acomputerdog.securitycheckup.test.Profile;
import net.acomputerdog.securitycheckup.test.Test;
import net.acomputerdog.securitycheckup.test.TestResult;
import net.acomputerdog.securitycheckup.test.registry.Bundle;
import net.acomputerdog.securitycheckup.test.registry.TestRegistry;
import net.acomputerdog.securitycheckup.util.gson.JarIndex;
import net.acomputerdog.securitycheckup.util.gson.JsonUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

import static net.acomputerdog.securitycheckup.main.gui.GUIMain.displayException;

public class SecurityCheckupApplication extends Application {
    // TODO settings
    public static final float PERFECT_SCORE = 1.0f;
    public static final float PASSING_SCORE = 0.75f;

    private TestRegistry testRegistry;

    private MainController mainController;
    private AboutController aboutController;
    private ProfileManagerController profileManagerController;
    private NewProfileController newProfileController;
    private AddTestController addTestController;
    private TestManagerController testManagerController;

    @Override
    public void init() {
        this.testRegistry = new TestRegistry();
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        try {
            // load tests
            loadJarTests();

            // load windows
            this.mainController = loadFXMLController("/ui/window/main.fxml");
            mainController.initData(this);
            this.aboutController = loadFXMLController("/ui/window/about.fxml");
            this.profileManagerController = loadFXMLController("/ui/window/profile_manager.fxml");
            profileManagerController.initData(this);
            this.newProfileController = loadFXMLController("/ui/window/new_profile.fxml");
            newProfileController.initData(this);
            this.addTestController = loadFXMLController("/ui/window/add_test.fxml");
            addTestController.initData(this);
            this.testManagerController = loadFXMLController("/ui/window/test_manager.fxml");
            testManagerController.initData(this);

            // register events
            mainController.addRunButtonListener(this::runProfile);

            // Test registry events
            TestRegistry.RegistryListener registryListener = (object, event, type) -> {
                if (type == TestRegistry.Type.TEST) {
                    profileManagerController.refreshTestsList();
                    testManagerController.refreshTestsList();
                } else if (type == TestRegistry.Type.PROFILE) {
                    profileManagerController.refreshProfilesList();

                    // TODO selective refresh
                    mainController.refreshProfiles();

                    if (event == TestRegistry.Event.REMOVE) {
                        profileManagerController.showProfile(null);
                    }
                }
            };
            testRegistry.registerAddProfileListener(registryListener);
            testRegistry.registerRemoveProfileListener(registryListener);
            testRegistry.registerAddTestListener(registryListener);
            testRegistry.registerRemoveTestListener(registryListener);

            addTestController.addAddTestListener((profile, test) -> {
                // triggers when test added to profile
                profileManagerController.showProfile(profile);

                // TODO selective refresh
                mainController.refreshProfiles();
            });
            profileManagerController.addProfileRemoveTestListener((profile, test) -> {
                // Triggers when test removed from registry
                // TODO selective refresh
                mainController.refreshProfiles();
            });

            // show main window
            mainController.refreshProfiles();
            mainController.getStage().show();
        } catch (Throwable t) {
            System.err.println("Unhandled exception starting: " + t.toString());
            t.printStackTrace();

            displayException("Unhandled exception while starting.  The program must now close.", t);

            throw t;
        }
    }

    private void runProfile(ProfileInfoPanel info, RunInfoPanel runInfo) {
        info.setRunButtonEnabled(true);
        runInfo.setRunStatus("Running", Color.DIMGREY);

        TestRunner runner = new TestRunner(testRegistry, info.getProfile());
        runInfo.bind(runner);

        // enable button when run finishes or fails
        runner.runningProperty().addListener(l -> {
            if (!runner.isRunning()) {
                info.setRunButtonEnabled(false);
            }
        });

        // handle normal exit
        runner.stateProperty().addListener(l -> {
            if (runner.getState() == Worker.State.SUCCEEDED) {
                float result = runner.getValue();
                Paint color = result >= PERFECT_SCORE ? Color.GREEN : result >= PASSING_SCORE ? Color.ORANGE : Color.RED;

                runInfo.setRunStatus("Success: " + TestResult.formatScore(runner.getValue()), color);
            }
        });

        // handle exceptions
        runner.exceptionProperty().addListener(l -> {
            System.err.println("Uncaught exception in test run thread");
            runner.getException().printStackTrace();

            runInfo.setRunStatus("Exception: " + runner.getException().toString(), Color.RED);
        });

        new Thread(runner).start();
    }

    public AboutWindow getAboutWindow() {
        return aboutController;
    }

    public ProfileManagerWindow getProfileManagerWindow() {
        return profileManagerController;
    }

    public NewProfileWindow getNewProfileWindow() {
        return newProfileController;
    }

    public MainWindow getMainWindow() {
        return mainController;
    }

    public AddTestWindow getAddTestWindow() {
        return addTestController;
    }

    public TestManagerWindow getTestManagerWindow() {
        return testManagerController;
    }

    public TestRegistry getTestRegistry() {
        return testRegistry;
    }

    private <T> T loadFXMLController(String path) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(getClass().getResource(path));
        fxmlLoader.load();
        return fxmlLoader.getController();
    }

    private void loadJarTests() throws IOException {
        // check if there are any in-jar tests
        URL testURL = getClass().getResource("/tests/index.json");
        if (testURL != null) {

            try (BufferedReader indexReader = new BufferedReader(new InputStreamReader(testURL.openStream()))) {
                JarIndex index = JsonUtils.readJarIndex(indexReader);

                // load bundles first
                for (String bundlePath : index.getBundles()) {
                    try (BufferedReader bundleReader = new BufferedReader(new InputStreamReader(getClass().getResourceAsStream(bundlePath)))) {
                        Bundle bundle = JsonUtils.readBundle(bundleReader);
                        bundle.addToRegistry(testRegistry);
                    } catch (IOException e) {
                        System.err.println("Exception loading internal bundle");
                        e.printStackTrace();
                        AlertUtils.showWarning("Security Checkup", "Error loading bundle", "Internal bundle '" + bundlePath + "' could not be loaded: " + e.toString());
                    }
                }

                // then load standalone tests
                for (String testPath : index.getTests()) {
                    try (BufferedReader testReader = new BufferedReader(new InputStreamReader(getClass().getResourceAsStream(testPath)))) {
                        Test test = JsonUtils.readTest(testReader);
                        testRegistry.addTest(test);
                    } catch (IOException e) {
                        System.err.println("Exception loading internal test");
                        e.printStackTrace();
                        AlertUtils.showWarning("Security Checkup", "Error loading test", "Internal test '" + testPath + "' could not be loaded: " + e.toString());
                    }
                }

                // finally load profiles after all tests are loaded
                for (String profilePath : index.getProfiles()) {
                    try (BufferedReader profileReader = new BufferedReader(new InputStreamReader(getClass().getResourceAsStream(profilePath)))) {
                        Profile profile = JsonUtils.readProfile(profileReader);
                        testRegistry.addProfile(profile);
                    } catch (IOException e) {
                        System.err.println("Exception loading internal profile");
                        e.printStackTrace();
                        AlertUtils.showWarning("Security Checkup", "Error loading profile", "Internal profile '" + profilePath + "' could not be loaded: " + e.toString());
                    }
                }
            }
        }
    }

    public static void launch(String[] args) {
        Application.launch(args);
    }

}
