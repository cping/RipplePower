package org.ripple.power.utils;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Desktop;
import java.awt.Desktop.Action;
import java.awt.Color;
import java.awt.Container;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.HierarchyEvent;
import java.awt.event.HierarchyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;


public class SwingUtils {

	public static Rectangle getScreenSize(Window win) {
		Rectangle sb;
		if (win == null) {
			sb = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration()
					.getBounds();
		} else {
			sb = win.getGraphicsConfiguration().getBounds();
		}
		return sb;
	}

	public static Insets getScreenInsets(Window win) {
		Insets si;
		if (win == null) {
			si = Toolkit.getDefaultToolkit().getScreenInsets(
					GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice()
							.getDefaultConfiguration());
		} else {
			si = win.getToolkit().getScreenInsets(win.getGraphicsConfiguration());
		}
		return si;
	}

	public static void fullScreen(Window win) {
		Rectangle screenBounds = SwingUtils.getScreenSize(win);
		win.setBounds(screenBounds);
	}

	public static void centerOnScreen(Window win) {
		Rectangle screenBounds = SwingUtils.getScreenSize(win);
		win.setBounds((int) (screenBounds.getWidth() - win.getWidth()) / 2,
				(int) (screenBounds.getHeight() - win.getHeight()) / 2, win.getWidth(), win.getHeight());
	}

	public static void centerOnParent(Window win) {
		Container parent = win.getParent();

		if (parent != null) {
			Point parentPosition = parent.getLocationOnScreen();
			win.setLocation(parentPosition.x + (parent.getWidth() - win.getWidth()) / 2,
					parentPosition.y + (parent.getHeight() - win.getHeight()) / 2);
		} else {
			centerOnScreen(win);
		}
	}

	public static boolean isTranslucentSupported() {
		GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
		GraphicsDevice gd = ge.getDefaultScreenDevice();

		boolean isUniformTranslucencySupported = gd
				.isWindowTranslucencySupported(GraphicsDevice.WindowTranslucency.TRANSLUCENT);
		return isUniformTranslucencySupported;
	}

	public static void setWindowTransparent(Window win) {
		if (SwingUtils.isTranslucentSupported()) {
			win.setBackground(new Color(0, 0, 0, 0));
		}
	}
	
	public BufferedImage toCompatibleImage(BufferedImage image) {
		GraphicsConfiguration gfx_config = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice()
				.getDefaultConfiguration();

		if (image.getColorModel().equals(gfx_config.getColorModel())){
			return image;
		}

		BufferedImage new_image = gfx_config.createCompatibleImage(image.getWidth(), image.getHeight(),
				image.getTransparency());

		Graphics2D g2d = (Graphics2D) new_image.getGraphics();

		g2d.drawImage(image, 0, 0, null);
		g2d.dispose();

		return new_image;
	}
	
	public static void addWindowListener(final Component source, final WindowListener listener) {
		if (source instanceof Window) {
			((Window)source).addWindowListener(listener);
		} else {
			source.addHierarchyListener(new HierarchyListener() {
				@Override public void hierarchyChanged(HierarchyEvent e) {
					if ((e.getChangeFlags() & HierarchyEvent.SHOWING_CHANGED) == HierarchyEvent.SHOWING_CHANGED) {
						SwingUtilities.getWindowAncestor(source).addWindowListener(listener);
					}
				}
			});
		}
	}

	public static JFrame getJFrame(Component cmp) {
		return (JFrame) SwingUtilities.getWindowAncestor(cmp);
	}

	public static void browse(String url, Component msgParent) {
		boolean error = false;

		if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Action.BROWSE)) {
			try {
				Desktop.getDesktop().browse(new URI(url));
			} catch (URISyntaxException ex) {
				throw new RuntimeException(ex);
			} catch (IOException ex) {
				error = true;
			}
		} else {
			error = true;
		}

		if (error) {
			String msg = "Impossible to open the default browser from the application.\nSorry.";
			JOptionPane.showMessageDialog(msgParent, msg);
		}
	}

	public static void addBrowseBehavior(final Component cmp, final String url){
		if (url == null) return;
		cmp.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		cmp.addMouseListener(new MouseAdapter() {
			@Override public void mousePressed(MouseEvent e) {
				JFrame frame = getJFrame(cmp);
				browse(url, frame);
			}
		});
	}

	public static void packLater(final Window win, final Component parent) {
		win.pack();
		win.setLocationRelativeTo(parent);
		win.addWindowListener(new WindowAdapter() {
			@Override public void windowOpened(WindowEvent e) {
				win.pack();
				win.setLocationRelativeTo(parent);
			}
		});
	}

	public static void importFont(InputStream stream) {
		try {
			Font font1 = Font.createFont(Font.TRUETYPE_FONT, stream);
			GraphicsEnvironment.getLocalGraphicsEnvironment().registerFont(font1);
		} catch (FontFormatException ex) {
			throw new RuntimeException(ex);
		} catch (IOException ex) {
			throw new RuntimeException(ex);
		}
	}
}
