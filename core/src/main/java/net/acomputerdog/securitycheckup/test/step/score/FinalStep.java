package net.acomputerdog.securitycheckup.test.step.score;

import net.acomputerdog.securitycheckup.test.TestEnvironment;
import net.acomputerdog.securitycheckup.test.TestResult;
import net.acomputerdog.securitycheckup.test.step.Step;

import java.util.Arrays;
import java.util.List;

public class FinalStep extends Step<TestResult> {
    private final Step<Float> child;

    public FinalStep(Step<Float> child) {
        this.child = child;
    }

    public Step<Float> getChild() {
        return child;
    }

    @Override
    public TestResult run(TestEnvironment environment) {
        TestResult result;

        try {
            Float score = child.run(environment);
            if (score != null) {
                result = TestResult.createNormalScore(environment.getCurrentTest().getInfo(), score);
            } else {
                result = new TestResult(environment.getCurrentTest().getInfo(), false, TestResult.SCORE_FAIL, TestResult.ResultCause.FAILED);
                result.setExtraInfo(TestResult.KEY_MESSAGE, "Child test returned null");
            }
        } catch (Exception e) {
            System.err.println("Exception during test");
            e.printStackTrace();

            result = new TestResult(environment.getCurrentTest().getInfo(), false, TestResult.SCORE_FAIL, TestResult.ResultCause.EXCEPTION);
            result.setExtraInfo(TestResult.KEY_EXCEPTION, e.getMessage());
        }

        return result;
    }

    @Override
    public List<Step> getSubsteps() {
        return Arrays.asList(new Step[]{child});
    }
}