package com.example.nikhiljoshi.enlighten.pojo;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by nikhiljoshi on 4/25/16.
 */
public class BookDetails implements Parcelable {

    public String title;
    public String description;
    public String author;
    public String publisher;
    public String book_image;
    public String amazon_product_url;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.title);
        dest.writeString(this.description);
        dest.writeString(this.author);
        dest.writeString(this.publisher);
        dest.writeString(this.book_image);
        dest.writeString(this.amazon_product_url);
    }

    public BookDetails() {
    }

    protected BookDetails(Parcel in) {
        this.title = in.readString();
        this.description = in.readString();
        this.author = in.readString();
        this.publisher = in.readString();
        this.book_image = in.readString();
        this.amazon_product_url = in.readString();
    }

    public static final Parcelable.Creator<BookDetails> CREATOR = new Parcelable.Creator<BookDetails>() {
        @Override
        public BookDetails createFromParcel(Parcel source) {
            return new BookDetails(source);
        }

        @Override
        public BookDetails[] newArray(int size) {
            return new BookDetails[size];
        }
    };
}
