package com.uiowa.chat.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.uiowa.chat.R;
import com.uiowa.chat.data.Thread;
import com.uiowa.chat.data.User;
import com.uiowa.chat.utils.RegistrationUtils;

import java.util.List;

/**
 * This is the adapter to fill the conversation list
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
public class ThreadArrayAdapter extends ArrayAdapter<Thread> {

    protected Context context;
    private LayoutInflater inflater;

    // holds the current user's id
    private long userId;

    private List<Thread> conversations;

    // Our ViewHolder in this one will contain a TextView for the name,
    // a TextView with a hint at the latest message (Message.getLatestMessage())
    // and an ImageView that will hold a dummy avatar for the user
    public static class ViewHolder {
        public TextView name;
        public TextView messageHint;
        public ImageView picture;
    }

    public ThreadArrayAdapter(Context context, List<Thread> conversations) {
        super(context, R.layout.conversation_item);

        this.context = context;
        inflater = LayoutInflater.from(context);

        this.conversations = conversations;

        // get the current user's id
        RegistrationUtils registrationUtils = new RegistrationUtils();
        this.userId = registrationUtils.getMyUserId(context);
    }

    // returns the total size of the list
    // NOTE: total number in the list, NOT what is currently on the UI
    @Override
    public int getCount() {
        return conversations.size();
    }

    // gets the thread object at the specificed position
    // NOTE: total position on the list, NOT what is currently on the UI
    @Override
    public Thread getItem(int position) {
        return conversations.get(position);
    }

    // The adapter is telling us that we need to create a new view and that we CANNOT use a
    // recycled view that we already have
    public View newView(ViewGroup viewGroup) {
        View v;

        // inflate the layout
        v = inflater.inflate(R.layout.conversation_item, viewGroup, false);

        // create a new view holder object
        final ViewHolder holder = new ViewHolder();

        // assign the children for the view holder
        holder.name = (TextView) v.findViewById(R.id.contact_name);
        holder.messageHint = (TextView) v.findViewById(R.id.message_hint);
        holder.picture = (ImageView) v.findViewById(R.id.contact_id);

        // set the tags so that we can find all of these view without searching for them every time
        v.setTag(holder);

        return v;
    }

    // This is where we actually send the message objects information to the UI for the user to view
    public void bindView(final View view, final Thread conversation) {

        // we are using the recycled tags here so that we don't have to find them again
        final ViewHolder holder = (ViewHolder) view.getTag();

        // finds the user that isn't currently logged in, so that we can display their name
        User otherUser = conversation.getUser1();
        long otherId = conversation.getUser1Id();
        if(otherId == this.userId) {
            otherUser = conversation.getUser2();
        }

        // display their name
        holder.name.setText(otherUser.getRealName() + " (" + otherUser.getUsername() + ")");

        // latest message should never be null, but check just in case
        if (conversation.getLatestMessage() != null) {
            holder.messageHint.setText(conversation.getLatestMessage().getText());
        }
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
        bindView(v, conversations.get(position));

        return v;
    }
}

