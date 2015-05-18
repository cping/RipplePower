package org.ripple.power.ui.projector.core;

import org.ripple.power.config.LSystem;
import org.ripple.power.ui.graphics.LColor;
import org.ripple.power.ui.graphics.LGraphics;
import org.ripple.power.ui.graphics.LImage;
import org.ripple.power.ui.projector.action.map.Config;
import org.ripple.power.ui.projector.action.sprite.effect.ArcEffect;
import org.ripple.power.ui.projector.action.sprite.effect.CrossEffect;
import org.ripple.power.ui.projector.action.sprite.effect.FadeEffect;
import org.ripple.power.ui.projector.action.sprite.effect.SplitEffect;
import org.ripple.power.utils.GraphicsUtils;

public class LTransition {

	/**
	 * 随机的百叶窗特效
	 * 
	 * @return
	 */
	public final static LTransition newCrossRandom() {
		return newCrossRandom(LColor.black);
	}

	/**
	 * 百叶窗特效
	 * 
	 * @param c
	 * @return
	 */
	public final static LTransition newCrossRandom(LColor c) {
		return newCross(LSystem.getRandomBetWeen(0, 1),
				GraphicsUtils.createLImage(LSystem.screenRect.width,
						LSystem.screenRect.height, c));
	}

	/**
	 * 百叶窗特效
	 * 
	 * @param c
	 * @return
	 */
	public final static LTransition newCross(final int c, final LImage texture) {

		final LTransition transition = new LTransition();

		transition.setTransitionListener(new TransitionListener() {

			final CrossEffect cross = new CrossEffect(c, texture);

			public void draw(LGraphics g) {
				cross.createUI(g);
			}

			public void update(long elapsedTime) {
				cross.update(elapsedTime);
			}

			public boolean completed() {
				return cross.isComplete();
			}

			public void dispose() {
				cross.dispose();
			}

		});
		transition.setDisplayGameUI(true);
		transition.code = 1;
		return transition;

	}

	/**
	 * 默认使用黑色的圆弧渐变特效
	 * 
	 * @return
	 */
	public final static LTransition newArc() {
		return newArc(LColor.black);
	}

	/**
	 * 单一色彩的圆弧渐变特效
	 * 
	 * @return
	 */
	public final static LTransition newArc(final LColor c) {

		final LTransition transition = new LTransition();

		transition.setTransitionListener(new TransitionListener() {

			final ArcEffect arc = new ArcEffect(c);

			public void draw(LGraphics g) {
				arc.createUI(g);
			}

			public void update(long elapsedTime) {
				arc.update(elapsedTime);
			}

			public boolean completed() {
				return arc.isComplete();
			}

			public void dispose() {
				arc.dispose();
			}

		});
		transition.setDisplayGameUI(true);
		transition.code = 1;
		return transition;

	}

	/**
	 * 产生一个Screen画面向双向分裂的过渡特效
	 * 
	 * @param texture
	 * @return
	 */
	public final static LTransition newSplitRandom(LImage texture) {
		return newSplit(LSystem.getRandomBetWeen(0, Config.TDOWN), texture);
	}

	/**
	 * 产生一个Screen画面向双向分裂的过渡特效
	 * 
	 * @param c
	 * @return
	 */
	public final static LTransition newSplitRandom(LColor c) {
		return newSplitRandom(GraphicsUtils.createLImage(
				LSystem.screenRect.width, LSystem.screenRect.height, c));
	}

	/**
	 * 产生一个Screen画面向双向分裂的过渡特效(方向的静态值位于Config类中)
	 * 
	 * @param d
	 * @param texture
	 * @return
	 */
	public final static LTransition newSplit(final int d, final LImage texture) {

		final LTransition transition = new LTransition();

		transition.setTransitionListener(new TransitionListener() {

			final SplitEffect split = new SplitEffect(texture, d);

			public void draw(LGraphics g) {
				split.createUI(g);
			}

			public void update(long elapsedTime) {
				split.update(elapsedTime);
			}

			public boolean completed() {
				return split.isComplete();
			}

			public void dispose() {
				split.dispose();
			}

		});
		transition.setDisplayGameUI(true);
		transition.code = 1;
		return transition;

	}

	/**
	 * 产生一个黑色的淡入效果
	 * 
	 * @return
	 */
	public final static LTransition newFadeIn() {
		return LTransition.newFade(FadeEffect.TYPE_FADE_IN);
	}

	/**
	 * 产生一个黑色的淡出效果
	 * 
	 * @return
	 */
	public final static LTransition newFadeOut() {
		return LTransition.newFade(FadeEffect.TYPE_FADE_OUT);
	}

	/**
	 * 产生一个黑色的淡入/淡出效果
	 * 
	 * @param type
	 * @return
	 */
	public final static LTransition newFade(int type) {
		return LTransition.newFade(type, LColor.black);
	}

	/**
	 * 产生一个指定色彩的淡入效果
	 * 
	 * @param c
	 * @return
	 */
	public final static LTransition newFade(final int type, final LColor c) {

		final LTransition transition = new LTransition();

		transition.setTransitionListener(new TransitionListener() {

			final FadeEffect fade = FadeEffect.getInstance(type, c);

			public void draw(LGraphics g) {
				fade.createUI(g);
			}

			public void update(long elapsedTime) {
				fade.update(elapsedTime);
			}

			public boolean completed() {
				return fade.isStop();
			}

			public void dispose() {
				fade.dispose();
			}

		});
		transition.setDisplayGameUI(true);
		transition.code = 1;
		return transition;

	}

	public final static LTransition newEmpty() {

		final LTransition transition = new LTransition();

		transition.setTransitionListener(new TransitionListener() {

			public void draw(LGraphics g) {
			}

			public void update(long elapsedTime) {
			}

			public boolean completed() {
				return true;
			}

			public void dispose() {
			}

		});

		transition.setDisplayGameUI(true);
		transition.code = 1;
		return transition;

	}

	public static interface TransitionListener {

		public void update(long elapsedTime);

		public void draw(LGraphics g);

		public boolean completed();

		public void dispose();
	}

	// 是否在在启动过渡效果同时显示游戏画面（即是否顶层绘制过渡画面，底层同时绘制标准游戏画面）
	boolean isDisplayGameUI;

	int code;

	TransitionListener listener;

	public void setDisplayGameUI(boolean s) {
		this.isDisplayGameUI = s;
	}

	public boolean isDisplayGameUI() {
		return this.isDisplayGameUI;
	}

	public void setTransitionListener(TransitionListener l) {
		this.listener = l;
	}

	public TransitionListener getTransitionListener() {
		return this.listener;
	}

	final void update(long elapsedTime) {
		if (listener != null) {
			listener.update(elapsedTime);
		}
	}

	final void draw(LGraphics g) {
		if (listener != null) {
			listener.draw(g);
		}
	}

	final boolean completed() {
		if (listener != null) {
			return listener.completed();
		}
		return false;
	}

	final void dispose() {
		if (listener != null) {
			listener.dispose();
		}
	}
}
