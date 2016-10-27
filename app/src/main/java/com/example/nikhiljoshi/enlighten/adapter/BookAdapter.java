package com.example.nikhiljoshi.enlighten.adapter;


import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.nikhiljoshi.enlighten.R;
import com.example.nikhiljoshi.enlighten.Utility;
import com.example.nikhiljoshi.enlighten.pojo.Book;
import com.example.nikhiljoshi.enlighten.pojo.BookDetails;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

/**
 * This is the adapter that will be used by the recycler view to decorate the
 * information in the RecyclerView. <br>
 * RecyclerView differs from ListView in the following ways: <br>
 *     1) ViewHolder gets a lot more power in RecyclerView... it gets to control how things get done <br>
 *     2) Things are modularized. You can take RecyclerView and plug that in into a LinearLayout
 *     or a GridLayout etc. <br>
 *     3) The onClickListener is controlled by the ViewHolder and not the ListView. Again the code
 *     is all modularized and separated <br>
 *
 *  @author nikhiljoshi
 *
 */
public class BookAdapter extends RecyclerView.Adapter<BookAdapter.BookViewHolder> implements DataSwappableAdapter<Book> {

    private List<Book> books;
    private View mEmptyView;
    private Context mContext;

    /**
     * This is the internal ViewHolder that is integral to the entire BookAdapter.
     * All BookAdapter does at this point of time is take a specific book and send it over
     * to the View Holder to decorate the book_info_lis_item.xml
     */
    public static class BookViewHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener {

        public ImageView bookImage;
        public TextView bookAuthor;
        public TextView bookCaption;

        public BookViewHolder(View itemView) {
            super(itemView);
            View bookInfoListItemView = itemView;
            bookImage = (ImageView) bookInfoListItemView.findViewById(R.id.book_image);
            bookAuthor = (TextView) bookInfoListItemView.findViewById(R.id.book_author);
            bookCaption = (TextView) bookInfoListItemView.findViewById(R.id.book_title);
            bookInfoListItemView.setOnClickListener(this);
        }

        public void bindView(Book book, int position) {
            BookDetails bookDetails = book.book_details.get(0);
            bookAuthor.setText(bookDetails.author);
            bookCaption.setText(Utility.camelCase(bookDetails.title));
            Picasso.with(bookImage.getContext()).load(bookDetails.book_image).into(bookImage);
        }

        @Override
        public void onClick(View v) {
            // put in the logic in here... I am guessing what we need to do is open a new
            // activity --> Where it dives into the details for List View
        }
    }

    public BookAdapter(Context context, View emptyView) {
        mContext = context;
        mEmptyView =  emptyView;
    }

    /**
     * Inflates up the book_info_list_item and sends the layout to the view holder
     * as a parameter in the {@link com.example.nikhiljoshi.enlighten.adapter.BookAdapter.BookViewHolder}
     * constructor
     */

    @Override
    public BookViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View bookInfoListItemView = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.book_info_list_item, parent, false);

        BookViewHolder viewHolder = new BookViewHolder(bookInfoListItemView);
        return viewHolder;
    }

    /**
     * Update the {@link com.example.nikhiljoshi.enlighten.adapter.BookAdapter.BookViewHolder}
     * to reflect the contents that are at the specific position
     */
    @Override
    public void onBindViewHolder(BookViewHolder holder, int position) {
        Book book = books.get(position);
        // TODO: What happens if index out of bound exception pops up
        holder.bindView(book, position);
    }

    @Override
    public int getItemCount() {
        return books == null ? 0 : books.size();
    }

    /**
     * Swaps the data that is going to be used in the view holder and
     * notifies the recycler view that the data has changed
     */
    public void swapData(List<Book> newBooks) {
        if (books == null) {
            books = new ArrayList<Book>();
        }

        books.clear();
        books.addAll(newBooks);
        notifyDataSetChanged();

        /**
         * If there are books that can be plugged in, then we can make the empty view
         * as invisible
         */
        if (newBooks.size() > 0) {
            mEmptyView.setVisibility(View.GONE);
        } else {
            mEmptyView.setVisibility(View.VISIBLE);
        }
    }

}
