package huaxia.demo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.summer.chxplayer.widght.utils.anim.LoveLikeLayout;

public class NewActivity extends AppCompatActivity {

    private LoveLikeLayout love;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new);
        love = (LoveLikeLayout) findViewById(R.id.love_layout);
    }

    public void love(View v) {
        love.addLove();
    }
}
