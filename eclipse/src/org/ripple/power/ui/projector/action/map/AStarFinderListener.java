package org.ripple.power.ui.projector.action.map;

import java.util.LinkedList;

import org.ripple.power.ui.graphics.geom.Vector2D;

public interface AStarFinderListener {

	void pathFound(LinkedList<Vector2D> path);

}
