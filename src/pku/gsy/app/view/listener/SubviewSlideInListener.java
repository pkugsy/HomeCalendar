package pku.gsy.app.view.listener;

import android.view.View;

public interface SubviewSlideInListener {
	public static final int DirectionHere = 0;
	public static final int DirectionLeft = -1;
	public static final int DirectionRight = 1;
	public static final int DirectionUp = -2;
	public static final int DirectionDown = 2;
	
	public void onSlide(View which, int direction);
}
