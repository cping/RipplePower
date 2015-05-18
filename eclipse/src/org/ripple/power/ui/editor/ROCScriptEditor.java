package org.ripple.power.ui.editor;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.KeyStroke;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.filechooser.FileFilter;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;
import javax.swing.undo.UndoManager;

import org.ripple.power.ui.graphics.LColor;

public class ROCScriptEditor extends JPanel {

	public final static ROCFileFilter FILTER = new ROCFileFilter();

	private static class ROCFileFilter extends FileFilter {
		public boolean accept(File f) {
			if (f.isDirectory()) {
				return true;
			}
			return f.getName().endsWith(".txt") || f.getName().endsWith(".roc")
					|| f.getName().endsWith(".script");
		}

		public String getDescription() {
			return ".txt|.roc|.script";
		}
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private ROCFileListener _listener = null;

	public boolean _isEdited = false;

	public boolean _isSaved = true;

	private SourcePaper _textEdit = new SourcePaper();

	private JScrollPane _jScrollPanel = new JScrollPane();

	private BorderLayout _borderLayout = new BorderLayout();

	private String fileName = "";

	private File file = null;

	private UndoManager manager = new UndoManager();

	public void setText(String str) {
		_textEdit.setText(str);
	}

	public SourcePaper getROC() {
		return _textEdit;
	}

	public String getText() {
		return _textEdit.getText();
	}

	public ROCScriptEditor() {
		try {
			this.setLayout(_borderLayout);
			this.add(_jScrollPanel, java.awt.BorderLayout.CENTER);

			_jScrollPanel.getViewport().add(_textEdit);
			_jScrollPanel.setBackground(new Color(70, 70, 70));
			_jScrollPanel.setForeground(Color.WHITE);
			_jScrollPanel.setBorder(BorderFactory
					.createLineBorder(LColor.black));

			_textEdit.setDocument(new SourceDocument());
			_textEdit.getDocument().addDocumentListener(
					new ROCEditor_Edit_documentAdapter(this));

			_textEdit.getDocument().addUndoableEditListener(
					new ROCEditorListener(manager));

			this.getActionMap().put("ctrl_s", new AbstractAction("ctrl_s") {
				/**
				 * 
				 */
				private static final long serialVersionUID = 1L;

				@Override
				public void actionPerformed(ActionEvent evt) {
					if (_isEdited()) {
						saveFile();
					} else {
						saveAs();
					}

				}
			});
			this.getInputMap().put(KeyStroke.getKeyStroke("control S"),
					"ctrl_s");
			_textEdit.getActionMap().put("ctrl_s",
					new AbstractAction("ctrl_s") {
						/**
				 * 
				 */
						private static final long serialVersionUID = 1L;

						@Override
						public void actionPerformed(ActionEvent evt) {
							if (file == null) {
								saveAs();
							} else {
								saveFile();
							}

						}
					});
			_textEdit.getInputMap().put(KeyStroke.getKeyStroke("control S"),
					"ctrl_s");

			this.getActionMap().put("ctrl_o", new AbstractAction("ctrl_o") {
				/**
				 * 
				 */
				private static final long serialVersionUID = 1L;

				@Override
				public void actionPerformed(ActionEvent evt) {
					openNew();
				}
			});
			this.getInputMap().put(KeyStroke.getKeyStroke("control O"),
					"ctrl_o");
			_textEdit.getActionMap().put("ctrl_o",
					new AbstractAction("ctrl_o") {
						/**
				 * 
				 */
						private static final long serialVersionUID = 1L;

						@Override
						public void actionPerformed(ActionEvent evt) {
							openNew();
						}
					});
			_textEdit.getInputMap().put(KeyStroke.getKeyStroke("control O"),
					"ctrl_o");

			_textEdit.setCaretPosition(0);

			_textEdit.getInputMap().put(KeyStroke.getKeyStroke("control Z"),
					"ctrl_z");
			_textEdit.getActionMap().put("ctrl_z",
					new AbstractAction("ctrl_z") {

						/**
				 * 
				 */
						private static final long serialVersionUID = 1L;

						@Override
						public void actionPerformed(ActionEvent e) {
							undo();

						}

					});

			_textEdit.getInputMap().put(KeyStroke.getKeyStroke("control Y"),
					"ctrl_y");
			_textEdit.getActionMap().put("ctrl_y",
					new AbstractAction("ctrl_y") {

						/**
				 * 
				 */
						private static final long serialVersionUID = 1L;

						@Override
						public void actionPerformed(ActionEvent e) {
							redo();
						}

					});

			_textEdit.getInputMap().put(
					KeyStroke.getKeyStroke("control shift Z"), "ctrl_shift_z");
			_textEdit.getActionMap().put("ctrl_shift_z",
					new AbstractAction("ctrl_shift_z") {

						/**
				 * 
				 */
						private static final long serialVersionUID = 1L;

						@Override
						public void actionPerformed(ActionEvent e) {
							redo();
						}

					});
			_textEdit.setText("#ROC Script\n");
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public File getFile() {
		return file;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String filename) {
		this.fileName = filename;
	}

	public void setFile(File file) {
		this.file = file;
		setFileName(file.getName());
		_isSaved = true;
		_isEdited = false;
	}

	public boolean _isEdited() {
		return this._isEdited;
	}

	public void openFile() {
		try {
			_textEdit.setText(readFile(file));
			_textEdit.setCaretPosition(0);
			_isSaved = true;
			_isEdited = false;
		} catch (IOException ex) {
		}
	}

	public boolean close() {
		boolean savedSuccessfully = true;
		if (_isEdited) {
			if (JOptionPane.showConfirmDialog(this.getParent().getParent(),
					"Do you want to save changes to the current file?",
					"Confirm", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
				savedSuccessfully = this.saveFile();
			}
		}
		if (savedSuccessfully) {
			setFileName("");
			file = null;
			_textEdit.setText("");
			_isSaved = true;
			_isEdited = false;
			return true;
		}
		return false;
	}

	private boolean saveAs() {
		if (_listener != null) {
			return _listener.doSave();
		}
		return false;
	}

	private void openNew() {
		if (_listener != null) {
			_listener.doLoad();
		}
	}

	private String readFile(File file) throws IOException {
		StringBuffer fileBuffer;
		String fileString = null;
		String line;

		try {
			FileReader in = new FileReader(file);
			BufferedReader dis = new BufferedReader(in);
			fileBuffer = new StringBuffer();
			while ((line = dis.readLine()) != null) {
				fileBuffer.append(line + "\n");
			}
			in.close();
			fileString = fileBuffer.toString();
		} catch (IOException e) {
			throw e;
		}
		return fileString;
	}

	public boolean saveFile() {
		try {
			if (file != null) {
				PrintWriter out = new PrintWriter(new BufferedWriter(
						new FileWriter(file)));
				out.print(_textEdit.getText());
				out.flush();
				out.close();
				_isSaved = true;
				_isEdited = false;
				return true;
			}
			return saveAs();
		} catch (IOException e) {
			return false;
		}
	}

	void edit_Performed() {
		_isEdited = true;
		_isSaved = false;
		if (_listener != null) {
			_listener.setButtons();
		}
	}

	public void undo() {
		try {
			manager.undo();
		} catch (CannotUndoException e) {
		}

	}

	public void redo() {
		try {
			manager.redo();
		} catch (CannotRedoException e) {
		}

	}

	public ROCFileListener getListener() {
		return _listener;
	}

	public void setListener(ROCFileListener l) {
		this._listener = l;
	}

	public boolean isEdited() {
		return _isEdited;
	}

	public void setEdited(boolean e) {
		this._isEdited = e;
	}

	public boolean isSaved() {
		return _isSaved;
	}

	public void setSaved(boolean s) {
		this._isSaved = s;
	}

}

class ROCEditor_Edit_documentAdapter implements DocumentListener {
	ROCScriptEditor adaptee;

	public ROCEditor_Edit_documentAdapter(ROCScriptEditor adaptee) {
		this.adaptee = adaptee;
	}

	@Override
	public void insertUpdate(DocumentEvent evt) {
		adaptee.edit_Performed();
	}

	@Override
	public void removeUpdate(DocumentEvent evt) {
		adaptee.edit_Performed();
	}

	@Override
	public void changedUpdate(DocumentEvent evt) {
		adaptee.edit_Performed();
	}
}
