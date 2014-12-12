package org.ripple.power.server;
import java.util.ArrayList;
import java.util.List;

import org.ripple.power.utils.StringUtils;

public class LinkList {
	Node root;
	Node pointer;
	int count;


	public LinkList() {
		root = new Node();
		root.next = null;
		pointer = null;
		count = 0;
	}

	public void addUser(Node n) {
		pointer = root;

		while (pointer.next != null) {
			pointer = pointer.next;
		}

		pointer.next = n;
		n.next = null;
		count++;

	}
	
	public void delUser(Node n) {
		pointer = root;

		while (pointer.next != null) {
			if (pointer.next == n) {
				pointer.next = n.next;
				count--;

				break;
			}

			pointer = pointer.next;
		}
	}

	public int getCount() {
		return count;
	}

	public Node findUser(String username) {
		if (count == 0){
			return null;
		}

		pointer = root;

		while (pointer.next != null) {
			pointer = pointer.next;

			if (pointer.username.equalsIgnoreCase(username)) {
				return pointer;
			}
		}

		return null;
	}


	public Node findUser(int index) {
		if (count == 0) {
			return null;
		}

		if (index < 0) {
			return null;
		}

		pointer = root;

		int i = 0;
		while (i < index + 1) {
			if (pointer.next != null) {
				pointer = pointer.next;
			} else {
				return null;
			}

			i++;
		}
		return pointer;
	}

	public List<String> users() {
		List<String> list = new ArrayList<String>();
		pointer = root;
		while (pointer.next != null) {
			pointer = pointer.next;
			if (!StringUtils.isEmpty(pointer.username)) {
				list.add(pointer.username);
			}
		}
		list.add("all");
		return list;
	}
}
