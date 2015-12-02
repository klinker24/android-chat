package com.uiowa.chat.adapters;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.uiowa.chat.R;
import com.uiowa.chat.data.Message;
import com.uiowa.chat.utils.RegistrationUtils;

import java.util.List;

/**
 * This is the adapter to fill the message list for a thread.
 *
 * Adapter's are annoying in Android. Complex ones take a log of code and you just want to get to the
 * end of it to see the results... But, it isn't difficult work.
 *
 * What to learn from this:
 *      - This is just an ArrayAdapter. Android has a new RecyclerView class (New in 2014) that CAN be
 *          more effient, but it is also more complex with lot of different peices, they are a lot more powerful though.
 *          ArrayAdapter's and CursorAdapter's are simple, and can be made very efficient as well.
 *          Recycler views are based off of the ViewHolder pattern that originated in basic Array and Cursor Adapters.
 *          It is the pattern that we use in these classes.
 *
 *      - ViewHolder keeps an instance of the different attributes of your views so that you do not have
 *          to find them within your layouts for every single element in the list (using the findViewById() function).
 *          It recycles the views from the already inflated layout and reuses their tags.
 *          Hard to explain, so lets check out the code.
 */
public class MessageArrayAdapter extends ArrayAdapter<Message> {

    protected Context context;
    private LayoutInflater inflater;

    // reference to the currently logged in user's id number
    private long userId;

    private List<Message> messages;

    // here is our view holder for the messages.
    // in this one, we hold an instance to the textview containing the message
    // as well as the parent (so that we can use the gravity to align the message to the right or left side of the
    // screen depending on if the current user was the sender or not)
    public static class ViewHolder {
        public TextView message;
        public LinearLayout parent;
    }

    public MessageArrayAdapter(Context context, List<Message> message) {
        super(context, R.layout.message_item);

        this.context = context;
        inflater = LayoutInflater.from(context);

        this.messages = message;

        // get the user's id
        RegistrationUtils registrationUtils = new RegistrationUtils();
        this.userId = registrationUtils.getMyUserId(context);
    }

    // returns the total size of the list
    // NOTE: total number in the list, NOT what is currently on the UI
    @Override
    public int getCount() {
        return messages.size();
    }

    // gets the thread object at the specificed position
    // NOTE: total position on the list, NOT what is currently on the UI
    @Override
    public Message getItem(int position) {
        return messages.get(position);
    }

    // The adapter is telling us that we need to create a new view and that we CANNOT use a
    // recycled view that we already have
    public View newView(ViewGroup viewGroup) {
        View v;

        // inflate the layout
        v = inflater.inflate(R.layout.message_item, viewGroup, false);

        // create a new view holder object
        final ViewHolder holder = new ViewHolder();

        // assign the children for the view holder
        holder.message = (TextView) v.findViewById(R.id.message_text);
        holder.parent = (LinearLayout) v.findViewById(R.id.parent);

        // set the tags so that we can find all of these view without searching for them every time
        // (when the view is recycled)
        v.setTag(holder);

        return v;
    }

    // This is where we actually send the message objects information to the UI for the user to view
    public void bindView(final View view, final Message message) {

        final ViewHolder holder = (ViewHolder) view.getTag();

        // gravity based on whether the current user sent the message or not
        // gravity in the parent will change the alignment for the message TextView
        int padding = context.getResources().getDimensionPixelSize(R.dimen.message_padding);
        if (message.getSenderId() == userId) {
            holder.parent.setGravity(Gravity.RIGHT);
            holder.message.setGravity(Gravity.RIGHT);
            holder.message.setPadding(padding, 0, 0, 0);
        } else {
            holder.parent.setGravity(Gravity.LEFT);
            holder.message.setGravity(Gravity.LEFT);
            holder.message.setPadding(0, 0, padding, 0);
        }

        // set the message messageText
        holder.message.setText(message.getText());
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        // We DO NOT want to create the views every time. Recycle when we can!
        // We use the ViewHolder pattern to increase performance.
        // It is by far the most common practice.
        View v;

        if (convertView == null) {
            v = newView(parent);
        } else {
            v = convertView;
        }

        // we will display all the info in bind view function.
        bindView(v, messages.get(position));

        return v;
    }
}

