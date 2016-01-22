package restaurant.example.com.lazeezadmin;

/**
 * Created by Pewds on 20-Nov-15.
 */

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

public class FeedListAdapter extends BaseAdapter {

    private Activity activity;
    private LayoutInflater inflater;
    private List<FeedItem> feedItems;

    public FeedListAdapter(Activity activity, List<FeedItem> feedItems) {
        this.activity = activity;
        this.feedItems = feedItems;
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public int getItemViewType(int position) {
        return feedItems.get(position).getType();
    }

    @Override
    public int getCount() {
        return feedItems.size();
    }

    @Override
    public Object getItem(int location) {
        return feedItems.get(location);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        int type = getItemViewType(position);
        FeedItem item = feedItems.get(position);

        if (inflater == null)
            inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if (type == 0)
        {
            convertView = inflater.inflate(R.layout.order_row, null);

            TextView order_id = (TextView) convertView.findViewById(R.id.order_id);
            TextView bill = (TextView) convertView.findViewById(R.id.amt);
            TextView phone = (TextView) convertView.findViewById(R.id.phone);

            order_id.setText("Order ID- "+String.valueOf(item.getOrder_id()));
            bill.setText("Bill- "+String.valueOf(item.getBill()));
            phone.setText("Phone- "+String.valueOf(item.getPhone()));

        }if (type == 1)
        {
            convertView = inflater.inflate(R.layout.content_row, null);

            TextView dish = (TextView) convertView.findViewById(R.id.dish);
            TextView quantity = (TextView) convertView.findViewById(R.id.quantity);

            dish.setText(item.getDish());
            quantity.setText("Quantity- "+String.valueOf(item.getQuantity()));

        }


        return convertView;
    }
}
