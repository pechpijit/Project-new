package com.comsci.project;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RectShape;
import android.util.AttributeSet;
import android.view.View;


public final class BarLevelDrawable extends View {
  private ShapeDrawable mDrawable;
  private double mLevel = 3.0 ;

  final int[] segmentColors = {
      0x00000000,
      0x00000000,
      0x00000000,
      0x00000000,
      0x00000000,
      0x00000000,
      0x00000000,
      0x00000000,
      0x00000000,
      0x00000000};
  final int segmentOffColor = 0x0000000;

  public BarLevelDrawable(Context context, AttributeSet attrs) {
    super(context, attrs);
    initBarLevelDrawable();
  }

  public BarLevelDrawable(Context context) {
    super(context);
    initBarLevelDrawable();
  }


  public void setLevel(double level) {
    mLevel = level;
    invalidate();
  }

  public double getLevel() {
    return mLevel;
  }

  private void initBarLevelDrawable() {
    mLevel = 0.1;
  }

  private void drawBar(Canvas canvas) {
    int padding = 5; // Padding on both sides.
    int x = 1;
    int y = 10;

    int width = (int) (Math.floor(getWidth() / segmentColors.length))
        - (2 * padding);
    int height = 50;

    mDrawable = new ShapeDrawable(new RectShape());
    for (int i = 0; i < segmentColors.length; i++) {
      x = x + padding;
      if ((mLevel * segmentColors.length) > (i + 0.5)) {
        mDrawable.getPaint().setColor(segmentColors[i]);
      } else {
        mDrawable.getPaint().setColor(segmentOffColor);
      }
      mDrawable.setBounds(x, y, x + width, y + height);
      mDrawable.draw(canvas);
      x = x + width + padding;
    }
  }

  @Override
  protected void onDraw(Canvas canvas) {
    drawBar(canvas);
  }
}
