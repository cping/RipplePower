package org.ripple.power.ui.view;

import java.awt.Component;
import java.awt.Container;
import java.awt.LayoutManager2;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import javax.swing.AbstractButton;
import javax.swing.ImageIcon;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.text.JTextComponent;

import net.miginfocom.swing.MigLayout;

import org.ripple.power.i18n.LangConfig;
import org.ripple.power.ui.UIConfig;
import org.ripple.power.ui.graphics.LImage;

import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.google.common.util.concurrent.Uninterruptibles;

public class Panels {

	private static Optional<LightBoxPanel> lightBoxPanel = Optional.absent();

	private static Optional<LightBoxPanel> lightBoxPopoverPanel = Optional
			.absent();

	private static JDialog applicationWindow;

	private static boolean deferredHideEventInProgress = false;

	private static HashMap<String, ImageIcon> _speed_images = new HashMap<String, ImageIcon>(
			10);
	private static LImage _speed_image;

	public synchronized static ImageIcon getSpeedImage(String name) {
		name = name.toLowerCase();
		ImageIcon image = _speed_images.get(name);
		if (image == null) {
			if (_speed_image == null) {
				_speed_image = LImage.createImage("icons/speed.png");
			}
			if ("empty".equals(name)) {
				image = new ImageIcon(_speed_image
						.getSubImageSize(0, 0, 34, 20).getBufferedImage());
			} else if ("lv1".equals(name)) {
				image = new ImageIcon(_speed_image.getSubImageSize(34, 0, 68,
						20).getBufferedImage());
			} else if ("lv2".equals(name)) {
				image = new ImageIcon(_speed_image.getSubImageSize(68, 0, 102,
						20).getBufferedImage());
			} else if ("lv3".equals(name)) {
				image = new ImageIcon(_speed_image.getSubImageSize(0, 20, 34,
						40).getBufferedImage());
			} else if ("lv4".equals(name)) {
				image = new ImageIcon(_speed_image.getSubImageSize(34, 20, 68,
						40).getBufferedImage());
			} else if ("lv5".equals(name)) {
				image = new ImageIcon(_speed_image.getSubImageSize(68, 20, 102,
						40).getBufferedImage());
			} else if ("black".equals(name)) {
				image = new ImageIcon(_speed_image.getSubImageSize(0, 40, 34,
						60).getBufferedImage());
			} else if ("lv0".equals(name)) {
				image = new ImageIcon(_speed_image.getSubImageSize(34, 40, 68,
						60).getBufferedImage());
			} else if ("all".equals(name)) {
				image = new ImageIcon(_speed_image.getSubImageSize(68, 40, 102,
						60).getBufferedImage());
			}
			_speed_images.put(name, image);
		}
		return image;
	}

	public static void setApplication(JDialog application) {
		Panels.applicationWindow = application;
	}

	public static JDialog getApplication() {
		return applicationWindow;
	}

	public synchronized static boolean isDeferredHideEventInProgress() {
		return deferredHideEventInProgress;

	}

	public synchronized static void setDeferredHideEventInProgress(boolean value) {
		deferredHideEventInProgress = value;
	}

	public static String migXYLayout() {
		return migLayout("fill,insets 0");
	}

	public static String migXLayout() {
		return migLayout("fillx,insets 0");
	}

	public static String migLayout(String layout) {
		return layout + (LangConfig.isLeftToRight() ? "" : ",rtl");
	}

	public static String migXYDetailLayout() {
		return migLayout("fill,insets 10 5 5 5");
	}

	public static String migXPopoverLayout() {
		return migLayout("fill,insets 10 10 10 10");
	}

	public static JPanel newPanel() {

		return Panels.newPanel(new MigLayout(migXYLayout(), "[]", "[]"));

	}

	public static JPanel newPanel(LayoutManager2 layout) {
		JPanel panel = new JPanel(layout);
		panel.setBackground(UIConfig.dialogbackground);
		panel.setOpaque(false);
		panel.applyComponentOrientation(LangConfig
				.currentComponentOrientation());
		return panel;
	}

	public static JPanel newRoundedPanel() {

		return newRoundedPanel(new MigLayout(Panels.migXLayout(), "[]", "[]"));

	}

	public static JPanel newRoundedPanel(LayoutManager2 layout) {

		JPanel panel = new RoundedPanel(layout);

		return panel;

	}

	public synchronized static boolean isLightBoxShowing() {

		return lightBoxPanel.isPresent();

	}

	public synchronized static void showLightBox(final JPanel panel) {

		Preconditions.checkState(SwingUtilities.isEventDispatchThread(),
				"LightBox requires the EDT");

		if (isDeferredHideEventInProgress()) {

			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {

					Uninterruptibles.sleepUninterruptibly(100,
							TimeUnit.MILLISECONDS);

					Preconditions.checkState(!isDeferredHideEventInProgress(),
							"Deferred hide has taken too long to complete");

					Preconditions.checkState(!lightBoxPanel.isPresent(),
							"Light box should never be called twice ");

					allowFocus(Panels.getApplication(), false);

					lightBoxPanel = Optional.of(new LightBoxPanel(panel,
							JLayeredPane.MODAL_LAYER));

				}
			});

			return;
		}

		Preconditions.checkState(!lightBoxPanel.isPresent(),
				"Light box should never be called twice ");

		allowFocus(Panels.getApplication(), false);

		lightBoxPanel = Optional.of(new LightBoxPanel(panel,
				JLayeredPane.MODAL_LAYER));

	}

	public synchronized static void hideLightBoxIfPresent() {

		Preconditions.checkState(SwingUtilities.isEventDispatchThread(),
				"LightBoxPopover requires the EDT");

		hideLightBoxPopoverIfPresent();

		if (lightBoxPanel.isPresent()) {
			lightBoxPanel.get().close();
		}

		lightBoxPanel = Optional.absent();

		allowFocus(Panels.getApplication(), true);

	}

	private static void allowFocus(final Component component,
			final boolean allowFocus) {
		if (component instanceof AbstractButton) {
			component.setFocusable(allowFocus);
		}
		if (component instanceof JComboBox) {
			component.setFocusable(allowFocus);
		}
		if (component instanceof JTree) {
			component.setFocusable(allowFocus);
		}
		if (component instanceof JTextComponent) {
			component.setFocusable(allowFocus);
		}
		if (component instanceof JTable) {
			component.setFocusable(allowFocus);
		}
		if (component instanceof Container) {
			for (Component child : ((Container) component).getComponents()) {
				allowFocus(child, allowFocus);
			}
		}
	}

	public static void invalidate(JPanel panel) {
		panel.validate();
		panel.repaint();
	}

	public synchronized static void showLightBoxPopover(JPanel panel) {

		Preconditions.checkState(SwingUtilities.isEventDispatchThread(),
				"LightBoxPopover requires the EDT");
		Preconditions
				.checkState(lightBoxPanel.isPresent(),
						"LightBoxPopover should not be called unless a light box is showing");
		Preconditions.checkState(!lightBoxPopoverPanel.isPresent(),
				"LightBoxPopover should never be called twice");

		lightBoxPopoverPanel = Optional.of(new LightBoxPanel(panel,
				JLayeredPane.DRAG_LAYER));

	}

	public synchronized static void hideLightBoxPopoverIfPresent() {

		Preconditions.checkState(SwingUtilities.isEventDispatchThread(),
				"LightBoxPopover requires the EDT");

		if (lightBoxPopoverPanel.isPresent()) {
			lightBoxPopoverPanel.get().close();
		}

		lightBoxPopoverPanel = Optional.absent();

	}

}
