package io.ucia0xff.fe.game;

import android.app.Activity;
import android.os.Bundle;
import android.util.DisplayMetrics;

import io.ucia0xff.fe.Values;

public class GameActivity extends Activity {
    GameView view;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        Values.SCREEN_WIDTH = dm.widthPixels;
        Values.SCREEN_HEIGHT = dm.heightPixels;
        Values.CONTEXT = this;
        view = new GameView();
        setContentView(view);
    }
}
