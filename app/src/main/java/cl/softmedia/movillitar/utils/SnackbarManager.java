package cl.softmedia.movillitar.utils;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.widget.TextView;

import cl.softmedia.movillitar.R;

/**
 * Created by iroman on 09/03/2016.
 */
public class SnackbarManager {

    public static final int INFORMATION = 0;
    public static final int WARNING = 1;
    public static final int ERROR = 2;
    public static final int SUCCESS = 3;

    public static void show(View view, String message) {
        Snackbar.make(view, message, Snackbar.LENGTH_LONG)
                .setAction("Action", null).show();
    }

    public static void show(View view, String text, int toastType) {

        Context oContext = view.getContext();
        Drawable img;
        int bg;

        switch (toastType) {

            case WARNING:
                img = oContext.getResources().getDrawable(R.drawable.icon_warning);
                bg = R.drawable.toast_background_yellow;
                break;
            case ERROR:
                img = oContext.getResources().getDrawable(R.drawable.icon_error);
                bg = R.drawable.toast_background_red;
                break;
            case SUCCESS:
                img = oContext.getResources().getDrawable(R.drawable.icon_ok);
                bg = R.drawable.toast_background_green;
                break;
            default:
                img = oContext.getResources().getDrawable(R.drawable.icon_info);
                bg = R.drawable.toast_background_blue;
                break;
        }

        Snackbar snackbar = Snackbar
                .make(view, text, Snackbar.LENGTH_LONG)
                .setAction("Action", null);
        snackbar.setActionTextColor(Color.CYAN);
        View snackbarView = snackbar.getView();
        snackbarView.setBackgroundResource(bg);
        TextView textView = (TextView) snackbarView.findViewById(android.support.design.R.id.snackbar_text);
        textView.setTextColor(Color.BLACK);
        textView.setCompoundDrawables(img, null, null, null);
        snackbar.show();
    }
}
