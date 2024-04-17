package com.example.testcreditcard.component;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentContainerView;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import java.util.ArrayList;

public class TabLayoutComponent extends LinearLayout implements ViewTreeObserver.OnGlobalLayoutListener {
    private LinearLayout _mainViewGroup;
    private HorizontalScrollView _tabScroller;
    private LinearLayout  _tabViewGroup;

    private ArrayList<TabComponentView> _tabViewList = new ArrayList<TabComponentView>() ;
    private FragmentContainerView _contentContainer;
    private FragmentManager _fragementManager;
    private Context _context;

    @SuppressLint("ResourceType")
    private void Init(Context context, FragmentManager fragmentManager){
        getViewTreeObserver().addOnGlobalLayoutListener(this);
        _context = context;
        _fragementManager = fragmentManager;
        //mainViewGroup is Container for tabScroller and contentContainer
        _mainViewGroup = new LinearLayout(context);
        _mainViewGroup.setOrientation(LinearLayout.VERTICAL);

        //tabScroller is Container for tabViewGroup
        _tabScroller = new HorizontalScrollView(context);
        _tabScroller.setHorizontalScrollBarEnabled(false);
        _tabViewGroup = new LinearLayout(context);
        _tabScroller.addView(_tabViewGroup);

        //contentContainer is Container for Fragment
        _contentContainer = new FragmentContainerView(context);
        _contentContainer.setId(987987987);
        _contentContainer.setBackgroundColor(_focusColor);

        _mainViewGroup.addView(_tabScroller);
        _mainViewGroup.addView(_contentContainer);
        this.addView(_mainViewGroup);
    }


    @Override
    public void onGlobalLayout() {
        _contentContainer.setMinimumWidth(getWidth());
        _contentContainer.setMinimumHeight(getHeight()- _tabScroller.getHeight());
    }

    public TabLayoutComponent(Context context,FragmentManager fragmentManager) {
        super(context);
        Init(context,fragmentManager);
    }
    public TabLayoutComponent(Context context, AttributeSet attrs,FragmentManager fragmentManager) {
        super(context,attrs);
        Init(context,fragmentManager);
    }
    public TabLayoutComponent(Context context,AttributeSet attrs,int defStyleAttr,int defStyleRes,FragmentManager fragmentManager) {
        super(context,attrs,defStyleAttr,defStyleRes);
        Init(context,fragmentManager);
    }
    public void addTab(TabComponent tabComponent){
        TabComponentView NewButton = new TabComponentView(_context,tabComponent._fragment);
        NewButton.setUnFocusColor(_unFocusColor);
        NewButton.setFocusColor(_focusColor);
        NewButton.setText(tabComponent.TabText);
        NewButton.setRadius(10);
        NewButton.setIsFocus(false);
        FragmentTransaction fragmentTransaction = _fragementManager.beginTransaction();

        fragmentTransaction.add(_contentContainer.getId(),tabComponent._fragment);
        fragmentTransaction.hide(tabComponent._fragment);
        NewButton.setY(18);
        if(_tabViewList.isEmpty()){
            NewButton.setIsFocus(true);
            fragmentTransaction.show(tabComponent._fragment);
        }
        fragmentTransaction.commit();
        NewButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                TabComponentView t =(TabComponentView) v;
                FragmentTransaction fragmentTransaction = _fragementManager.beginTransaction();
                for (TabComponentView tabView:_tabViewList) {
                    tabView.setIsFocus(false);
                    fragmentTransaction.hide(tabView._fragment);
                }
                t.setIsFocus(true);
                fragmentTransaction.show(t._fragment);
                fragmentTransaction.commit();
            }
        });
        _tabViewList.add(NewButton);
        _tabViewGroup.addView(NewButton);
    }


    private int _unFocusColor = Color.WHITE;
    private int getUnFocusColor(){return _unFocusColor;}
    public void setUnFocusColor(int value) {
        _unFocusColor = value;
        postInvalidate();
    }

    private int _focusColor = Color.GRAY;
    public int getFocusColor(){return _focusColor;}
    public void setFocusColor(int value) {
        _focusColor = value;
        _contentContainer.setBackgroundColor(_focusColor);
        postInvalidate();
    }

    private class TabComponentView extends androidx.appcompat.widget.AppCompatButton{
        private RoundedRectDrawable _drawable;
        private int _focusColor = Color.GRAY;
        private int _unFocusColor = Color.WHITE;
        private boolean _isFocus = false;
        public Fragment _fragment;

        public int getFocusColor(){return _focusColor;}
        public void setFocusColor(int value){
            _focusColor = value;
            postInvalidate();
        }

        private int getUnFocusColor(){return _unFocusColor;}
        public void setUnFocusColor(int value){
            _unFocusColor = value;
            postInvalidate();
        }

        public void setRadius(int value) {
            _drawable.setRadius(value);
            postInvalidate();
        }

        public boolean getIsFocus(){return _isFocus;}
        public void setIsFocus(boolean value) {
            _isFocus = value;
            if(_isFocus)
            {
                _drawable.setColor(getFocusColor());
            }
            else
            {
                _drawable.setColor(getUnFocusColor());
            }
            postInvalidate();
        }

        public TabComponentView(Context context,Fragment fragment) {
            super(context);
            _drawable = new RoundedRectDrawable(_unFocusColor, 0);
            _fragment =fragment;
        }
        public TabComponentView(Context context, AttributeSet attrs,Fragment fragment) {
            super(context, attrs);
            _drawable = new RoundedRectDrawable(_unFocusColor, 0);
            _fragment =fragment;
        }
        public TabComponentView(Context context, AttributeSet attrs, int defStyleAttr,Fragment fragment) {
            super(context, attrs, defStyleAttr);
            _drawable = new RoundedRectDrawable(_unFocusColor, 0);
            _fragment =fragment;
        }

        @Override
        protected void onDraw(Canvas canvas) {
            _drawable.setBounds(0, 0, getWidth(), getHeight());
            _drawable.draw(canvas);
            super.onDraw(canvas);
        }

    }

    private class RoundedRectDrawable extends Drawable {
        private final Paint mPaint;
        private int mRadius;

        public RoundedRectDrawable(int color, int radius) {
            mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            mPaint.setColor(color);
            mRadius = radius;
        }
        @Override
        public void draw(Canvas canvas) {
            RectF rect = new RectF(getBounds());
            canvas.drawRoundRect(rect, mRadius, mRadius, mPaint);
        }
        @Override
        public void setAlpha(int alpha) {
            mPaint.setAlpha(alpha);
        }
        public void setColor(int color){
            mPaint.setColor(color);
        }
        public void setRadius(int value){
            mRadius = value;
        }
        @Override
        public void setColorFilter(ColorFilter colorFilter) {
            mPaint.setColorFilter(colorFilter);
        }
        @Override
        public int getOpacity() {
            return PixelFormat.TRANSLUCENT;
        }
    }
}

