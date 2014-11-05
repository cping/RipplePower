package org.ripple.power.sound;

import java.io.IOException;
import java.io.InputStream;

import org.ripple.power.ui.UIRes;


import mediaframe.mpeg4.audio.MP3AudioPlayer;

public class LMP3Sound implements Sound {

	private int volume;

	private MP3AudioPlayer player;

	public LMP3Sound() {
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
			player = new MP3AudioPlayer(in);
			setSoundVolume(volume);
			player.play();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void setSoundVolume(int volume) {
		this.volume = volume;
		if (this.player == null) {
			return;
		}
		player.setVolume(volume);

	}

	public void stopSound() {
		if (this.player == null) {
			return;
		}
		player.stop();

	}

	public boolean isVolumeSupported() {
		return true;
	}

}
