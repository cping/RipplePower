package org.ripple.power.ui.editor;

import javax.swing.event.UndoableEditEvent;
import javax.swing.event.UndoableEditListener;
import javax.swing.text.AbstractDocument;
import javax.swing.undo.UndoManager;
import javax.swing.undo.UndoableEdit;

public class ROCEditorListener implements UndoableEditListener {
	private UndoManager manager;

	public ROCEditorListener(UndoManager manager) {
		this.manager = manager;
	}

	@Override
	public void undoableEditHappened(UndoableEditEvent ev) {
		UndoableEdit edit = ev.getEdit();
		if (edit instanceof AbstractDocument.DefaultDocumentEvent
				&& ((AbstractDocument.DefaultDocumentEvent) edit).getType() == AbstractDocument.DefaultDocumentEvent.EventType.CHANGE) {
			return;
		}
		manager.addEdit(edit);
	}

}
