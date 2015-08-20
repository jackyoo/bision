package tv.bision.tanktank;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class GameView extends SurfaceView implements Runnable {
    private static final int FPS = 60;
    private static int x = 100;
    private static int y = 100;
    private static int dx = 1;
    private static int dy = 2;
    private Thread renderThread = null;
    private boolean running = false;
    private int radius = 50;
    private int mWidth;
    private int mHeight;
    private Paint paint = new Paint();
    private SurfaceHolder mHolder;

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
        if ((y - radius + dy < 0) || (y + radius + dy > mHeight)) dy = -dy;
    }

    public void move() {
        x += dx;
        y += dy;
    }

    public void run() {
        long ticks = 1000 / FPS;
        long startTime;
        long sleepTime;

        while (running) {
            if (!mHolder.getSurface().isValid())
                continue;

            startTime = System.currentTimeMillis();
            check();
            move();
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
