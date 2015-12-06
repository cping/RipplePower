package org.ripple.power.ui.todo;

import org.json.JSONArray;
import org.json.JSONObject;
import org.ripple.power.config.LSystem;
import org.ripple.power.database.SafeData;

import java.util.LinkedList;
import java.util.List;

public class TodoDataBase {

	private final static String _table = "ripple_todo";

	private final static String _table_list = "ripple_arrays";

	private String message;
	private boolean status;
	private SafeData db;

	private TodoDataBase() {
		db = new SafeData();
		try {
			db.setPassphrase(LSystem.getAppPassword());
		} catch (Exception e) {
			try {
				db.clear();
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
	}

	private static TodoDataBase instance;

	public static TodoDataBase getInstance() {
		synchronized (TodoDataBase.class) {
			if (instance == null) {
				instance = new TodoDataBase();
			}
		}
		return instance;
	}

	public boolean addItem(TodoItem todo) {
		JSONObject obj = null;
		try {
			obj = db.getJSON(_table);
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		if (obj == null) {
			obj = new JSONObject();
		}

		JSONArray arrays = null;
		if (!obj.has(_table_list)) {
			arrays = new JSONArray();
		} else {
			arrays = obj.getJSONArray(_table_list);
		}

		int id = (arrays.length() > 0 ? arrays.length() : 0);

		id++;
		JSONObject stodoitem = new JSONObject();
		stodoitem.put("type", todo.getType());
		stodoitem.put("desc", todo.getDesc());
		stodoitem.put("timeout", todo.getTimeout());
		stodoitem.put("period", todo.getPeriod());
		stodoitem.put("note", todo.getNote());
		stodoitem.put("status", todo.getStatus());
		stodoitem.put("itemid", id);

		arrays.put(stodoitem);

		todo.setId(String.valueOf(id));

		obj.put(_table_list, arrays);

		try {
			db.putJSON(_table, obj);
			status = true;
		} catch (Exception e) {
			status = false;
			e.printStackTrace();
		}

		return status;
	}

	public boolean removeItem(TodoItem todo) {

		JSONObject obj = null;
		try {
			obj = db.getJSON(_table);
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		if (obj == null) {
			obj = new JSONObject();
		}

		JSONArray arrays = null;
		if (!obj.has(_table_list)) {
			arrays = new JSONArray();
		} else {
			arrays = obj.getJSONArray(_table_list);
		}

		for (int i = 0; i < arrays.length(); i++) {
			JSONObject stodoitem = arrays.getJSONObject(i);
			if (stodoitem.has("itemid")
					&& stodoitem.getInt("itemid") == Integer.parseInt(todo
							.getId()))
				arrays.remove(i);
		}

		obj.put(_table_list, arrays);

		try {
			db.putJSON(_table, obj);
			status = true;
		} catch (Exception e) {
			status = false;
			e.printStackTrace();
		}

		return status;

	}

	public boolean updateItem(TodoItem todo) {

		JSONObject obj = null;
		try {
			obj = db.getJSON(_table);
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		if (obj == null) {
			obj = new JSONObject();
		}

		JSONArray arrays = null;
		if (!obj.has(_table_list)) {
			arrays = new JSONArray();
			obj.put(_table_list, arrays);
		} else {
			arrays = obj.getJSONArray(_table_list);
			for (int i = 0; i < arrays.length(); i++) {
				JSONObject stodoitem = arrays.getJSONObject(i);

				if (stodoitem.has("itemid")
						&& stodoitem.getInt("itemid") == Integer.parseInt(todo
								.getId())) {

					stodoitem.put("type", todo.getType());
					stodoitem.put("desc", todo.getDesc());
					stodoitem.put("timeout", todo.getTimeout());
					stodoitem.put("period", todo.getPeriod());
					stodoitem.put("note", todo.getNote());
					stodoitem.put("status", todo.getStatus());

				}
			}
		}

		try {
			db.putJSON(_table, obj);
			status = true;
		} catch (Exception e) {
			status = false;
			e.printStackTrace();
		}

		return status;

	}

	public List<TodoItem> getAllItems() {
		List<TodoItem> list = new LinkedList<TodoItem>();
		String date = null;
		JSONObject obj = null;
		try {
			obj = db.getJSON(_table);
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		if (obj == null) {
			return list;
		}
		JSONArray arrays = null;
		if (!obj.has(_table_list)) {
			arrays = new JSONArray();
		} else {
			arrays = obj.getJSONArray(_table_list);
		}

		for (int i = 0; i < arrays.length(); i++) {
			JSONObject o = arrays.getJSONObject(i);
			if (o.has("itemid")) {
				TodoItem node = new TodoItem();
				node.setId(String.valueOf(o.getInt("itemid")));
				node.setType(o.getString("type"));
				node.setDesc(o.getString("desc"));
				date = o.getString("timeout");
				node.setTimeout(date);
				node.setPeriod(o.getString("period"));
				node.setNote(o.getString("note"));
				node.setStatus(o.getString("status"));
				list.add(node);
			}
		}

		return list;
	}

	public String getMessage() {
		return message;
	}
}
