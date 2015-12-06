package org.ripple.power.chart.test;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;

import org.ripple.power.ui.editor.EditorDialog;
import org.ripple.power.ui.editor.ROCScriptEditor;

public class TextEditorDemo extends JFrame {

	public TextEditorDemo() {

		JPanel scrollPane = new JPanel();

		ROCScriptEditor textArea = new ROCScriptEditor();
		textArea.setPreferredSize(new Dimension(700, 400));
		// scrollPane.setMinimumSize(new Dimension(700, 400));

		scrollPane.add(textArea);

		setContentPane(scrollPane);
		setTitle("Text Editor Demo");
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		pack();
		setLocationRelativeTo(null);

		EditorDialog.showDialog(this);

	}

	public static void main(String[] args) {
		// Start all Swing applications on the EDT.
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				new TextEditorDemo().setVisible(true);
			}
		});
	}

}