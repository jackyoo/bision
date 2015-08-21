package tv.bision.tanktank;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class GameView extends SurfaceView implements Runnable {
    private static final int FPS = 60;
    private static int x = 300;
    private static int y = 1200;
    private static int ground = 1250;
    private static float dx = 0;
    private static float dy = 0;
    private Thread renderThread = null;
    private boolean running = false;
    private int radius = 50;
    private int mWidth;
    private int mHeight;
    private Paint paint = new Paint();
    private SurfaceHolder mHolder;
    private boolean touched = false;
    private float tX;
    private float tY;
    private static final float G = 0.098f;

    public GameView(Context context) {
        super(context);
        mHolder = getHolder();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mWidth = MeasureSpec.getSize(widthMeasureSpec);
        mHeight = MeasureSpec.getSize(heightMeasureSpec);
    }

    public void check() {
        if ((x - radius + dx < 0) || (x + radius + dx > mWidth)) dx = -dx;
        if (y - radius + dy < 0) dy = -dy;
        if (y + radius + dy >= ground) {
            dx = 0;
            dy = 0;
        }
    }

    public void move() {
        x += dx;
        y += dy;
        dy += G;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        Log.d("GameView", "TouchEvent " + event.getAction());
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            tX = event.getX();
            tY = event.getY();
            touched = isCircleTouched(tX, tY);
            Log.d("GameView", "Circle touched " + isCircleTouched(event.getX(), event.getY()));
        }
        if (event.getAction() == MotionEvent.ACTION_UP) {
            if (touched) {
                float scale = 0.1f;
                dx = scale * (tX - event.getX());
                dy = scale * (tY - event.getY());
                Log.d("GameView", "" + dx + " " +dy);
            }
            touched = false;
        }
        return true;
    }

    private boolean isCircleTouched(float touchX, float touchY) {

        return touchX < x + radius && x > x - radius && touchY < y + radius && touchY > y - radius;
    }

    public void run() {
        long ticks = 1000 / FPS;
        long startTime;
        long sleepTime;

        while (running) {
            if (!mHolder.getSurface().isValid())
                continue;

            startTime = System.currentTimeMillis();
            move();
            check();

            Canvas canvas = mHolder.lockCanvas();
            draw(canvas);
            mHolder.unlockCanvasAndPost(canvas);

            sleepTime = ticks - (System.currentTimeMillis() - startTime);
            try {
                if (sleepTime > 0) renderThread.sleep(sleepTime);
            } catch (Exception e) {
            }
        }
    }

    public void draw(Canvas canvas) {
        super.draw(canvas);
        canvas.drawColor(Color.WHITE);
        paint.setColor(Color.BLACK);
        canvas.drawLine(0, ground,mWidth, ground, paint);
        paint.setColor(Color.BLUE);
        canvas.drawCircle(x, y, radius, paint);
    }

    public void resume() {
        running = true;
        renderThread = new Thread(this);
        renderThread.start();
    }

    public void pause() {
        running = false;
        while (true) {
            try {
                renderThread.join();
                return;
            } catch (InterruptedException e) {
            }
        }
    }

}
