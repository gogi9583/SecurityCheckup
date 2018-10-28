package net.acomputerdog.securitycheckup.main.gui.controller;

import javafx.fxml.FXML;
import javafx.scene.web.WebView;

import java.net.URL;

public class AboutController {
    @FXML
    private WebView webView;

    @FXML
    public void initialize() {
        URL url = getClass().getResource("/doc/about.html");
        if (url != null) {
            webView.getEngine().load(url.toString());
        } else {
            webView.getEngine().loadContent("<html><head></head><body><h1>Unable to load content.</h1></body></html>");
            System.err.println("Unable to load about.html");
        }
    }
}
