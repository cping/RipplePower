package org.ripple.power.ui;

import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JDialog;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.ripple.power.config.LSystem;
import org.ripple.power.i18n.LangConfig;
import org.ripple.power.news.NewsParser;
import org.ripple.power.news.NewsParser.News;
import org.ripple.power.txns.Updateable;
import org.ripple.power.ui.graphics.LColor;
import org.ripple.power.utils.StringUtils;

public class RPNewsDialog extends JDialog {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private RPCButton _queryButton;
	private RPLabel _loadStatusLabel;
	private RPLabel _KeywordLabel;
	private RPList _newsList;
	private javax.swing.JScrollPane jScrollPane1;
	private RPTextBox _wordText;
	private List<News> _find_result;

	public static RPNewsDialog showDialog() {
		RPNewsDialog dialog = new RPNewsDialog(LSystem.applicationMain);
		dialog.pack();
		dialog.setLocationRelativeTo(LSystem.applicationMain);
		dialog.setVisible(true);
		return dialog;
	}

	public RPNewsDialog(Window parent) {
		super(parent,LangConfig.get(RPNewsDialog.class, "news", "News"), Dialog.ModalityType.MODELESS);
		setResizable(false);
		Dimension dim = new Dimension(366, 550);
		setPreferredSize(dim);
		setSize(dim);
		initComponents();
	}

	private void initComponents() {

		_loadStatusLabel = new RPLabel();
		jScrollPane1 = new javax.swing.JScrollPane();
		_newsList = new RPList();
		_KeywordLabel = new RPLabel();
		_wordText = new RPTextBox();
		_queryButton = new RPCButton();

		getContentPane().setLayout(null);

		Font font = new Font(LangConfig.fontName, 0, 12);

		_loadStatusLabel.setFont(font); // NOI18N
		_loadStatusLabel
				.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
		getContentPane().add(_loadStatusLabel);
		_loadStatusLabel.setBounds(10, 490, 340, 16);
		_newsList.setBorder(BorderFactory.createEtchedBorder(LColor.WHITE,
				LColor.WHITE));
		_newsList.addListSelectionListener(new ListSelectionListener() {

			@Override
			public void valueChanged(ListSelectionEvent e) {
				int idx = _newsList.getSelectedIndex();
				if (idx > 0 && _find_result != null && _find_result.size() > 0) {
					LSystem.openURL(_find_result.get(idx).url);
				}
			}
		});

		jScrollPane1.setViewportView(_newsList);

		getContentPane().add(jScrollPane1);
		jScrollPane1.setBounds(10, 50, 340, 430);

		_KeywordLabel.setText(LangConfig.get(this, "keyword", "Keyword"));
		_KeywordLabel.setFont(font);
		getContentPane().add(_KeywordLabel);
		_KeywordLabel.setBounds(10, 5, 60, 30);

		_wordText.setText("Ripple/Bitcoin");
		getContentPane().add(_wordText);
		_wordText.setBounds(70, 10, 170, 21);

		_queryButton.setText(LangConfig.get(this, "search", "Search"));
		_queryButton.setFont(font);
		getContentPane().add(_queryButton);
		_queryButton.setBounds(261, 10, 90, 23);
		_queryButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				String query = _wordText.getText().trim();
				if (query.length() > 0) {
					loadNews(query);
				}
			}
		});

		getContentPane().setBackground(LSystem.dialogbackground);
		String query = _wordText.getText().trim();
		if (query.length() > 0) {
			loadNews(query);
		}
		pack();
	}

	private Thread _newThread;

	private void loadNews(final String query) {
		_loadStatusLabel.setText("Loading......");
		Updateable update = new Updateable() {

			@Override
			public void action(Object o) {
				List<News> tmp = null;
				if (query.indexOf(" ") == -1 && query.indexOf("/") == -1) {
					tmp = NewsParser.getAllNew(query);
				} else {
					if (query.indexOf("/") != -1) {
						if (tmp == null) {
							tmp = new ArrayList<News>(100);
						}
						String[] split = StringUtils.split(query, "/");
						for (String s : split) {
							tmp.addAll(NewsParser.getAllNew(s));
						}
					}
					if (query.indexOf(" ") != -1) {
						if (tmp == null) {
							tmp = new ArrayList<News>(100);
						}
						String[] split = StringUtils.split(query, " ");
						for (String s : split) {
							tmp.addAll(NewsParser.getAllNew(s));
						}
					}
				}
				final List<News> news = tmp;
				if (news != null && news.size() > 0) {
					_newsList
							.setModel(new javax.swing.AbstractListModel<Object>() {

								private static final long serialVersionUID = 1L;

								@Override
								public int getSize() {
									return news.size();
								}

								@Override
								public Object getElementAt(int index) {
									return news.get(index).title;
								}

							});
					_find_result = news;
				}
				_loadStatusLabel.setText("Completed");

			}
		};
		if (_newThread == null) {
			_newThread = LSystem.postThread(update);
		} else {
			try {
				_newThread.interrupt();
				_newThread = null;
			} catch (Exception ex) {

			}
			_newThread = LSystem.postThread(update);
		}
	}
}
