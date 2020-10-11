package com.mb.memeboss.ui.notifications;

import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.mb.memeboss.R;
import com.zolad.zoominimageview.ZoomInImageView;

import org.json.JSONObject;

import java.util.ArrayList;


public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>  {

    private final int VIEW_TYPE_ITEM = 0;
    private final int VIEW_TYPE_LOADING = 1;
    public ArrayList<JSONObject> memes ;
    private onClickListner onclicklistner;
    public int loading = 1;
    public RecyclerViewAdapter(ArrayList<JSONObject> memes){
        this.memes = memes;

    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Log.d("VIEWTYPE","VALUE"+viewType);
        if (viewType == VIEW_TYPE_ITEM ) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_card, parent, false);
//            Log.d("Adapter Array",memes.size()+" ");
//            loading = false;
            return new ItemViewHolder(view, this);
        } else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_loading, parent, false);
            Log.d("Adapter Array",memes.size()+" ");
            return new LoadingViewHolder(view);
        }

    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof ItemViewHolder) {
            populateItemRows((ItemViewHolder) holder, position);
        } else if (holder instanceof LoadingViewHolder && loading == -1) {
//            Log.d("first","MSG "+position+" "+holder.);
            showLoadingView((LoadingViewHolder) holder, position);
        }else if(holder instanceof LoadingViewHolder && loading == 1){
            dontShowLoadingView((LoadingViewHolder) holder,position);
        }
    }

    @Override
    public int getItemCount() {
        return memes == null ? 0 : memes.size();
    }
    /**
     * The following method decides the type of ViewHolder to display in the RecyclerView
     *
     * @param position
     * @return
     */
    @Override
    public int getItemViewType(int position) {
        return memes.get(position) == null ? VIEW_TYPE_LOADING : VIEW_TYPE_ITEM;
    }


    private  class ItemViewHolder extends RecyclerView.ViewHolder  implements View.OnClickListener, View.OnLongClickListener{

        TextView title , author;
        ZoomInImageView mainImage;
        ProgressBar imageProgressBar;
        RecyclerViewAdapter recyclerViewAdapter;
        public ItemViewHolder(@NonNull View itemView,RecyclerViewAdapter recyclerViewAdapter) {
            super(itemView);
            title = itemView.findViewById(R.id.titleTV);
            author = itemView.findViewById(R.id.authorTV);
            mainImage = itemView.findViewById(R.id.mainIV);
            imageProgressBar = itemView.findViewById(R.id.imagePB);
            this.recyclerViewAdapter = recyclerViewAdapter;
            itemView.setOnClickListener(this);
        }




        @Override
        public void onClick(View view) {
            onclicklistner.onItemClick(getAdapterPosition(), view);
        }

        @Override
        public boolean onLongClick(View view) {
            onclicklistner.onItemLongClick(getAdapterPosition(), view);
            return true;
        }
    }
    public interface onClickListner {
        void onItemClick(int position, View v);
        void onItemLongClick(int position, View v);
    }

    public void setOnItemClickListener(onClickListner onclicklistner) {
        this.onclicklistner = onclicklistner;
    }


    private static class LoadingViewHolder extends RecyclerView.ViewHolder {

        ProgressBar progressBar;

        public LoadingViewHolder(@NonNull View itemView) {
            super(itemView);
            progressBar = itemView.findViewById(R.id.progressBar);
        }
    }

    private void showLoadingView(LoadingViewHolder viewHolder, int position) {
        viewHolder.progressBar.setVisibility(View.VISIBLE);
    }
    private void dontShowLoadingView(LoadingViewHolder viewHolder,int position){
        viewHolder.progressBar.setVisibility(View.GONE);
    }
    private void populateItemRows(final ItemViewHolder viewHolder, int position) {
        JSONObject obj = memes.get(position);
        try {
            viewHolder.title.setText(obj.getString("title"));
            viewHolder.author.setText(obj.getString("author"));
            String url = obj.getString("url");
            Glide.with(viewHolder.mainImage.getContext())
                    .load(url)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .listener(new RequestListener<Drawable>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                            viewHolder.imageProgressBar.setVisibility(View.GONE);
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                            viewHolder.imageProgressBar.setVisibility(View.GONE);
                            return false;
                        }
                    }).into(viewHolder.mainImage);
        }catch (Exception e){
//            Log.d("ERROR",e+" ");
//            Toast.makeText(this,"error"+e,Toast.LENGTH_LONG).show();
        }
    }
}