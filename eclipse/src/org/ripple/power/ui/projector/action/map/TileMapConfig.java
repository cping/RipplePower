package org.ripple.power.ui.projector.action.map;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.ripple.power.ui.graphics.LImage;
import org.ripple.power.ui.projector.Resources;

public class TileMapConfig {

	private int[][] backMap;

	public int[][] getBackMap() {
		return backMap;
	}

	public void setBackMap(int[][] backMap) {
		this.backMap = backMap;
	}

	public static List<int[]> loadList(final String fileName)
			throws IOException {
		InputStream in = Resources.openResource(fileName);
		BufferedReader reader = new BufferedReader(new InputStreamReader(in));
		List<int[]> records = new ArrayList<int[]>(10);
		String result = null;
		try {
			while ((result = reader.readLine()) != null) {
				if (!"".equals(result)) {
					String[] stringArray = result.split(",");
					int size = stringArray.length;
					int[] intArray = new int[size];
					for (int i = 0; i < size; i++) {
						intArray[i] = Integer.parseInt(stringArray[i]);
					}
					records.add(intArray);
				}
			}
		} finally {
			if (reader != null) {
				try {
					reader.close();
					reader = null;
				} catch (IOException ex) {
				}
			}
		}
		return records;
	}

	public static LImage[][] reversalXandY(final LImage[][] array) {
		int col = array[0].length;
		int row = array.length;
		LImage[][] result = new LImage[col][row];
		for (int y = 0; y < col; y++) {
			for (int x = 0; x < row; x++) {
				result[x][y] = array[y][x];
			}
		}
		return result;
	}

	public static int[][] reversalXandY(final int[][] array) {
		int col = array[0].length;
		int row = array.length;
		int[][] result = new int[col][row];
		for (int y = 0; y < col; y++) {
			for (int x = 0; x < row; x++) {
				result[x][y] = array[y][x];
			}
		}
		return result;
	}

	public static int[][] loadAthwartArray(final String fileName)
			throws IOException {
		List<?> list = loadList(fileName);
		int col = list.size();
		int[][] result = new int[col][];
		for (int i = 0; i < col; i++) {
			result[i] = (int[]) list.get(i);
		}
		return result;
	}

	public static int[][] loadJustArray(final String fileName)
			throws IOException {
		List<?> list = loadList(fileName);
		int col = list.size();
		int[][] mapArray = new int[col][];
		for (int i = 0; i < col; i++) {
			mapArray[i] = (int[]) list.get(i);
		}
		int row = (((int[]) mapArray[col > 0 ? col - 1 : 0]).length);
		int[][] result = new int[row][col];
		for (int y = 0; y < col; y++) {
			for (int x = 0; x < row; x++) {
				result[x][y] = mapArray[y][x];
			}
		}
		return result;
	}

}
