/*
 * Copyright (C) 2008 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.android.systemui.statusbar;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.accessibility.AccessibilityEvent;
import android.widget.FrameLayout;
import android.graphics.Canvas;
import android.util.Log;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.StateListDrawable;
import android.graphics.Bitmap.Config;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.util.StateSet;
import android.graphics.Rect;
import android.graphics.Bitmap;

public class LatestItemView extends FrameLayout {

    StateListDrawable bgdrawable = null;

    public LatestItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void setOnClickListener(OnClickListener l) {
        super.setOnClickListener(l);
    }

    @Override
    public boolean onRequestSendAccessibilityEvent(View child, AccessibilityEvent event) {
        if (super.onRequestSendAccessibilityEvent(child, event)) {
            // Add a record for the entire layout since its content is somehow small.
            // The event comes from a leaf view that is interacted with.
            AccessibilityEvent record = AccessibilityEvent.obtain();
            onInitializeAccessibilityEvent(record);
            dispatchPopulateAccessibilityEvent(record);
            event.appendRecord(record);
            return true;
        }
        return false;
    }

    @Override
    protected void onSizeChanged (int w, int h, int oldw, int oldh){
        super.onSizeChanged(w, h, oldw, oldh);

        int minHeight = getContext().getResources().getDimensionPixelSize(com.android.systemui.R.dimen.notification_min_height);

        if(bgdrawable != null){  // Theme files exists
            if(getHeight() > minHeight){ // For large notification

                // create large images which contain bgdrawable
                StateListDrawable stateListDrawable = new StateListDrawable();
                Drawable drawablePressed=null;
                Drawable drawableNormal=null;

                Canvas c;
                bgdrawable.selectDrawable(0);
                Bitmap bmp =((BitmapDrawable)bgdrawable.getCurrent()).getBitmap();
                Rect rimg = new Rect(0, 0, bmp.getWidth(), bmp.getHeight());
                Rect r = new Rect(0, 0, getWidth(), minHeight);
                Paint p = new Paint();
                p.setStyle(Paint.Style.FILL_AND_STROKE);

                Bitmap bmpPressed = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_8888);
                c = new Canvas(bmpPressed);
                p.setColor(Color.argb(0x80,0x26,0x67,0x7f)); // translucent blue
                c.drawRect(0,0,getWidth(),getHeight(),p);
                c.drawBitmap(bmp, rimg, r, null);
                drawablePressed=new BitmapDrawable(bmpPressed);

                Bitmap bmpNormal = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_8888);
                c = new Canvas(bmpNormal);
                p.setColor(Color.argb(0x80,0x18,0x18,0x18)); // translucent black
                c.drawRect(0,0,getWidth(),getHeight(),p);
                bgdrawable.selectDrawable(1);
                c.drawBitmap(((BitmapDrawable)bgdrawable.getCurrent()).getBitmap(), rimg, r, null);
                drawableNormal=new BitmapDrawable(bmpNormal);

                stateListDrawable.addState(new int[] { android.R.attr.state_pressed }, drawablePressed);
                stateListDrawable.addState(StateSet.WILD_CARD, drawableNormal);

                setBackground(stateListDrawable);

            }else{ // For normal notification
                setBackground(bgdrawable);
            }
        }
    }

    public void setDrawable (Drawable d){
        bgdrawable = (StateListDrawable)d;
    }

}
