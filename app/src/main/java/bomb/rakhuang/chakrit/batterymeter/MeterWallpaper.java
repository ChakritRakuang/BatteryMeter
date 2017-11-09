package bomb.rakhuang.chakrit.batterymeter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.os.Bundle;
import android.os.Handler;
import android.service.wallpaper.WallpaperService;
import android.view.SurfaceHolder;

import java.util.ArrayList;

import bomb.rakhuang.chakrit.batterymeter.drawers.BatteryDrawer;
import bomb.rakhuang.chakrit.batterymeter.drawers.CombinedWifiCellularDrawer;
import bomb.rakhuang.chakrit.batterymeter.drawers.Drawer;
import bomb.rakhuang.chakrit.batterymeter.drawers.NotificationsDrawer;

/**
 * The Live Wallpaper Service and rendering Engine
 */
@SuppressLint("Registered")
public class MeterWallpaper extends WallpaperService {

    private final String TAG = this.getClass().getSimpleName();

    private Drawer mDrawer;

    // Variable containing the index of the drawer last shown
    private int mDrawerIndex = -1;
    private long mHideTimestamp = -1;

    @Override
    public Engine onCreateEngine() {

        return new WallpaperEngine(this);
    }

    /**
     * The wallpaper engine that will handle the rendering
     */
    private  class WallpaperEngine extends Engine {

        Context mContext;

        private boolean mVisible = false;
        private final Handler mHandler = new Handler();

        WallpaperEngine(Context context) {
            this.mContext = context;
        }

        /**
         * Handle tap commands
         */
        public Bundle onCommand(String action, int x, int y, int z, Bundle extras, boolean resultRequested){
            //taps work on Nexus devices but not all, for example Samsung
            if(action.equals("android.wallpaper.tap")){
                if( mDrawer != null ) {
                    mDrawer.tap(x, y);
                }
            }
            return super.onCommand(action,x,y,z,extras,resultRequested);
        }

        /**
         * Draw runloop
         */
        private final Runnable mUpdateDisplay = new Runnable() {
            @Override
            public void run() {
                draw();
            }
        };

        /**
         * Draw function doing the context locking and rendering
         */
        private void draw() {
            if(mDrawer == null) return;

            // Ask the drawer if wants to draw in this frame
            if(mDrawer.shouldDraw()) {
                SurfaceHolder holder = getSurfaceHolder();
                Canvas c = null;
                try {
                    // Lock the drawing canvas
                    c = holder.lockCanvas();
                    if (c != null) {
                        // Let the drawer render to the canvas
                        mDrawer.draw(c);
                    }
                } finally {
                    if (c != null) holder.unlockCanvasAndPost(c);
                }
            }
            mHandler.removeCallbacks(mUpdateDisplay);
            if (mVisible) {
                // Wait one frame, and redraw
                mHandler.postDelayed(mUpdateDisplay, 33);
            }
        }

        /**
         * Toggle visibility of the wallpaper
         * In this function we create new drawers every time the wallpaper
         * is visible again, cycling through the available ones
         * @param visible whether the wallpaper is currently visible
         */
        @Override
        public void onVisibilityChanged(boolean visible) {
            mVisible = visible;
            if (visible) {

                SharedPreferences prefs = getSharedPreferences(WallpaperPreferences.PREFERENCES, MODE_PRIVATE);

                ArrayList<Class> drawerClasses = new ArrayList<>();

                if(prefs.getBoolean(WallpaperPreferences.WIFI_CELLULAR, true)) {
                    drawerClasses.add(CombinedWifiCellularDrawer.class);
                }
                if(prefs.getBoolean(WallpaperPreferences.BATTERY, true)) {
                    drawerClasses.add(BatteryDrawer.class);
                }
                if( prefs.getBoolean(WallpaperPreferences.NOTIFICATIONS, true)) {
                    drawerClasses.add(NotificationsDrawer.class);
                }

                if(System.currentTimeMillis() - mHideTimestamp > 500 || mHideTimestamp == -1) {
                    mDrawerIndex++;
                    if (mDrawerIndex >= drawerClasses.size()) {
                        mDrawerIndex = 0;
                    }
                }
                Class cls = drawerClasses.get(mDrawerIndex);
                if(cls == NotificationsDrawer.class) {
                    mDrawer = new NotificationsDrawer(mContext);
                } else if(cls == BatteryDrawer.class) {
                    mDrawer = new BatteryDrawer(mContext);
                } else {
                    mDrawer = new CombinedWifiCellularDrawer(mContext);
                }

                mDrawer.start();
                // Start the drawing loop
                draw();

            } else {
                mHideTimestamp = System.currentTimeMillis();
                if( mDrawer != null ) {
                    mDrawer.destroy();
                    mDrawer = null;
                }
                mHandler.removeCallbacks(mUpdateDisplay);
            }
        }

        @Override
        public void onSurfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            draw();
        }

        @Override
        public void onSurfaceDestroyed(SurfaceHolder holder) {
            super.onSurfaceDestroyed(holder);
            mVisible = false;
            mHandler.removeCallbacks(mUpdateDisplay);
        }

        @Override
        public void onDestroy() {
            super.onDestroy();
            mVisible = false;
            mHandler.removeCallbacks(mUpdateDisplay);
        }
    }
}