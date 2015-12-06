package org.ripple.power.chart.test;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Image;
import java.io.UnsupportedEncodingException;

import javax.print.attribute.standard.MediaSize.Other;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import org.ripple.power.RippleSeedAddress;
import org.ripple.power.helper.GraphicTool;
import org.ripple.power.timer.LTimerContext;
import org.ripple.power.ui.RPChartsHistoryDialog;
import org.ripple.power.ui.RPChatClientDialog;
import org.ripple.power.ui.RPChatServerDialog;
import org.ripple.power.ui.RPConfigDialog;
import org.ripple.power.ui.RPDefineTradingDialog;
import org.ripple.power.ui.RPDownloadDialog;
import org.ripple.power.ui.RPHashInfoDialog;
import org.ripple.power.ui.RPNewsDialog;
import org.ripple.power.ui.RPOnlineWalletDialog;
import org.ripple.power.ui.RPOtherServicesDialog;
import org.ripple.power.ui.RPSendIOUDialog;
import org.ripple.power.ui.RPPayPortDialog;
import org.ripple.power.ui.RPPriceWarningDialog;
import org.ripple.power.ui.RPSelectChatDialog;
import org.ripple.power.ui.RPSelectWalletDialog;
import org.ripple.power.ui.RPTradingToolsDialog;
import org.ripple.power.ui.RPSendXRPDialog;
import org.ripple.power.ui.RPRippledMemoDialog;
import org.ripple.power.ui.RPSelectAddressDialog;
import org.ripple.power.ui.RPSendIOUDialog;
import org.ripple.power.ui.RPSendFlagsDialog;
import org.ripple.power.ui.UIConfig;
import org.ripple.power.ui.UIMessage;
import org.ripple.power.ui.UIRes;
import org.ripple.power.ui.graphics.LColor;
import org.ripple.power.ui.graphics.LGraphics;
import org.ripple.power.ui.graphics.chart.BarChartCanvas;
import org.ripple.power.ui.graphics.chart.ChartValue;
import org.ripple.power.ui.graphics.chart.ChartValueSerie;
import org.ripple.power.ui.graphics.chart.LineChartCanvas;
import org.ripple.power.ui.graphics.chart.StackedBarChartCanvas;
import org.ripple.power.ui.projector.UIScene;
import org.ripple.power.ui.projector.action.avg.AVGDialog;
import org.ripple.power.ui.projector.action.avg.AVGScreen;
import org.ripple.power.ui.projector.action.avg.command.Command;
import org.ripple.power.ui.projector.core.graphics.Screen;
import org.ripple.power.ui.projector.core.graphics.Screen.LTouch;
import org.ripple.power.ui.projector.core.graphics.component.LButton;
import org.ripple.power.ui.projector.core.graphics.component.LMessage;
import org.ripple.power.ui.projector.core.graphics.component.LPaper;
import org.ripple.power.ui.projector.core.graphics.component.LSelect;
import org.ripple.power.ui.view.RPToast;
import org.ripple.power.utils.Base64Coder;

import com.ripple.client.pubsub.Publisher;

public class DialogTest {

	public static class MyAVGScreen extends AVGScreen {

		int type;

		public MyAVGScreen(Image image) {
			super(true, "show/s1.txt", image);

		}

		public void onLoading() {

		}

		public void drawScreen(LGraphics g) {

		}

		public void initCommandConfig(Command command) {

		}

		public void initMessageConfig(LMessage message) {

		}

		public void initSelectConfig(LSelect select) {
		}

		public boolean nextScript(String mes) {
			return true;
		}

		public void onExit() {

		}

		public void onSelect(String message, int type) {

		}

		public void alter(LTimerContext timer) {

		}

	}

	public static void main(String[] args) {

		JFrame frame = new JFrame();
		Dimension size = new Dimension(400, 400);
		frame.setPreferredSize(size);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

	
		// RPTradingToolsDialog.showDialog("FFFFFFFFF", frame);
		// RPPayPortDialog.showDialog("FFFFFFFFFFFF", frame);

	}
}
