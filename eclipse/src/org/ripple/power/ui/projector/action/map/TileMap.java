package org.ripple.power.ui.projector.action.map;

import org.ripple.power.ui.graphics.LImage;
import org.ripple.power.ui.graphics.geom.Vector2D;

public class TileMap {

	private LImage[] tiles;

	private int tileWidth;

	private int tileHeight;

	private int width, height;

	private Vector2D offset;

	/**
	 * 构造一个瓦片地图（默认大小32*32）
	 * 
	 * @param width
	 * @param height
	 */
	public TileMap(int width, int height) {
		this(width, height, 32, 32);
	}

	/**
	 * 构造一个瓦片地图，并指定宽x高
	 * 
	 * @param width
	 * @param height
	 * @param tileWidth
	 * @param tileHeight
	 */
	public TileMap(int width, int height, int tileWidth, int tileHeight) {
		this.tileWidth = tileWidth;
		this.tileHeight = tileHeight;
		this.width = width;
		this.height = height;
		tiles = new LImage[width * height];
		offset = new Vector2D(0, 0);
	}

	public LImage getTile(int x, int y) {
		return tiles[x + width * y];
	}

	public void setTile(int x, int y, LImage img) {
		tiles[x + width * y] = img;
	}

	public LImage getTileFromPixels(double x, double y) {
		return getTileFromPixels(new Vector2D(x, y));
	}

	public LImage getTileFromPixels(Vector2D p) {
		double x = (p.getX() + offset.getX());
		double y = (p.getY() + offset.getY());
		Vector2D tileCoordinates = pixelsToTiles(x, y);
		return getTile((int) Math.round(tileCoordinates.getX()), (int) Math
				.round(tileCoordinates.getY()));
	}

	public Vector2D pixelsToTiles(double x, double y) {
		double xprime = x / tileWidth - 1;
		double yprime = y / tileHeight - 1;
		return new Vector2D(xprime, yprime);
	}

	/**
	 * 转换坐标为像素坐标
	 * 
	 * @param x
	 * @param y
	 * @return
	 */
	public Vector2D tilesToPixels(double x, double y) {
		double xprime = x * tileWidth - offset.getX();
		double yprime = y * tileHeight - offset.getY();
		return new Vector2D(xprime, yprime);
	}

	/**
	 * 获得矫正后的碰撞位置
	 * 
	 * @param p
	 * @param width
	 * @param height
	 * @return
	 */
	public Vector2D getCollision(Vector2D p, double width, double height) {
		LImage tile1 = getTileFromPixels(p.getX(), p.getY());
		LImage tile2 = getTileFromPixels(p.getX(), p.getY() + height);
		LImage tile3 = getTileFromPixels(p.getX() + width, p.getY());
		float x, y;
		x = y = 0;
		if (tile1 != null) {
			x = -1;
		} else if (tile3 != null) {
			x = 1;
		}
		if (tile2 != null) {
			y = 1;
		} else if (tile1 != null) {
			y = -1;
		}
		return new Vector2D(x, y);
	}

	/**
	 * 设置瓦片位置
	 * 
	 * @param x
	 * @param y
	 */
	public void setOffset(float x, float y) {
		this.offset.setX(x);
		this.offset.setY(y);
	}

	/**
	 * 设定偏移量
	 * 
	 * @param offset
	 */
	public void setOffset(Vector2D offset) {
		this.offset.setX(offset.getX());
		this.offset.setY(offset.getY());
	}

	/**
	 * 获得瓦片位置
	 * 
	 * @return
	 */
	public Vector2D getOffset() {
		return offset;
	}

	public int getHeight() {
		return height;
	}

	public int getWidth() {
		return width;
	}

}
