package org.jdesktop.layout;
import java.awt.Container;
import javax.swing.JComponent;
import javax.swing.SwingConstants;

import org.ripple.power.ui.graphics.LFont;


class WindowsLayoutStyle extends LayoutStyle {

    private int baseUnitX;

    private int baseUnitY;


    public int getPreferredGap(JComponent source, JComponent target,
                          int type, int position, Container parent) {
        super.getPreferredGap(source, target, type, position, parent);

        if (type == INDENT) {
            if (position == SwingConstants.EAST || position == SwingConstants.WEST) {
                int gap = getButtonChildIndent(source, position);
                if (gap != 0) {
                    return gap;
                }
                return 10;
            }
            type = RELATED;
        }
        if (type == UNRELATED) {
            return getCBRBPadding(source, target, position,
                                  dluToPixels(7, position));
        }
        else {
            boolean sourceLabel = (source.getUIClassID() == "LabelUI");
            boolean targetLabel = (target.getUIClassID() == "LabelUI");

            if (((sourceLabel && !targetLabel) ||
                 (targetLabel && !sourceLabel)) &&
                (position == SwingConstants.EAST ||
                 position == SwingConstants.WEST)) {
                return getCBRBPadding(source, target, position,
                                      dluToPixels(3, position));
            }
            return getCBRBPadding(source, target, position,
                                  dluToPixels(4, position));
        }
    }

    public int getContainerGap(JComponent component, int position,
            Container parent) {
        super.getContainerGap(component, position, parent);
        return getCBRBPadding(component, position, dluToPixels(7, position));
    }
    
    private int dluToPixels(int dlu, int direction) {
        if (baseUnitX == 0) {
            calculateBaseUnits();
        }
        if (direction == SwingConstants.EAST ||
                         direction == SwingConstants.WEST) {
            return dlu * baseUnitX / 4;
        }
        assert (direction == SwingConstants.NORTH ||
                direction == SwingConstants.SOUTH);
        return dlu * baseUnitY / 8;
    }

    private void calculateBaseUnits() {
    	LFont font = LFont.getDefaultFont();
        baseUnitX = font.stringWidth(
                      "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz");
        baseUnitX = (baseUnitX / 26 + 1) / 2;
        baseUnitY = font.getAscent() + font.getDescent() - 1;
    }
}
