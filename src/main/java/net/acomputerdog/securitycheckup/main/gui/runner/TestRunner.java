package net.acomputerdog.securitycheckup.main.gui.runner;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.concurrent.Task;
import net.acomputerdog.jwmi.JWMI;
import net.acomputerdog.securitycheckup.main.gui.test.Profile;
import net.acomputerdog.securitycheckup.test.Test;
import net.acomputerdog.securitycheckup.test.TestEnvironment;
import net.acomputerdog.securitycheckup.test.TestResult;

import java.util.ArrayList;
import java.util.List;

public class TestRunner extends Task<Float> {
    private final Profile profile;
    private final List<RunTest> tests;

    public TestRunner(Profile profile) {
        this.profile = profile;
        this.tests = new ArrayList<>();

        profile.getTestSuite().getTests().forEach(test -> {
            RunTest runTest = new RunTest(test);
            tests.add(runTest);
        });
    }

    @Override
    protected Float call() throws Exception {
        try {
            TestEnvironment environment = new TestEnvironment(JWMI.getInstance().createWbemLocator());

            // run each test
            for (RunTest runTest : tests) {
                try {
                    Test test = runTest.getTest();
                    TestResult result = test.runTest(environment);

                    runTest.setStatus(result.getResultString());
                    //runTest.setStatus("SKIPPED");
                } catch (Exception e) {
                    System.err.println("Exception running test " + runTest.getTest().getID());
                    e.printStackTrace();

                    runTest.setStatus("Exception");
                }
            }

        } catch (Throwable t) {
            System.err.println("Uncaught exception in test run thread");
            t.printStackTrace();
        }

        // TODO exception handling by events

        return 0.5f;
    }

    public Profile getProfile() {
        return profile;
    }

    public List<RunTest> getTests() {
        return tests;
    }

    public static class RunTest {
        private ObjectProperty<Test> test = new SimpleObjectProperty<>(null, "test");
        private StringProperty status = new SimpleStringProperty(null, "status");

        public RunTest(Test test) {
            this.test.set(test);
            this.status.set("WAITING");
        }

        public Test getTest() {
            return test.get();
        }

        public String getStatus() {
            return status.get();
        }

        public void setStatus(String status) {
            this.status.set(status);
        }

        public ObjectProperty<Test> testProperty() {
            return test;
        }

        public StringProperty statusProperty() {
            return status;
        }
    }
}