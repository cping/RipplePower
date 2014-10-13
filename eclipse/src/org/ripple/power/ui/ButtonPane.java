/**
 * Copyright 2014 Ronald W Hoffman
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.ripple.power.ui;

import java.awt.event.ActionListener;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JPanel;

/**
 * ButtonPane create a JPanel containing one or more buttons arranged horizontally
 */
public class ButtonPane extends JPanel {

    /**
     * Create a JPanel containing the specified buttons
     *
     * @param       listener            Action listener
     * @param       spacing             Horizontal spacing between the buttons
     * @param       items               One or more button specifications
     */
    public ButtonPane(ActionListener listener, int spacing, String[]... items) {
        super();
        boolean addSpacer = false;
        for (String[] item : items) {
            if (addSpacer)
                add(Box.createHorizontalStrut(spacing));
            JButton button = new JButton(item[0]);
            button.setActionCommand(item[1]);
            button.addActionListener(listener);
            add(button);
            addSpacer = true;
        }
    }
}
