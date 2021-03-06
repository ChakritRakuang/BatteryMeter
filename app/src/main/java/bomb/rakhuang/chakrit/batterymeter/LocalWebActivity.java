package bomb.rakhuang.chakrit.batterymeter;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.widget.ImageButton;

/**
 * Activity for opening html files within this projects asset directory
 */
public class LocalWebActivity extends Activity
{

    public static final String EXTRA_HTML_URI = "extra_html_uri";

    protected WebView mWebView;
    protected ImageButton mCloseBtn;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_licenses);


        Intent incoming = getIntent();
        Bundle extras = incoming.getExtras();

        String uri = "html/licenses.html";
        if(extras != null){
            uri = extras.getString(EXTRA_HTML_URI);
        }

        mWebView = findViewById(R.id.webView);
        mCloseBtn = findViewById(R.id.closeButton);

        mCloseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        mWebView.loadUrl("file:///android_asset/"+uri);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}