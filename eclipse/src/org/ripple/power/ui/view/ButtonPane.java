package org.ripple.power.ui.view;

import java.awt.event.ActionListener;

import javax.swing.Box;
import javax.swing.JPanel;

import org.ripple.power.ui.RPCButton;
import org.ripple.power.ui.UIConfig;

public class ButtonPane extends JPanel {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

    public ButtonPane(ActionListener listener, int spacing, String[]... items) {
        super();
        boolean addSpacer = false;
        for (String[] item : items) {
            if (addSpacer){
                add(Box.createHorizontalStrut(spacing));
            }
            RPCButton button = new RPCButton(item[0]);
            button.setActionCommand(item[1]);
            button.addActionListener(listener);
            add(button);
            addSpacer = true;
        }
        setBackground(UIConfig.background);
    }
}
