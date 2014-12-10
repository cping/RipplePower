package org.ripple.power.ui.todo;


import java.sql.*;
import java.util.LinkedList;
import java.util.List;

public class TodoItemListBuilder {
	private List<TodoItem> list;
	public TodoItemListBuilder() {
		list = new LinkedList<TodoItem>();
		accessDataBase();
	}

	private void accessDataBase() {
		try {
			Class.forName("org.sqlite.JDBC");
			Connection con = DriverManager
					.getConnection("jdbc:sqlite:stodoitem");
			Statement stat = con.createStatement();
			String sql = "SELECT itemid, type, desc, timeout, period, note, status FROM stodoitem";
			ResultSet rs = stat.executeQuery(sql);
			parse(rs);
			con.close();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public List<TodoItem> getTodoItems() {
		return list;
	}

	private void parse(ResultSet rs) {
		try {
			while (rs.next()) {
				TodoItem node = new TodoItem();
				node.setId(String.valueOf(rs.getInt("itemid")));
				node.setType(rs.getString("type"));
				node.setDesc(rs.getString("desc"));
				node.setTimeout(rs.getString("timeout"));
				node.setPeriod(rs.getString("period"));
				node.setNote(rs.getString("note"));
				node.setStatus(rs.getString("status"));
				list.add(node);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

	}
}
