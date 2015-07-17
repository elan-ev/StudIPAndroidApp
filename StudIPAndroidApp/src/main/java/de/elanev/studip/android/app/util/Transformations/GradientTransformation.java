/*
 * Copyright (c) 2015 ELAN e.V.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 */

package de.elanev.studip.android.app.util.Transformations;


import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Shader;

import com.squareup.picasso.Transformation;

/**
 * Created by Miao1007 on 2/2/15.
 *
 * Original Code: https://gist.github.com/miao1007/3f7e9b5f011fc188eba6
 */
public class GradientTransformation implements Transformation {

  int startColor = Color.BLACK;
  int endColor = Color.TRANSPARENT;

  @Override public Bitmap transform(Bitmap source) {

    int x = source.getWidth();
    int y = source.getHeight();

    Bitmap grandientBitmap = source.copy(source.getConfig(), true);
    Canvas canvas = new Canvas(grandientBitmap);
    //left-top == (0,0) , right-bottom(x,y);
    LinearGradient grad =
        new LinearGradient(x/2, y, x/2, y/2, startColor, endColor, Shader.TileMode.CLAMP);
    Paint p = new Paint(Paint.DITHER_FLAG);
    p.setShader(null);
    p.setDither(true);
    p.setFilterBitmap(true);
    p.setShader(grad);
    canvas.drawPaint(p);
    source.recycle();
    return grandientBitmap;
  }

  @Override public String key() {
    return "Gradient";
  }
}

