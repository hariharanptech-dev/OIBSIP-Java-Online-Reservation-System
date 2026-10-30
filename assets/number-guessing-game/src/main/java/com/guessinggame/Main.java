package com.guessinggame;

import com.guessinggame.ui.AppFrame;
import javax.swing.SwingUtilities;

/** Entry point: launches the window on the Event Dispatch Thread. */
public final class Main {
    private Main() { }
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new AppFrame().setVisible(true));
    }
}
