package org.ripple.power.chart.test;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;

import org.ripple.power.ui.editor.ROCScriptEditor;

public class TextEditorDemo2 extends JFrame {

	public TextEditorDemo2() {

		JPanel scrollPane = new JPanel();

		ROCScriptEditor textArea = new ROCScriptEditor();
		textArea.setPreferredSize(new Dimension(700, 400));
		// scrollPane.setMinimumSize(new Dimension(700, 400));

		scrollPane.add(textArea);

		setContentPane(scrollPane);
		// cp.add(sp);

		setTitle("Text Editor Demo");
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		pack();
		setLocationRelativeTo(null);

	}

	public static void main(String[] args) {
		// Start all Swing applications on the EDT.
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				new TextEditorDemo2().setVisible(true);
			}
		});
	}

}