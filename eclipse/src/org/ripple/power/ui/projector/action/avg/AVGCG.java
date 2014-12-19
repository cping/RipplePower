package org.ripple.power.ui.projector.action.avg;

import org.ripple.power.collection.ArrayMap;
import org.ripple.power.ui.graphics.LImage;

public class AVGCG {

	private LImage background;

	private ArrayMap charas;

	public AVGCG() {
		charas = new ArrayMap(100);
	}

	public LImage getBackgroundCG() {
		return background;
	}

	public void noneBackgroundCG() {
		if (background != null) {
			background.dispose();
			background = null;
		}
	}

	public void setBackgroundCG(LImage backgroundCG) {
		if (backgroundCG == this.background) {
			return;
		}
		if (background != null) {
			background.dispose();
			background = null;
		}
		this.background = backgroundCG;
	}

	public void setBackgroundCG(String resName) {
		this.setBackgroundCG(new LImage(resName));
	}

	public void addChara(String file, AVGChara role) {
		charas.put(file.replaceAll(" ", "").toLowerCase(), role);
	}

	public void addImage(String name, int x, int y, int w) {
		String keyName = name.replaceAll(" ", "").toLowerCase();
		AVGChara chara = (AVGChara) charas.get(keyName);
		if (chara == null) {
			charas.put(keyName, new AVGChara(name, x, y, w));
		} else {
			chara.setX(x);
			chara.setY(y);
		}
	}

	public AVGChara removeImage(String file) {
		return (AVGChara) charas.remove(file.replaceAll(" ", "").toLowerCase());
	}

	public void dispose() {
		for (int i = 0; i < charas.size(); i++) {
			AVGChara ch = (AVGChara) charas.get(i);
			if (ch != null) {
				ch.dispose();
				ch = null;
			}
		}
		charas.clear();
		System.gc();
	}

	public void clear() {
		charas.clear();
	}

	public ArrayMap getCharas() {
		return charas;
	}

	public int count() {
		if (charas != null) {
			return charas.size();
		}
		return 0;
	}

}
