
package com.example.group6.appsharebook;

import android.content.Context;
import android.content.Intent;
import android.os.Parcelable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.support.v7.widget.CardView;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.firebase.ui.storage.images.FirebaseImageLoader;

import java.util.List;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.MyViewHolder> {
    private Context mContext;
    private List<Book> mData;

    public RecyclerViewAdapter(Context mContext, List<Book> mData){
        this.mContext = mContext;
        this.mData = mData;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        //This method is called right when the adapter is created and is used to initialize your
        // ViewHolder(s).
        View view;
        LayoutInflater mInflater = LayoutInflater.from(mContext);
        view = mInflater.inflate(R.layout.cardview_item_book, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position){
        //This method is called for each ViewHolder to bind it to the adapter.
        //This is where we will pass our data to our ViewHolder.
        holder.tv_book_title.setText(mData.get(position).getTitle());
        //holder.img_book_thumbnail.setImageURI(mData.get(position).getThumbnail());
        //holder.img_book_thumbnail.setImageResource(mData.get(position).getThumbnail());

        Glide.with(this.mContext)
                .load(mData.get(position).getThumbnail())
                .into(holder.img_book_thumbnail);

        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, BookActivity.class);
                intent.putExtra("Title", mData.get(position).getTitle());
                //intent.putExtra("Description", mData.get(position).getDescription());
                intent.putExtra("Thumbnail", mData.get(position).getThumbnail());
                intent.putExtra("ID",mData.get(position).getID());
                intent.putExtra("Author", mData.get(position).getAuthor());
                intent.putExtra("Edition", mData.get(position).getEdition());

                //intent.putExtra("Category",mData.get(position).getCategory());
                //start activity
                mContext.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount(){
        return mData.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{
        TextView tv_book_title;
        ImageView img_book_thumbnail;
        CardView cardView;

        public MyViewHolder(View itemView){
            super (itemView);
            tv_book_title = (TextView)itemView.findViewById(R.id.book_title_id);
            img_book_thumbnail = (ImageView)itemView.findViewById(R.id.book_img_id);
            cardView = (CardView) itemView.findViewById(R.id.cardview_id);
        }
    }

}
