package org.ripple.power.ui;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JButton;

import org.ripple.power.sound.LWaveSound;

public class SoundButtonListener extends MouseAdapter {

	private final static LWaveSound wave = new LWaveSound();

	JButton button;

	int mode = -1;

	public SoundButtonListener(JButton button, int mode) {
		this.button = button;
		this.mode = mode;
	}

	public SoundButtonListener(JButton button) {
		this.button = button;
	}

	public void mouseEntered(MouseEvent e) {
	}

	public void mouseExited(MouseEvent e) {
		wave.stopSound();
	}

	public void mouseClicked(MouseEvent e) {
		wave.playSound("click.wav");
	}
}
