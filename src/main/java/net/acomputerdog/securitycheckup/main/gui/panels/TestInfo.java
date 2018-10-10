package net.acomputerdog.securitycheckup.main.gui.panels;

import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import net.acomputerdog.securitycheckup.test.Test;
import net.acomputerdog.securitycheckup.test.TestResult;

public class TestInfo implements Panel {

    private final VBox root;
    private final GridPane detailsPane;

    private final Label descText;
    private final Text idText;

    private final Label resultScoreText;
    private final Label resultStateText;
    private final Label resultStringText;
    private final Label resultMessageText;
    private final Label resultExceptionText;


    public TestInfo() {
        this.root = new VBox();
        root.setVisible(false); // default hidden
        root.setSpacing(5);
        root.setPadding(new Insets(5, 5, 5, 5));

        this.descText = new Label();
        descText.setWrapText(true);
        root.getChildren().add(descText);

        this.detailsPane = new GridPane();
        detailsPane.setHgap(2);
        detailsPane.setVgap(2);
        root.getChildren().add(detailsPane);

        this.idText = new Text();

        this.resultScoreText = new Label();
        resultScoreText.setWrapText(true);
        this.resultStateText = new Label();
        resultStateText.setWrapText(true);
        this.resultStringText = new Label();
        resultStringText.setWrapText(true);
        this.resultMessageText = new Label();
        resultMessageText.setWrapText(true);
        this.resultExceptionText = new Label();
        resultExceptionText.setWrapText(true);
        root.setVisible(false); // defaultHidden

        detailsPane.add(new Text("ID: "), 0, 0);
        detailsPane.add(idText, 1, 0);
        detailsPane.add(new Text("Result: "), 0, 1);
        detailsPane.add(resultStringText, 1, 1);
        detailsPane.add(new Text("Score: "), 0, 2);
        detailsPane.add(resultScoreText, 1, 2);
        detailsPane.add(new Text("State: "), 0, 3);
        detailsPane.add(resultStateText, 1, 3);
        detailsPane.add(new Text("Message: "), 0, 4);
        detailsPane.add(resultMessageText, 1, 4);
        detailsPane.add(new Text("Exception: "), 0, 5);
        detailsPane.add(resultExceptionText, 1, 5);
    }

    public void showTest(Test test) {
        // Reset results pane
        showResult(null);

        if (test != null) {
            root.setVisible(true);

            descText.setText(test.getDescription());
            idText.setText(test.getID());
        } else {
            root.setVisible(false);
        }
    }

    public void showResult(TestResult result) {
        if (result != null) {
            setResultsVisible(true);

            resultScoreText.setText(result.getScoreString());
            resultStateText.setText(result.getState().name());
            resultStringText.setText(result.getResultString());
            resultMessageText.setText(String.valueOf(result.getMessage()));
            resultExceptionText.setText(String.valueOf(result.getException()));
        } else {
            setResultsVisible(false);
        }
    }

    private void setResultsVisible(boolean visible) {
        resultExceptionText.setVisible(visible);
        resultMessageText.setVisible(visible);
        resultStringText.setVisible(visible);
        resultStateText.setVisible(visible);
        resultScoreText.setVisible(visible);
    }


    @Override
    public Node getRoot() {
        return root;
    }
}