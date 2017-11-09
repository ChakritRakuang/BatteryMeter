package bomb.rakhuang.chakrit.batterymeter.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.ToggleButton;

import bomb.rakhuang.chakrit.batterymeter.R;

/**
 * Render the ToggleButton as my drawable checkbox graphics
 */
public class CustomToggleButton extends ToggleButton
{

    public CustomToggleButton(Context context) {
        super(context);
    }


    public CustomToggleButton(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void onFinishInflate(){
        super.onFinishInflate();
        setText("");
        updateBackground();
    }

    @SuppressLint("ObsoleteSdkInt")
    private void updateBackground(){
        int drawable = isChecked() ? R.drawable.menu_checkbox_selected : R.drawable.menu_checkbox_unselected;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            setBackground(getContext().getResources().getDrawable(drawable));
        }
    }

    @Override
    public void setChecked(boolean checked)
    {
        super.setChecked(checked);
        setText("");
        updateBackground();
    }
}