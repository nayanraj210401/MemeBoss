package com.mb.memeboss.ui.notifications;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.google.android.material.snackbar.Snackbar;
import com.mb.memeboss.R;
import com.mb.memeboss.ui.home.RecyclerViewAdapter;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Objects;


/*
        Created by  github.com/nayanraj210401
    This is the BookMarks module
 */

public class NotificationsFragment extends Fragment {
    private DBManager dbManager;
    ArrayList<JSONObject> memes;
    RecyclerView recyclerView;
    TextView noBookmarks;
    private RecyclerViewAdapter recyclerViewAdapter;
    final String[] from = new String[] { DatabaseHelper._ID,
            DatabaseHelper.MEME_OBJ};

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_notifications, container, false);
        dbManager = new DBManager(root.getContext());
        dbManager.open();
        Cursor cursor = dbManager.fetch();
        recyclerView = root.findViewById(R.id.bookmarkrecyclerView);
      populate(cursor);

        noBookmarks = root.findViewById(R.id.textView3);
        if(memes.isEmpty()){
            noBookmarks.setVisibility(View.VISIBLE);
        }
        intiAdapter(root);
        // TODO : Fix the delete card feature
        SwipperItem();
        recyclerViewAdapter.setOnItemClickListener(new RecyclerViewAdapter.onClickListner() {
            @Override
            public void onItemClick(int position, View v) {
                try {
                    JSONObject js = memes.get(position);
                    String url = js.getString("url");
                    Intent sendIntent = new Intent();
                    sendIntent.setAction(Intent.ACTION_SEND);
                    sendIntent.putExtra(Intent.EXTRA_TEXT, url);
                    sendIntent.setType("text/*");
                    Intent shareIntent = Intent.createChooser(sendIntent, null);
                    startActivity(shareIntent);
                }catch (Exception e){
                    //TODO
                }
            }

            @Override
            public void onItemLongClick(int position, View v) {
                Toast.makeText(getContext(),"",Toast.LENGTH_SHORT).show();
            }
        });
        dbManager.close();
        return root;
    }

    private void populate(Cursor cursor){
        try {
            memes = new ArrayList<>();
            if (cursor.moveToFirst()) {
                while (!cursor.isAfterLast()) {
                    Log.d("DB","Value :"+cursor.getString(cursor.getColumnIndex("_id")));
                    String jsonString = cursor.getString(cursor.getColumnIndex("memeobj"));
                    JSONObject obj = new JSONObject(jsonString);
                    memes.add(obj);
                    cursor.moveToNext();
                }
            }
        }catch (JSONException e){
            //TODO
        }
//        Collections.reverse(memes);
    }


    private void intiAdapter(View root){
        LinearLayoutManager llm = new LinearLayoutManager(root.getContext());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerViewAdapter = new RecyclerViewAdapter(memes);
        recyclerView.setLayoutManager(llm);
        recyclerView.setAdapter(recyclerViewAdapter);

    }

    public void showDataBase(Cursor cursor){
        if (cursor.moveToFirst()) {
            while (!cursor.isAfterLast()) {
                Log.d("DB","Value :"+cursor.getString(cursor.getColumnIndex("_id")));
//                String jsonString = cursor.getString(cursor.getColumnIndex("memeobj"));
                Log.d("jsonString","ID: "+cursor.getString(cursor.getColumnIndex("memeobj")));
                cursor.moveToNext();
            }
        }
    }
    public long getDataBaseIndex(Cursor cursor){
        long val = 0;
        cursor.moveToFirst();
        if (cursor.moveToFirst()) {
            while (!cursor.isAfterLast()) {
                 val = Long.parseLong(cursor.getString(cursor.getColumnIndex("_id")));
                cursor.moveToNext();
            }
        }
        return val;
    }

    // TODO : need to do in next version
    private void SwipperItem() {

        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0,
                ItemTouchHelper.RIGHT | ItemTouchHelper.LEFT) {

            private ColorDrawable background = new ColorDrawable(Color.TRANSPARENT);

            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NotNull RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();
                try {
                    dbManager.open();
                    Log.d("DataStatus","Status: "+dbManager.isOpen());
                    Cursor cursor = dbManager.fetch();
//                    Log.d("DELETE", "VALUE: " + cursor.getString(position) + "cursor " + cursor.getCount());
                Log.d("DELETE",  "cursor " + cursor.getCount());

                    dbManager.delete(getDataBaseIndex(cursor));

                    memes.remove(position);
                    recyclerViewAdapter.notifyDataSetChanged();

                    Snackbar snackbar = Snackbar.make(Objects.requireNonNull(getView()), "Removed from Bookmarks", Snackbar.LENGTH_SHORT);
                    snackbar.show();
                    if (memes.isEmpty()) {
                        noBookmarks.setVisibility(View.VISIBLE);
                    }
                }catch (Exception e){

                    Toast.makeText(getContext(),"Error"+e,Toast.LENGTH_LONG).show();

                }
            }

            @Override
            public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView,
                                    @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY,
                                    int actionState, boolean isCurrentlyActive) {
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
                View itemView = viewHolder.itemView;
                int backgroundCornerOffset = 10;

                if (dX > 0) { // Swiping to the right
                    background.setBounds(itemView.getLeft(), itemView.getTop(),
                            itemView.getLeft() + ((int) dX) + backgroundCornerOffset,
                            itemView.getBottom());


                } else if (dX < 0) { // Swiping to the left
                    background.setBounds(itemView.getRight() + ((int) dX) - backgroundCornerOffset,
                            itemView.getTop(), itemView.getRight(), itemView.getBottom());
                } else { // view is unSwiped
                    background.setBounds(0, 0, 0, 0);
                }
                background.draw(c);
            }
        }).attachToRecyclerView(recyclerView);
    }

}