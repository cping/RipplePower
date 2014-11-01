/**
 * Copyright 2008 - 2012
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 * 
 * @project loon
 * @author cping
 * @email：javachenpeng@yahoo.com
 * @version 0.3.3
 */
package org.ripple.power.sound;

import java.util.HashMap;

import org.ripple.power.ui.UIRes;
import org.ripple.power.utils.CollectionUtils;
import org.ripple.power.utils.FileUtils;


public abstract class SoundBox {

	protected static final String[] SUFFIXES = { ".wav", ".mp3" };

	private static Audio _audio;

	public static Sound getSound(String path) {
		return getSound(path, false);
	}

	public static Sound getMusic(String path) {
		return getSound(path, true);
	}

	protected static Sound getSound(String path, boolean music) {
		if (_audio == null) {
			_audio = new Audio();
		}
		Exception err = null;
		String ext = FileUtils.getExtension(path);
		if (ext == null || ext.length() == 0) {
			for (String suff : SUFFIXES) {
				final String soundPath = path + suff;
				try {
					return _audio.createSound(UIRes.getStream(soundPath), music);
				} catch (Exception e) {
					e.printStackTrace();
					err = e;
				}
			}
		} else {
			try {
				return _audio.createSound(UIRes.getStream(path), music);
			} catch (Exception e) {
				e.printStackTrace();
				err = e;
			}
		}
		return new Sound.Error(err);
	}


	private HashMap<String, Sound> sounds = new HashMap<String, Sound>(
			CollectionUtils.INITIAL_CAPACITY);

	public void playSound(String path) {
		playSound(path, false);
	}

	public void playSound(String path, boolean loop) {
		Sound sound = sounds.get(path);
		if (sound == null) {
			sound = getSound(path);
			sounds.put(path, sound);
		} else {
			sound.stop();
		}
		sound.setLooping(loop);
		sound.play();
	}

	public void volume(String path, float volume) {
		Sound sound = sounds.get(path);
		if (sound != null) {
			sound.setVolume(volume);
		}
	}

	public void stopSound(String path) {
		Sound sound = sounds.get(path);
		if (sound != null) {
			sound.stop();
		}
	}

	public void stopSound() {
		for (Sound s : sounds.values()) {
			if (s != null) {
				s.stop();
			}
		}
	}

	public void release() {
		for (Sound s : sounds.values()) {
			if (s != null) {
				s.release();
			}
		}
		sounds.clear();
	}
}
