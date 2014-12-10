package org.ripple.power.ui.todo;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.ripple.power.utils.GraphicsUtils;

import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.TooManyListenersException;

public class JImagePanel extends JPanel {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private boolean showHintOnEmptyImage;
	private Image image;
	private String imagePath;
	private boolean editable;
	private Image cachedImage;
	private Image dragImage;

	static {
		UIManager.getDefaults().addResourceBundle(
				"passwordstore.swingx.resources.strings");
	}

	public JImagePanel() {
		setBackground(Color.WHITE);
		setOpaque(true);
		showHintOnEmptyImage = true;
		updateHintIfNecessary();
	}

	public void setShowHintOnEmptyImage(boolean value) {
		boolean oldValue = showHintOnEmptyImage;
		showHintOnEmptyImage = value;
		firePropertyChange("showHintOnEmptyImage", oldValue, value);
	}

	public boolean getShowHintOnEmptyImage() {
		return showHintOnEmptyImage;
	}

	public void setEditable(boolean editable) {
		if (editable != this.editable) {
			this.editable = editable;
			if (editable) {
				setDropTarget(new DropTarget());
				try {
					getDropTarget().addDropTargetListener(
							new DropTargetHandler());
				} catch (TooManyListenersException ex) {
				}
				enableEvents(MouseEvent.MOUSE_EVENT_MASK);
			} else {
				setDropTarget(null);
			}
			updateHintIfNecessary();
			firePropertyChange("editable", !editable, editable);
		}
	}

	public boolean isEditable() {
		return editable;
	}

	public final void setImage(Image image) {
		setImage0(image);
		setImagePath0(null);
	}

	private void setImage0(Image image) {
		Image oldImage = this.image;
		this.image = image;
		clearCachedImage();
		firePropertyChange("image", oldImage, image);
		revalidate();
		repaint();
		updateHintIfNecessary();
	}

	public final Image getImage() {
		return image;
	}

	protected void setDragImage(Image image) {
		Image oldImage = this.dragImage;
		this.dragImage = image;
		clearCachedImage();
		updateHintIfNecessary();
		firePropertyChange("dragImage", oldImage, image);
	}

	protected Image getDragImage() {
		return dragImage;
	}

	public final void setImagePath(String path) {
		if (path == null) {
			setImage0(null);
		} else {
			setImage0(GraphicsUtils.loadImage(path));
		}
		setImagePath0(path);
	}

	private void setImagePath0(String path) {
		String oldPath = this.imagePath;
		this.imagePath = path;
		firePropertyChange("imagePath", oldPath, imagePath);
	}

	public final String getImagePath() {
		return imagePath;
	}

	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		Image image = getImageToDraw();
		if (image != null) {
			Point loc = getImageLocation();
			g.drawImage(image, loc.x, loc.y, this);
		}
	}

	private Image getImageToDraw() {
		if (cachedImage == null) {
			cachedImage = createCachedImage();
		}
		return cachedImage;
	}

	private Image createCachedImage() {
		Image image = getDragImage();
		boolean isDrag = true;
		if (image == null) {
			image = getImage();
			isDrag = false;
		}
		if (image != null) {
			int iw = image.getWidth(this);
			int ih = image.getHeight(this);
			if (iw > 0 && ih > 0) {
				Insets insets = getInsets();
				int w = getWidth() - insets.left - insets.right;
				int h = getHeight() - insets.top - insets.bottom;
				float aspectRatio = (float) image.getWidth(this)
						/ (float) image.getHeight(this);
				int targetWidth;
				int targetHeight;
				if (iw > ih) {
					targetWidth = w;
					targetHeight = (int) (targetWidth / aspectRatio);
					if (targetHeight > h) {
						targetHeight = h;
						targetWidth = (int) (aspectRatio * targetHeight);
					}
				} else {
					targetHeight = h;
					targetWidth = (int) (aspectRatio * targetHeight);
					if (targetWidth > w) {
						targetWidth = w;
						targetHeight = (int) (targetWidth / aspectRatio);
					}
				}
				if (targetWidth != iw || targetHeight != ih) {
					Image cachedImage;
					if (isDrag) {
						if (getGraphicsConfiguration() != null) {
							cachedImage = getGraphicsConfiguration()
									.createCompatibleImage(targetWidth,
											targetHeight,
											Transparency.TRANSLUCENT);
						} else {
							cachedImage = new BufferedImage(targetWidth,
									targetHeight, BufferedImage.TYPE_INT_ARGB);
						}
						Graphics imageG = cachedImage.getGraphics();
						if (imageG instanceof Graphics2D) {
							((Graphics2D) imageG).setComposite(AlphaComposite
									.getInstance(AlphaComposite.SRC_OVER, .3f));
							((Graphics2D) imageG).setRenderingHint(
									RenderingHints.KEY_INTERPOLATION,
									RenderingHints.VALUE_INTERPOLATION_BICUBIC);
						}
						imageG.drawImage(image, 0, 0, targetWidth,
								targetHeight, 0, 0, iw, ih, this);
						imageG.dispose();
					} else {
						cachedImage = createImage(this, image, targetWidth,
								targetHeight);
					}
					return cachedImage;
				}
				return image;
			}
		}
		return null;
	}

	private static Image createImage(Component c, Image image, int w, int h) {
		int iw = image.getWidth(null);
		int ih = image.getHeight(null);
		if (iw > 0 && ih > 0) {
			float aspectRatio = (float) iw / (float) ih;
			int targetWidth;
			int targetHeight;
			if (iw > ih) {
				targetWidth = w;
				targetHeight = (int) (targetWidth / aspectRatio);
				if (targetHeight > h) {
					targetHeight = h;
					targetWidth = (int) (aspectRatio * targetHeight);
				}
			} else {
				targetHeight = h;
				targetWidth = (int) (aspectRatio * targetHeight);
				if (targetWidth > w) {
					targetWidth = w;
					targetHeight = (int) (targetWidth / aspectRatio);
				}
			}
			if (targetWidth != iw || targetHeight != iw) {
				Image cachedImage;
				GraphicsConfiguration gc;
				if (c != null && (gc = c.getGraphicsConfiguration()) != null) {
					cachedImage = gc.createCompatibleImage(targetWidth,
							targetHeight, Transparency.TRANSLUCENT);
				} else {
					cachedImage = new BufferedImage(targetWidth, targetHeight,
							BufferedImage.TYPE_INT_ARGB);
				}
				Graphics imageG = cachedImage.getGraphics();
				if (imageG instanceof Graphics2D) {
					((Graphics2D) imageG).setRenderingHint(
							RenderingHints.KEY_INTERPOLATION,
							RenderingHints.VALUE_INTERPOLATION_BILINEAR);
				}
				imageG.drawImage(image, 0, 0, targetWidth, targetHeight, 0, 0,
						iw, ih, null);
				imageG.dispose();
				return cachedImage;
			}
			return image;
		}
		return null;
	}

	private Point getImageLocation() {
		Image image = getImageToDraw();
		Insets insets = getInsets();
		int w = getWidth() - insets.left - insets.right;
		int h = getHeight() - insets.top - insets.bottom;
		int iw = image.getWidth(this);
		int ih = image.getHeight(this);
		return new Point(insets.left + (w - iw) / 2, insets.top + (h - ih) / 2);
	}

	private void clearCachedImage() {
		cachedImage = null;
		repaint();
	}

	public void setBounds(int x, int y, int w, int h) {
		super.setBounds(x, y, w, h);
		clearCachedImage();
	}

	public boolean imageUpdate(Image img, int infoflags, int x, int y, int w,
			int h) {
		if (img == getImage()) {
			return super.imageUpdate(img, infoflags, x, y, w, h);
		}
		return false;
	}

	public void setBorder(Border border) {
		super.setBorder(border);
		clearCachedImage();
	}

	protected void showChooser() {
		JFileChooser chooser = new JFileChooser();
		chooser.addChoosableFileFilter(new FileNameExtensionFilter("image",
				"jpg", "gif", "jpeg", "png"));
		String image = getImagePath();
		if (image != null) {
			chooser.setSelectedFile(new File(image));
		}
		chooser.setMultiSelectionEnabled(false);
		if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
			File file = chooser.getSelectedFile();
			setImagePath(file.getAbsolutePath());
		}
	}

	protected void processMouseEvent(MouseEvent e) {
		super.processMouseEvent(e);
		if (!e.isConsumed() && e.getClickCount() == 1) {
			showChooser();
		}
	}

	private void updateHintIfNecessary() {
		Component hint = getHintComponent();
		if (getImage() == null && getDragImage() == null
				&& getShowHintOnEmptyImage() && isEditable()) {
			if (hint == null) {
				hint = createHintComponent();
				setLayout(new BorderLayout());
			}
			add(hint, BorderLayout.SOUTH);
			revalidate();
			repaint();
		} else if (hint != null) {
			remove(hint);
			revalidate();
			repaint();
		}
	}

	private Component getHintComponent() {
		if (getComponentCount() > 0) {
			return getComponent(0);
		}
		return null;
	}

	private Component createHintComponent() {
		// PENDING: this is a hack, should be a text area or something that
		// can wrap
		JLabel label = new JLabel(getLabelText());
		label.setHorizontalAlignment(JLabel.CENTER);
		label.setForeground(Color.LIGHT_GRAY);
		label.setBorder(new EmptyBorder(4, 4, 4, 4));
		return label;
	}

	private String getLabelText() {
		String text = UIManager.getString("ImagePanel.clickText");
		if (text == null) {
			text = "Click or Drop to Set";
		}
		return text;
	}

	private class DropTargetHandler implements ActionListener,
			DropTargetListener {
		private String dragFile;
		private Timer dragTimer;
		private boolean validDragImage;

		public void dragEnter(DropTargetDragEvent e) {
			dragFile = null;
			stopTimer();
			validDragImage = false;
			if (e.isDataFlavorSupported(DataFlavor.javaFileListFlavor)) {
				try {
					@SuppressWarnings("unchecked")
					List<File> files = (List<File>) e.getTransferable()
							.getTransferData(DataFlavor.javaFileListFlavor);
					if (files.size() == 1) {
						File file = files.get(0);
						dragFile = file.getCanonicalFile().getAbsolutePath();
						updateDragImage();
					}
				} catch (IOException ex) {
				} catch (UnsupportedFlavorException ex) {
				}
			}
		}

		public void dragOver(DropTargetDragEvent e) {
		}

		public void dropActionChanged(DropTargetDragEvent e) {
		}

		public void dragExit(DropTargetEvent e) {
			setDragImage(null);
			stopTimer();
		}

		public void drop(DropTargetDropEvent e) {
			if (dragFile != null && !validDragImage) {
				updateDragImage();
			}
			boolean accepted = false;
			if (validDragImage) {
				setImage0(getDragImage());
				setDragImage(null);
				setImagePath0(dragFile);
				accepted = true;
			}
			if (accepted) {
				e.acceptDrop(DnDConstants.ACTION_COPY);
			} else {
				e.rejectDrop();
			}
		}

		public void actionPerformed(ActionEvent e) {
			updateDragImage();
		}

		private void updateDragImage() {
			File file = new File(dragFile);
			if (file.length() > 0) {
				try {
					ImageIcon icon = new ImageIcon(dragFile);
					Image image = icon.getImage();
					if (image.getWidth(JImagePanel.this) > 0) {
						stopTimer();
						setDragImage(image);
						validDragImage = true;
					} else if (dragTimer == null) {
						startTimer();
					}
				} catch (Exception ex) {
					validDragImage = true;
					stopTimer();
				}
			} else if (dragTimer == null) {
				startTimer();
			}
		}

		private void startTimer() {
			dragTimer = new Timer(50, this);
			dragTimer.setRepeats(true);
			dragTimer.start();
		}

		private void stopTimer() {
			if (dragTimer != null) {
				dragTimer.stop();
				dragTimer = null;
			}
		}
	}
}
