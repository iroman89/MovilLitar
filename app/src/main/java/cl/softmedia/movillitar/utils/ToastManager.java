package cl.softmedia.movillitar.utils;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import cl.softmedia.movillitar.R;


public class ToastManager {

    public static final int INFORMATION = 0;
    public static final int WARNING = 1;
    public static final int ERROR = 2;
    public static final int SUCCESS = 3;

    public ToastManager() {
    }

    public static void show(Context oContext, String text, int toastType) {

        LayoutInflater inflater = (LayoutInflater) oContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View layout = inflater.inflate(R.layout.toast_custom, null);

        TextView tv = (TextView) layout.findViewById(R.id.tvTexto);
        tv.setText("  " + text);

        LinearLayout llRoot = (LinearLayout) layout.findViewById(R.id.llRoot);

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

        tv.setCompoundDrawablesWithIntrinsicBounds(img, null, null, null);

        llRoot.setBackgroundResource(bg);

        Toast toast = new Toast(oContext);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.setDuration(Toast.LENGTH_LONG);
        toast.setView(layout);
        toast.show();
    }
}
