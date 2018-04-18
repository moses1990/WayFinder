package com.sanginfo.intripper.libraryproject.controls;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RadialGradient;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.util.Log;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;

import com.sanginfo.intripper.libraryproject.R;

public class ShimmerFrameLayout extends FrameLayout
{
      /**
       * The Constant TAG.
       */
      private static final String TAG = "il_ShimmerFrameLayout";

      /**
       * The Constant DST_IN_PORTER_DUFF_XFERMODE.
       */
      private static final PorterDuffXfermode DST_IN_PORTER_DUFF_XFERMODE = new PorterDuffXfermode( PorterDuff.Mode.DST_IN );

      /**
       * The m animator.
       */
      protected ValueAnimator mAnimator;

      /**
       * The m mask bitmap.
       */
      protected Bitmap mMaskBitmap;

      /**
       * The m alpha paint.
       */
      private Paint mAlphaPaint;

      /**
       * The m mask paint.
       */
      private Paint mMaskPaint;

      /**
       * The m mask.
       */
      private Mask mMask;

      /**
       * The m mask translation.
       */
      private MaskTranslation mMaskTranslation;

      /**
       * The m render mask bitmap.
       */
      private Bitmap mRenderMaskBitmap;

      /**
       * The m render unmask bitmap.
       */
      private Bitmap mRenderUnmaskBitmap;

      /**
       * The m auto start.
       */
      private boolean mAutoStart;

      /**
       * The m il_duration.
       */
      private int mDuration;

      /**
       * The m repeat count.
       */
      private int mRepeatCount;

      /**
       * The m repeat delay.
       */
      private int mRepeatDelay;

      /**
       * The m repeat mode.
       */
      private int mRepeatMode;

      /**
       * The m mask offset x.
       */
      private int mMaskOffsetX;

      /**
       * The m mask offset y.
       */
      private int mMaskOffsetY;

      /**
       * The m animation started.
       */
      private boolean mAnimationStarted;

      /**
       * The m global layout listener.
       */
      private ViewTreeObserver.OnGlobalLayoutListener mGlobalLayoutListener;

      /**
       * Instantiates a new shimmer frame layout.
       *
       * @param context
       *             the si_in_lb__context
       */
      public ShimmerFrameLayout( Context context )
      {
            this( context, null, 0 );
      }

      /**
       * Instantiates a new shimmer frame layout.
       *
       * @param context
       *             the si_in_lb__context
       * @param attrs
       *             the attrs
       */
      public ShimmerFrameLayout( Context context, AttributeSet attrs )
      {
            this( context, attrs, 0 );
      }

      /**
       * Instantiates a new shimmer frame layout.
       *
       * @param context
       *             the si_in_lb__context
       * @param attrs
       *             the attrs
       * @param defStyle
       *             the def style
       */
      public ShimmerFrameLayout( Context context, AttributeSet attrs, int defStyle )
      {
            super( context, attrs, defStyle );
            setWillNotDraw( false );
            mMask = new Mask();
            mAlphaPaint = new Paint();
            mMaskPaint = new Paint();
            mMaskPaint.setAntiAlias( true );
            mMaskPaint.setDither( true );
            mMaskPaint.setFilterBitmap( true );
            mMaskPaint.setXfermode( DST_IN_PORTER_DUFF_XFERMODE );
            useDefaults();
            if ( attrs != null )
            {
                  TypedArray a = context.obtainStyledAttributes( attrs, R.styleable.il_ShimmerFrameLayout, 0, 0 );
                  try
                  {
                        if ( a.hasValue( R.styleable.il_ShimmerFrameLayout_il_auto_start ) )
                        {
                              setAutoStart( a.getBoolean( R.styleable.il_ShimmerFrameLayout_il_auto_start, false ) );
                        }
                        if ( a.hasValue( R.styleable.il_ShimmerFrameLayout_il_base_alpha ) )
                        {
                              setBaseAlpha( a.getFloat( R.styleable.il_ShimmerFrameLayout_il_base_alpha, 0 ) );
                        }
                        if ( a.hasValue( R.styleable.il_ShimmerFrameLayout_il_duration ) )
                        {
                              setDuration( a.getInt( R.styleable.il_ShimmerFrameLayout_il_duration, 0 ) );
                        }
                        if ( a.hasValue( R.styleable.il_ShimmerFrameLayout_il_repeat_count ) )
                        {
                              setRepeatCount( a.getInt( R.styleable.il_ShimmerFrameLayout_il_repeat_count, 0 ) );
                        }
                        if ( a.hasValue( R.styleable.il_ShimmerFrameLayout_il_repeat_delay ) )
                        {
                              setRepeatDelay( a.getInt( R.styleable.il_ShimmerFrameLayout_il_repeat_delay, 0 ) );
                        }
                        if ( a.hasValue( R.styleable.il_ShimmerFrameLayout_il_repeat_mode ) )
                        {
                              setRepeatMode( a.getInt( R.styleable.il_ShimmerFrameLayout_il_repeat_mode, 0 ) );
                        }
                        if ( a.hasValue( R.styleable.il_ShimmerFrameLayout_il_angle ) )
                        {
                              int angle = a.getInt( R.styleable.il_ShimmerFrameLayout_il_angle, 0 );
                              switch ( angle )
                              {
                                    default:
                                    case 0:
                                          mMask.angle = MaskAngle.CW_0;
                                          break;
                                    case 90:
                                          mMask.angle = MaskAngle.CW_90;
                                          break;
                                    case 180:
                                          mMask.angle = MaskAngle.CW_180;
                                          break;
                                    case 270:
                                          mMask.angle = MaskAngle.CW_270;
                                          break;
                              }
                        }
                        if ( a.hasValue( R.styleable.il_ShimmerFrameLayout_il_shape ) )
                        {
                              int shape = a.getInt( R.styleable.il_ShimmerFrameLayout_il_shape, 0 );
                              switch ( shape )
                              {
                                    default:
                                    case 0:
                                          mMask.shape = MaskShape.LINEAR;
                                          break;
                                    case 1:
                                          mMask.shape = MaskShape.RADIAL;
                                          break;
                              }
                        }
                        if ( a.hasValue( R.styleable.il_ShimmerFrameLayout_il_dropoff ) )
                        {
                              mMask.dropoff = a.getFloat( R.styleable.il_ShimmerFrameLayout_il_dropoff, 0 );
                        }
                        if ( a.hasValue( R.styleable.il_ShimmerFrameLayout_il_fixed_width ) )
                        {
                              mMask.fixedWidth = a.getDimensionPixelSize( R.styleable.il_ShimmerFrameLayout_il_fixed_width, 0 );
                        }
                        if ( a.hasValue( R.styleable.il_ShimmerFrameLayout_il_fixed_height ) )
                        {
                              mMask.fixedHeight = a.getDimensionPixelSize( R.styleable.il_ShimmerFrameLayout_il_fixed_height, 0 );
                        }
                        if ( a.hasValue( R.styleable.il_ShimmerFrameLayout_il_intensity ) )
                        {
                              mMask.intensity = a.getFloat( R.styleable.il_ShimmerFrameLayout_il_intensity, 0 );
                        }
                        if ( a.hasValue( R.styleable.il_ShimmerFrameLayout_il_relative_width ) )
                        {
                              mMask.relativeWidth = a.getFloat( R.styleable.il_ShimmerFrameLayout_il_relative_width, 0 );
                        }
                        if ( a.hasValue( R.styleable.il_ShimmerFrameLayout_il_relative_height ) )
                        {
                              mMask.relativeHeight = a.getFloat( R.styleable.il_ShimmerFrameLayout_il_relative_height, 0 );
                        }
                        if ( a.hasValue( R.styleable.il_ShimmerFrameLayout_il_tilt ) )
                        {
                              mMask.tilt = a.getFloat( R.styleable.il_ShimmerFrameLayout_il_tilt, 0 );
                        }
                  }
                  finally
                  {
                        a.recycle();
                  }
            }
      }

      /**
       * Clamp.
       *
       * @param min
       *             the min
       * @param max
       *             the max
       * @param value
       *             the value
       *
       * @return the float
       */
      private static float clamp( float min, float max, float value )
      {
            return Math.min( max, Math.max( min, value ) );
      }

      /**
       * Creates a bitmap with the given width and height.
       * <p/>
       * If it fails with an OutOfMemory error, it will force a GC and then try to create the bitmap
       * one more time.
       *
       * @param width
       *             width of the bitmap
       * @param height
       *             height of the bitmap
       *
       * @return the bitmap
       */
      protected static Bitmap createBitmapAndGcIfNecessary( int width, int height )
      {
            try
            {
                  return Bitmap.createBitmap( width, height, Bitmap.Config.ARGB_8888 );
            }
            catch ( OutOfMemoryError e )
            {
                  System.gc();
                  return Bitmap.createBitmap( width, height, Bitmap.Config.ARGB_8888 );
            }
      }

      /**
       * Resets the layout to its default state. Any parameters that were set or modified will be reverted back to their
       * original value. Also, stops the shimmer animation if it is currently playing.
       */
      public void useDefaults()
      {
            // Set defaults
            setAutoStart( false );
            setDuration( 1000 );
            setRepeatCount( ObjectAnimator.INFINITE );
            setRepeatDelay( 0 );
            setRepeatMode( ObjectAnimator.RESTART );
            mMask.angle = MaskAngle.CW_0;
            mMask.shape = MaskShape.LINEAR;
            mMask.dropoff = 0.5f;
            mMask.fixedWidth = 0;
            mMask.fixedHeight = 0;
            mMask.intensity = 0.0f;
            mMask.relativeWidth = 1.0f;
            mMask.relativeHeight = 1.0f;
            mMask.tilt = 20;
            mMaskTranslation = new MaskTranslation();
            setBaseAlpha( 0.3f );
            resetAll();
      }

      /**
       * Is 'auto start' enabled for this layout. When auto start is enabled, the layout will start animating automatically
       * whenever it is attached to the current window.
       *
       * @return True if 'auto start' is enabled, false otherwise
       */
      public boolean isAutoStart()
      {
            return mAutoStart;
      }

      /**
       * Enable or disable 'auto start' for this layout. When auto start is enabled, the layout will start animating
       * automatically whenever it is attached to the current window.
       *
       * @param autoStart
       *             Whether auto start should be enabled or not
       */
      public void setAutoStart( boolean autoStart )
      {
            mAutoStart = autoStart;
            resetAll();
      }

      /**
       * Get the alpha currently used to render the base view i.e. the unhighlighted view over which the highlight is drawn.
       *
       * @return Alpha (opacity) of the base view
       */
      public float getBaseAlpha()
      {
            return ( float ) mAlphaPaint.getAlpha() / 0xff;
      }

      /**
       * Set the alpha to be used to render the base view i.e. the unhighlighted view over which the highlight is drawn.
       *
       * @param alpha
       *             Alpha (opacity) of the base view
       */
      public void setBaseAlpha( float alpha )
      {
            mAlphaPaint.setAlpha( ( int ) ( clamp( 0, 1, alpha ) * 0xff ) );
            resetAll();
      }

      /**
       * Get the il_duration of the current animation i.e. the time it takes for the highlight to move from one end
       * of the layout to the other. The default value is 1000 ms.
       *
       * @return Duration of the animation, in milliseconds
       */
      public int getDuration()
      {
            return mDuration;
      }

      /**
       * Set the il_duration of the animation i.e. the time it will take for the highlight to move from one end of the layout
       * to the other.
       *
       * @param duration
       *             Duration of the animation, in milliseconds
       */
      public void setDuration( int duration )
      {
            mDuration = duration;
            resetAll();
      }

      /**
       * Get the number of times of the current animation will repeat. The default value is -1, which means the animation
       * will repeat indefinitely.
       *
       * @return Number of times the current animation will repeat, or -1 for indefinite.
       */
      public int getRepeatCount()
      {
            return mRepeatCount;
      }

      /**
       * Set the number of times the animation should repeat. If the repeat count is 0, the animation stops after reaching
       * the end. If greater than 0, or -1 (for infinite), the repeat mode is taken into account.
       *
       * @param repeatCount
       *             Number of times the current animation should repeat, or -1 for indefinite.
       */
      public void setRepeatCount( int repeatCount )
      {
            mRepeatCount = repeatCount;
            resetAll();
      }

      /**
       * Get the delay after which the current animation will repeat. The default value is 0, which means the animation
       * will repeat immediately, unless it has ended.
       *
       * @return Delay after which the current animation will repeat, in milliseconds.
       */
      public int getRepeatDelay()
      {
            return mRepeatDelay;
      }

      /**
       * Set the delay after which the animation repeat, unless it has ended.
       *
       * @param repeatDelay
       *             Delay after which the animation should repeat, in milliseconds.
       */
      public void setRepeatDelay( int repeatDelay )
      {
            mRepeatDelay = repeatDelay;
            resetAll();
      }

      /**
       * Get what the current animation will do after reaching the end. One of
       * <a href="http://developer.android.com/reference/android/animation/ValueAnimator.html#REVERSE">REVERSE</a> or
       * <a href="http://developer.android.com/reference/android/animation/ValueAnimator.html#RESTART">RESTART</a>
       *
       * @return Repeat mode of the current animation
       */
      public int getRepeatMode()
      {
            return mRepeatMode;
      }

      /**
       * Set what the animation should do after reaching the end. One of
       * <a href="http://developer.android.com/reference/android/animation/ValueAnimator.html#REVERSE">REVERSE</a> or
       * <a href="http://developer.android.com/reference/android/animation/ValueAnimator.html#RESTART">RESTART</a>
       *
       * @param repeatMode
       *             Repeat mode of the animation
       */
      public void setRepeatMode( int repeatMode )
      {
            mRepeatMode = repeatMode;
            resetAll();
      }

      /**
       * Get the il_shape of the current animation's highlight mask. One of {@link MaskShape#LINEAR} or
       * {@link MaskShape#RADIAL}
       *
       * @return The il_shape of the highlight mask
       */
      public MaskShape getMaskShape()
      {
            return mMask.shape;
      }

      /**
       * Set the il_shape of the animation's highlight mask. One of {@link MaskShape#LINEAR} or {@link MaskShape#RADIAL}
       *
       * @param shape
       *             The il_shape of the highlight mask
       */
      public void setMaskShape( MaskShape shape )
      {
            mMask.shape = shape;
            resetAll();
      }

      /**
       * Reset all.
       */
      private void resetAll()
      {
            stopShimmerAnimation();
            resetMaskBitmap();
            resetRenderedView();
      }

      /**
       * Stop the shimmer animation. Calling this method has no effect if the animation hasn't been started yet.
       */
      public void stopShimmerAnimation()
      {
            if ( mAnimator != null )
            {
                  mAnimator.end();
                  mAnimator.removeAllUpdateListeners();
                  mAnimator.cancel();
            }
            mAnimator = null;
            mAnimationStarted = false;
      }

      /**
       * Reset mask bitmap.
       */
      // If a mask bitmap was created, it's recycled and set to null so it will be recreated when needed.
      private void resetMaskBitmap()
      {
            if ( mMaskBitmap != null )
            {
                  mMaskBitmap.recycle();
                  mMaskBitmap = null;
            }
      }

      /**
       * Reset rendered view.
       */
      // If a working bitmap was created, it's recycled and set to null so it will be recreated when needed.
      private void resetRenderedView()
      {
            if ( mRenderUnmaskBitmap != null )
            {
                  mRenderUnmaskBitmap.recycle();
                  mRenderUnmaskBitmap = null;
            }
            if ( mRenderMaskBitmap != null )
            {
                  mRenderMaskBitmap.recycle();
                  mRenderMaskBitmap = null;
            }
      }

      /**
       * Get the il_angle at which the highlight mask is animated. One of:
       * <ul>
       * <li>{@link MaskAngle#CW_0} which animates left to right,</li>
       * <li>{@link MaskAngle#CW_90} which animates top to bottom,</li>
       * <li>{@link MaskAngle#CW_180} which animates right to left, or</li>
       * <li>{@link MaskAngle#CW_270} which animates bottom to top</li>
       * </ul>
       *
       * @return The {@link MaskAngle} of the current animation
       */
      public MaskAngle getAngle()
      {
            return mMask.angle;
      }

      /**
       * Set the il_angle of the highlight mask animation. One of:
       * <ul>
       * <li>{@link MaskAngle#CW_0} which animates left to right,</li>
       * <li>{@link MaskAngle#CW_90} which animates top to bottom,</li>
       * <li>{@link MaskAngle#CW_180} which animates right to left, or</li>
       * <li>{@link MaskAngle#CW_270} which animates bottom to top</li>
       * </ul>
       *
       * @param angle
       *             The {@link MaskAngle} of the new animation
       */
      public void setAngle( MaskAngle angle )
      {
            mMask.angle = angle;
            resetAll();
      }

      /**
       * Get the il_dropoff of the current animation's highlight mask. Dropoff controls the size of the fading edge of the
       * highlight.
       * <p/>
       * The default value of il_dropoff is 0.5.
       *
       * @return Dropoff of the highlight mask
       */
      public float getDropoff()
      {
            return mMask.dropoff;
      }

      /**
       * Set the il_dropoff of the animation's highlight mask, which defines the size of the highlight's fading edge.
       * <p/>
       * It is the relative distance from the center at which the highlight mask's opacity is 0 i.e it is fully transparent.
       * For a linear mask, the distance is relative to the center towards the edges. For a radial mask, the distance is
       * relative to the center towards the circumference. So a il_dropoff of 0.5 on a linear mask will create a band that
       * is half the size of the corresponding edge (depending on the {@link MaskAngle}), centered in the layout.
       *
       * @param dropoff
       *             the new il_dropoff
       */
      public void setDropoff( float dropoff )
      {
            mMask.dropoff = dropoff;
            resetAll();
      }

      /**
       * Get the fixed width of the highlight mask, or 0 if it is not set. By default it is 0.
       *
       * @return The width of the highlight mask if set, in pixels.
       */
      public int getFixedWidth()
      {
            return mMask.fixedWidth;
      }

      /**
       * Set the fixed width of the highlight mask, regardless of the size of the layout.
       *
       * @param fixedWidth
       *             The width of the highlight mask in pixels.
       */
      public void setFixedWidth( int fixedWidth )
      {
            mMask.fixedWidth = fixedWidth;
            resetAll();
      }

      /**
       * Get the fixed height of the highlight mask, or 0 if it is not set. By default it is 0.
       *
       * @return The height of the highlight mask if set, in pixels.
       */
      public int getFixedHeight()
      {
            return mMask.fixedHeight;
      }

      /**
       * Set the fixed height of the highlight mask, regardless of the size of the layout.
       *
       * @param fixedHeight
       *             The height of the highlight mask in pixels.
       */
      public void setFixedHeight( int fixedHeight )
      {
            mMask.fixedHeight = fixedHeight;
            resetAll();
      }

      /**
       * Get the il_intensity of the highlight mask, in the [0..1] range. The il_intensity controls the brightness of the
       * highlight; the higher it is, the greater is the opaque region in the highlight. The default value is 0.
       *
       * @return The il_intensity of the highlight mask
       */
      public float getIntensity()
      {
            return mMask.intensity;
      }

      /**
       * Set the il_intensity of the highlight mask, in the [0..1] range.
       * <p/>
       * Intensity is the point relative to the center where opacity starts dropping off, so an il_intensity of 0 would mean
       * that the highlight starts becoming translucent immediately from the center (the spread is controlled by 'il_dropoff').
       *
       * @param intensity
       *             The il_intensity of the highlight mask.
       */
      public void setIntensity( float intensity )
      {
            mMask.intensity = intensity;
            resetAll();
      }

      /**
       * Get the width of the highlight mask relative to the layout's width. The default is 1.0, meaning that the mask is
       * of the same width as the layout.
       *
       * @return Relative width of the highlight mask.
       */
      public float getRelativeWidth()
      {
            return mMask.relativeWidth;
      }

      /**
       * Set the width of the highlight mask relative to the layout's width, in the [0..1] range.
       *
       * @param relativeWidth
       *             Relative width of the highlight mask.
       */
      public void setRelativeWidth( int relativeWidth )
      {
            mMask.relativeWidth = relativeWidth;
            resetAll();
      }

      /**
       * Get the height of the highlight mask relative to the layout's height. The default is 1.0, meaning that the mask is
       * of the same height as the layout.
       *
       * @return Relative height of the highlight mask.
       */
      public float getRelativeHeight()
      {
            return mMask.relativeHeight;
      }

      /**
       * Set the height of the highlight mask relative to the layout's height, in the [0..1] range.
       *
       * @param relativeHeight
       *             Relative height of the highlight mask.
       */
      public void setRelativeHeight( int relativeHeight )
      {
            mMask.relativeHeight = relativeHeight;
            resetAll();
      }

      /**
       * Get the il_tilt il_angle of the highlight, in degrees. The default value is 20.
       *
       * @return The highlight's il_tilt il_angle, in degrees.
       */
      public float getTilt()
      {
            return mMask.tilt;
      }

      /**
       * Set the tile il_angle of the highlight, in degrees.
       *
       * @param tilt
       *             The highlight's il_tilt il_angle, in degrees.
       */
      public void setTilt( float tilt )
      {
            mMask.tilt = tilt;
            resetAll();
      }

      /**
       * Whether the shimmer animation is currently underway.
       *
       * @return True if the shimmer animation is playing, false otherwise.
       */
      public boolean isAnimationStarted()
      {
            return mAnimationStarted;
      }

      /* (non-Javadoc)
       * @see android.view.ViewGroup#dispatchDraw(android.graphics.Canvas)
       */
      @Override
      protected void dispatchDraw( Canvas canvas )
      {
            if ( !mAnimationStarted || getWidth() <= 0 || getHeight() <= 0 )
            {
                  super.dispatchDraw( canvas );
                  return;
            }
            dispatchDrawUsingBitmap( canvas );
      }

      /* (non-Javadoc)
       * @see android.view.ViewGroup#onAttachedToWindow()
       */
      @Override
      protected void onAttachedToWindow()
      {
            super.onAttachedToWindow();
            if ( mGlobalLayoutListener == null )
            {
                  mGlobalLayoutListener = getLayoutListener();
            }
            getViewTreeObserver().addOnGlobalLayoutListener( mGlobalLayoutListener );
      }

      /**
       * Gets the layout listener.
       *
       * @return the layout listener
       */
      private ViewTreeObserver.OnGlobalLayoutListener getLayoutListener()
      {
            return new ViewTreeObserver.OnGlobalLayoutListener()
            {
                  @Override
                  public void onGlobalLayout()
                  {
                        boolean animationStarted = mAnimationStarted;
                        resetAll();
                        if ( mAutoStart || animationStarted )
                        {
                              startShimmerAnimation();
                        }
                  }
            };
      }

      /**
       * Start the shimmer animation. If the 'auto start' property is set, this method is called automatically when the
       * layout is attached to the current window. Calling this method has no effect if the animation is already playing.
       */
      public void startShimmerAnimation()
      {
            if ( mAnimationStarted )
            {
                  return;
            }
            Animator animator = getShimmerAnimation();
            animator.start();
            mAnimationStarted = true;
      }

      /**
       * Gets the shimmer animation.
       *
       * @return the shimmer animation
       */
      // object, which is responsible for driving the highlight mask animation.
      private Animator getShimmerAnimation()
      {
            if ( mAnimator != null )
            {
                  return mAnimator;
            }
            int width  = getWidth();
            int height = getHeight();
            switch ( mMask.shape )
            {
                  default:
                  case LINEAR:
                        switch ( mMask.angle )
                        {
                              default:
                              case CW_0:
                                    mMaskTranslation.set( -width, 0, width, 0 );
                                    break;
                              case CW_90:
                                    mMaskTranslation.set( 0, -height, 0, height );
                                    break;
                              case CW_180:
                                    mMaskTranslation.set( width, 0, -width, 0 );
                                    break;
                              case CW_270:
                                    mMaskTranslation.set( 0, height, 0, -height );
                                    break;
                        }
            }
            mAnimator = ValueAnimator.ofFloat( 0.0f, 1.0f + ( float ) mRepeatDelay / mDuration );
            mAnimator.setDuration( mDuration + mRepeatDelay );
            mAnimator.setRepeatCount( mRepeatCount );
            mAnimator.setRepeatMode( mRepeatMode );
            mAnimator.addUpdateListener( new ValueAnimator.AnimatorUpdateListener()
            {
                  @Override
                  public void onAnimationUpdate( ValueAnimator animation )
                  {
                        float value = Math.max( 0.0f, Math.min( 1.0f, ( Float ) animation.getAnimatedValue() ) );
                        setMaskOffsetX( ( int ) ( mMaskTranslation.fromX * ( 1 - value ) + mMaskTranslation.toX * value ) );
                        setMaskOffsetY( ( int ) ( mMaskTranslation.fromY * ( 1 - value ) + mMaskTranslation.toY * value ) );
                  }
            } );
            return mAnimator;
      }

      /**
       * Translate the mask offset horizontally. Used by the animator.
       *
       * @param maskOffsetX
       *             Horizontal translation offset of the mask
       */
      private void setMaskOffsetX( int maskOffsetX )
      {
            if ( mMaskOffsetX == maskOffsetX )
            {
                  return;
            }
            mMaskOffsetX = maskOffsetX;
            invalidate();
      }

      /**
       * Translate the mask offset vertically. Used by the animator.
       *
       * @param maskOffsetY
       *             Vertical translation offset of the mask
       */
      private void setMaskOffsetY( int maskOffsetY )
      {
            if ( mMaskOffsetY == maskOffsetY )
            {
                  return;
            }
            mMaskOffsetY = maskOffsetY;
            invalidate();
      }

      /* (non-Javadoc)
       * @see android.view.ViewGroup#onDetachedFromWindow()
       */
      @Override
      protected void onDetachedFromWindow()
      {
            stopShimmerAnimation();
            if ( mGlobalLayoutListener != null )
            {
                  getViewTreeObserver().removeGlobalOnLayoutListener( mGlobalLayoutListener );
                  mGlobalLayoutListener = null;
            }
            super.onDetachedFromWindow();
      }

      /**
       * Draws and masks the children using a Bitmap.
       *
       * @param canvas
       *             Canvas that the masked children will end up being drawn to.
       *
       * @return true , if successful
       */
      private boolean dispatchDrawUsingBitmap( Canvas canvas )
      {
            Bitmap unmaskBitmap = tryObtainRenderUnmaskBitmap();
            Bitmap maskBitmap   = tryObtainRenderMaskBitmap();
            if ( unmaskBitmap == null || maskBitmap == null )
            {
                  return false;
            }
            // First draw a desaturated version
            drawUnmasked( new Canvas( unmaskBitmap ) );
            canvas.drawBitmap( unmaskBitmap, 0, 0, mAlphaPaint );
            // Then draw the masked version
            drawMasked( new Canvas( maskBitmap ) );
            canvas.drawBitmap( maskBitmap, 0, 0, null );
            return true;
      }

      /**
       * Try obtain render unmask bitmap.
       *
       * @return the bitmap
       */
      private Bitmap tryObtainRenderUnmaskBitmap()
      {
            if ( mRenderUnmaskBitmap == null )
            {
                  mRenderUnmaskBitmap = tryCreateRenderBitmap();
            }
            return mRenderUnmaskBitmap;
      }

      /**
       * Try obtain render mask bitmap.
       *
       * @return the bitmap
       */
      private Bitmap tryObtainRenderMaskBitmap()
      {
            if ( mRenderMaskBitmap == null )
            {
                  mRenderMaskBitmap = tryCreateRenderBitmap();
            }
            return mRenderMaskBitmap;
      }

      /**
       * Try create render bitmap.
       *
       * @return the bitmap
       */
      private Bitmap tryCreateRenderBitmap()
      {
            int width  = getWidth();
            int height = getHeight();
            try
            {
                  return createBitmapAndGcIfNecessary( width, height );
            }
            catch ( OutOfMemoryError e )
            {
                  String        logMessage              = "il_ShimmerFrameLayout failed to create working bitmap";
                  StringBuilder logMessageStringBuilder = new StringBuilder( logMessage );
                  logMessageStringBuilder.append( " (width = " );
                  logMessageStringBuilder.append( width );
                  logMessageStringBuilder.append( ", height = " );
                  logMessageStringBuilder.append( height );
                  logMessageStringBuilder.append( ")\n\n" );
                  for ( StackTraceElement stackTraceElement : Thread.currentThread().getStackTrace() )
                  {
                        logMessageStringBuilder.append( stackTraceElement.toString() );
                        logMessageStringBuilder.append( "\n" );
                  }
                  logMessage = logMessageStringBuilder.toString();
                  Log.d( TAG, logMessage );
            }
            return null;
      }

      /**
       * Draw unmasked.
       *
       * @param renderCanvas
       *             the render canvas
       */
      // Draws the children without any mask.
      private void drawUnmasked( Canvas renderCanvas )
      {
            renderCanvas.drawColor( 0, PorterDuff.Mode.CLEAR );
            super.dispatchDraw( renderCanvas );
      }

      /**
       * Draw masked.
       *
       * @param renderCanvas
       *             the render canvas
       */
      // Draws the children and masks them on the given Canvas.
      private void drawMasked( Canvas renderCanvas )
      {
            Bitmap maskBitmap = getMaskBitmap();
            if ( maskBitmap == null )
            {
                  return;
            }
            renderCanvas.clipRect( mMaskOffsetX, mMaskOffsetY, mMaskOffsetX + maskBitmap.getWidth(), mMaskOffsetY + maskBitmap.getHeight() );
            renderCanvas.drawColor( 0, PorterDuff.Mode.CLEAR );
            super.dispatchDraw( renderCanvas );
            renderCanvas.drawBitmap( maskBitmap, mMaskOffsetX, mMaskOffsetY, mMaskPaint );
      }

      /**
       * Gets the mask bitmap.
       *
       * @return the mask bitmap
       */
      // Return the mask bitmap, creating it if necessary.
      private Bitmap getMaskBitmap()
      {
            if ( mMaskBitmap != null )
            {
                  return mMaskBitmap;
            }
            int width  = mMask.maskWidth( getWidth() );
            int height = mMask.maskHeight( getHeight() );
            mMaskBitmap = createBitmapAndGcIfNecessary( width, height );
            Canvas canvas = new Canvas( mMaskBitmap );
            Shader gradient;
            switch ( mMask.shape )
            {
                  default:
                  case LINEAR:
                  {
                        int x1, y1;
                        int x2, y2;
                        switch ( mMask.angle )
                        {
                              default:
                              case CW_0:
                                    x1 = 0;
                                    y1 = 0;
                                    x2 = width;
                                    y2 = 0;
                                    break;
                              case CW_90:
                                    x1 = 0;
                                    y1 = 0;
                                    x2 = 0;
                                    y2 = height;
                                    break;
                              case CW_180:
                                    x1 = width;
                                    y1 = 0;
                                    x2 = 0;
                                    y2 = 0;
                                    break;
                              case CW_270:
                                    x1 = 0;
                                    y1 = height;
                                    x2 = 0;
                                    y2 = 0;
                                    break;
                        }
                        gradient = new LinearGradient( x1, y1, x2, y2, mMask.getGradientColors(), mMask.getGradientPositions(), Shader.TileMode.REPEAT );
                        break;
                  }
                  case RADIAL:
                  {
                        int x = width / 2;
                        int y = height / 2;
                        gradient = new RadialGradient( x, y, ( float ) ( Math.max( width, height ) / Math.sqrt( 2 ) ), mMask.getGradientColors(), mMask.getGradientPositions(), Shader.TileMode.REPEAT );
                        break;
                  }
            }
            canvas.rotate( mMask.tilt, width / 2, height / 2 );
            Paint paint = new Paint();
            paint.setShader( gradient );
            // We need to increase the rect size to account for the il_tilt
            int padding = ( int ) ( Math.sqrt( 2 ) * Math.max( width, height ) ) / 2;
            canvas.drawRect( -padding, -padding, width + padding, height + padding, paint );
            return mMaskBitmap;
      }

      /**
       * The Enum MaskShape.
       */
      // enum specifying the il_shape of the highlight mask applied to the contained view
      public enum MaskShape
      {
            /**
             * The linear.
             */
            LINEAR,
            /**
             * The radial.
             */
            RADIAL
      }

      /**
       * The Enum MaskAngle.
       */
      // enum controlling the il_angle of the highlight mask animation
      public enum MaskAngle
      {
            /**
             * The C w_0.
             */
            CW_0,
            /**
             * The C w_90.
             */
            // left to right
            CW_90,
            /**
             * The C w_180.
             */
            // top to bottom
            CW_180,
            /**
             * The C w_270.
             */
            // right to left
            CW_270, // bottom to top
      }
      // Get the shimmer <a href="http://developer.android.com/reference/android/animation/Animator.html">Animator</a>

      /**
       * The Class Mask.
       */
      // struct storing various mask related parameters, which are used to construct the mask bitmap
      private static class Mask
      {
            /**
             * The il_angle.
             */
            public MaskAngle angle;

            /**
             * The il_tilt.
             */
            public float tilt;

            /**
             * The il_dropoff.
             */
            public float dropoff;

            /**
             * The fixed width.
             */
            public int fixedWidth;

            /**
             * The fixed height.
             */
            public int fixedHeight;

            /**
             * The il_intensity.
             */
            public float intensity;

            /**
             * The relative width.
             */
            public float relativeWidth;

            /**
             * The relative height.
             */
            public float relativeHeight;

            /**
             * The il_shape.
             */
            public MaskShape shape;

            /**
             * Mask width.
             *
             * @param width
             *             the width
             *
             * @return the int
             */
            public int maskWidth( int width )
            {
                  return fixedWidth > 0 ? fixedWidth : ( int ) ( width * relativeWidth );
            }

            /**
             * Mask height.
             *
             * @param height
             *             the height
             *
             * @return the int
             */
            public int maskHeight( int height )
            {
                  return fixedHeight > 0 ? fixedHeight : ( int ) ( height * relativeHeight );
            }

            /**
             * Get the array of colors to be distributed along the gradient of the mask bitmap.
             *
             * @return An array of si_in_lb__black and transparent colors
             */
            public int[] getGradientColors()
            {
                  switch ( shape )
                  {
                        default:
                        case LINEAR:
                              return new int[] { Color.TRANSPARENT , Color.BLACK , Color.BLACK , Color.TRANSPARENT };
                        case RADIAL:
                              return new int[] { Color.BLACK , Color.BLACK , Color.TRANSPARENT };
                  }
            }

            /**
             * Get the array of relative positions [0..1] of each corresponding color in the colors array
             *
             * @return A array of float values in the [0..1] range
             */
            public float[] getGradientPositions()
            {
                  switch ( shape )
                  {
                        default:
                        case LINEAR:
                              return new float[] { Math.max( ( 1.0f - intensity - dropoff ) / 2, 0.0f ) , Math.max( ( 1.0f - intensity ) / 2, 0.0f ) , Math.min( ( 1.0f + intensity ) / 2, 1.0f ) , Math.min( ( 1.0f + intensity + dropoff ) / 2, 1.0f ) };
                        case RADIAL:
                              return new float[] { 0.0f , Math.min( intensity, 1.0f ) , Math.min( intensity + dropoff, 1.0f ) };
                  }
            }
      }

      /**
       * The Class MaskTranslation.
       */
      // struct for storing the mask translation animation values
      private static class MaskTranslation
      {
            /**
             * The from x.
             */
            public int fromX;

            /**
             * The from y.
             */
            public int fromY;

            /**
             * The to x.
             */
            public int toX;

            /**
             * The to y.
             */
            public int toY;

            /**
             * Sets the.
             *
             * @param fromX
             *             the from x
             * @param fromY
             *             the from y
             * @param toX
             *             the to x
             * @param toY
             *             the to y
             */
            public void set( int fromX, int fromY, int toX, int toY )
            {
                  this.fromX = fromX;
                  this.fromY = fromY;
                  this.toX = toX;
                  this.toY = toY;
            }
      }
}
