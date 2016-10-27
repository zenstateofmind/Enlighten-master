package com.example.nikhiljoshi.enlighten.pojo;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by nikhiljoshi on 4/25/16.
 */
public class Book implements Parcelable {

    public String published_date;
    public String weeks_on_list;
    public List<BookDetails> book_details;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.published_date);
        dest.writeString(this.weeks_on_list);
        dest.writeList(this.book_details);
    }

    public Book() {
    }

    protected Book(Parcel in) {
        this.published_date = in.readString();
        this.weeks_on_list = in.readString();
        this.book_details = new ArrayList<BookDetails>();
        in.readList(this.book_details, BookDetails.class.getClassLoader());
    }

    public static final Parcelable.Creator<Book> CREATOR = new Parcelable.Creator<Book>() {
        @Override
        public Book createFromParcel(Parcel source) {
            return new Book(source);
        }

        @Override
        public Book[] newArray(int size) {
            return new Book[size];
        }
    };
}
