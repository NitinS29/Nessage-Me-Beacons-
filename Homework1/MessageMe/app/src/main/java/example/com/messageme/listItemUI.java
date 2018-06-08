package example.com.messageme;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * Created by Nitin on 6/8/2017.
 */

public class listItemUI extends LinearLayout {

    //public TextView tilte, price, artist, date;
    public TextView sendername, date, time;//, region;
    //public ImageView productimg;

    public listItemUI(Context context) {
        super(context);
        inflateXML(context);
    }

    private void inflateXML(Context context){
        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View itemView = inflater.inflate(R.layout.listitem,this);
        this.sendername = (TextView)findViewById(R.id.tvSenderName);
        this.date = (TextView)findViewById(R.id.tvDate);
        this.time = (TextView)findViewById(R.id.tvTime);
        //this.storyImage = (ImageView)findViewById(R.id.imageViewThumbnail);
    }
}
