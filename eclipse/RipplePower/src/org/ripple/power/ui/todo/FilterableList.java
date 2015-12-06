package org.ripple.power.ui.todo;

import javax.swing.*;

public class FilterableList extends JList<Object> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public FilterableList() {
		FilterableListModel model = new FilterableListModel();
		setModel(model);
	}

	public void installFilterField(JTextField input) {
		if (input != null) {
			FilterableListModel model = (FilterableListModel) getModel();
			input.getDocument().addDocumentListener(model);
		}
	}

	public void uninstallFilterField(JTextField input) {
		if (input != null) {
			FilterableListModel model = (FilterableListModel) getModel();
			input.getDocument().removeDocumentListener(model);
		}
	}

	public void setModel(ListModel<Object> model) {
		if (!(model instanceof FilterableListModel)) {
			throw new IllegalArgumentException();
		} else {
			super.setModel(model);
		}
	}

	public void addElement(Object element) {
		((FilterableListModel) getModel()).addElement(element);
	}

	public FilterableListModel getContents() {
		return (FilterableListModel) getModel();
	}

}
