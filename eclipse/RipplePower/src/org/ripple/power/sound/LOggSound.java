package org.ripple.power.sound;

import java.io.IOException;
import java.io.InputStream;

import org.ripple.power.config.LSystem;
import org.ripple.power.ui.UIRes;

public class LOggSound implements Sound {

	private int volume;

	private JoggStreamer player;

	public LOggSound() {
		setSoundVolume(Sound.defaultMaxVolume);
	}

	public void playSound(String fileName) {
		try {
			playSound(UIRes.getStream(fileName));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void playSound(InputStream in) {
		try {
			stopSound();
			player = new JoggStreamer(in);
			try {
				synchronized (player) {
					setSoundVolume(volume);
					player.start();
					player.wait(LSystem.SECOND);
				}
			} catch (InterruptedException e) {
				throw new IOException("interrupted: " + e);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void setSoundVolume(int volume) {
		this.volume = volume;
		if (this.player == null) {
			return;
		}
		player.updateVolume(volume);

	}

	public void stopSound() {
		if (this.player == null) {
			return;
		}
		player.interrupt();

	}

	public boolean isVolumeSupported() {
		return true;
	}

}
