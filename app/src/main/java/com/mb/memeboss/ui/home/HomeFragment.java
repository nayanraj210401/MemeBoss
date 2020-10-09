package com.mb.memeboss.ui.home;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.baoyz.widget.PullRefreshLayout;
import com.google.android.material.snackbar.Snackbar;
import com.mb.memeboss.R;
import com.mb.memeboss.ui.notifications.DBManager;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Objects;

/*
        Created by  github.com/nayanraj210401
        This is the Memeframent in the MainActivity
 */


public class HomeFragment extends Fragment {

    RecyclerView mainrecyclerView;
    RecyclerViewAdapter recyclerViewAdapter;
    ArrayList<JSONObject> memeObject = new ArrayList<>();
    boolean isLoading = false;
    ProgressBar progressBar,scrollPB;
    PullRefreshLayout layout;
    private DBManager dbManager;
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        final View root = inflater.inflate(R.layout.fragment_home, container, false);
        mainrecyclerView = root.findViewById(R.id.mainrecyclerView);
        dbManager = new DBManager(root.getContext());
        dbManager.open();
        layout = root.findViewById(R.id.swipeRefreshLayout);
        layout.setOnRefreshListener(new PullRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                memeObject.clear();
            populateData(root);
            layout.setRefreshing(false);
            }

        });

        progressBar = root.findViewById(R.id.loadingPD);
        scrollPB = root.findViewById(R.id.progressBar);
        populateData(root);
        progressBar.setVisibility(View.VISIBLE);
        initScrollListener(root);
        SwipperItem();
        return root;
    }

    private void populateData(final View root) {
        String apiurl = "https://meme-api.herokuapp.com/gimme/30";
        RequestQueue queue = Volley.newRequestQueue(Objects.requireNonNull(root.getContext()));
        StringRequest stringRequest = new StringRequest(Request.Method.GET, apiurl,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

//                        Log.d("Response","Value"+response);
                        try {
                            JSONObject respon = new JSONObject(response);
                            JSONArray memes = respon.getJSONArray("memes");
//                            Log.d("memes","GOT"+memes);
                            for(int meme = 0;meme< memes.length();meme++){
                                JSONObject obj = memes.getJSONObject(meme);
//                                Log.d("memes","GOT"+obj);
                                memeObject.add(obj);
                            }
                            Log.d("ARRAY","msg"+memeObject.size());
                            progressBar.setVisibility(View.INVISIBLE);
                            intiAdapter(root);
                        }catch (Exception e){
                            Toast.makeText(getContext(),"Something went Wrong !!",Toast.LENGTH_LONG).show();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getContext(),"Check your Internet or Pull to refresh",Toast.LENGTH_LONG).show();
            }
        });
        queue.add(stringRequest);

    }

    private void intiAdapter(View root){
        LinearLayoutManager llm = new LinearLayoutManager(root.getContext());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerViewAdapter = new RecyclerViewAdapter(memeObject);
        mainrecyclerView.setLayoutManager(llm);
        mainrecyclerView.setAdapter(recyclerViewAdapter);

        recyclerViewAdapter.setOnItemClickListener(new RecyclerViewAdapter.onClickListner() {
            @Override
            public void onItemClick(int position, View v) {
                try {
                    JSONObject js = memeObject.get(position);
                    String url = js.getString("url");
                    Intent sendIntent = new Intent();
                    sendIntent.setAction(Intent.ACTION_SEND);
                    sendIntent.putExtra(Intent.EXTRA_TEXT,url);
                    sendIntent.setType("text/*");
                    Intent shareIntent = Intent.createChooser(sendIntent, "memeBoss");
                    startActivity(shareIntent);
                }catch (Exception e){
                    // need to add Toast
                }
            }

            @Override
            public void onItemLongClick(int position, View v) {
                Toast.makeText(getContext(),"Long"+position,Toast.LENGTH_SHORT).show();
            }
        });

    }


    private void loadMore(final View view) {
        memeObject.add(null);
        recyclerViewAdapter.notifyItemInserted(memeObject.size() - 1);
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                memeObject.remove(memeObject.size() - 1);
                int scrollPosition = memeObject.size();
                recyclerViewAdapter.notifyItemRemoved(scrollPosition);
                final int currentSize = scrollPosition;
                final int nextLimit = currentSize + 30;
                String apiurl = "https://meme-api.herokuapp.com/gimme/30";
                RequestQueue queue = Volley.newRequestQueue(Objects.requireNonNull(view.getContext()));
                StringRequest stringRequest = new StringRequest(Request.Method.GET, apiurl,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
//                        Log.d("Response","Value"+response);
                                try {
                                    JSONObject respon = new JSONObject(response);
                                    JSONArray memes = respon.getJSONArray("memes");
                                    Log.d("SIZE","current :"+currentSize+" "+nextLimit);
                                    for(int meme = currentSize;meme< nextLimit;meme++){

                                        JSONObject obj = memes.getJSONObject(nextLimit- meme - 1);

                                        memeObject.add(obj);
                                    }
                                    Log.d("ARRAY","msg load more"+memeObject.size());
                                    recyclerViewAdapter.loading = 1;
                                }catch (Exception e){
                            Toast.makeText(view.getContext(),"Error"+e,Toast.LENGTH_LONG).show();
                                }
                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getContext(),"Error",Toast.LENGTH_LONG).show();
                    }
                });
                queue.add(stringRequest);
                recyclerViewAdapter.notifyDataSetChanged();
                isLoading = false;
            }
        }, 2000);


    }
    private void SwipperItem() {

        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0,
                ItemTouchHelper.RIGHT | ItemTouchHelper.LEFT) {

            private ColorDrawable background = new ColorDrawable(Color.TRANSPARENT);

            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();
                JSONObject obj = memeObject.get(position);
                dbManager.insert(obj.toString());
//                memeObject.set(position,null);
                memeObject.remove(position);
                recyclerViewAdapter.notifyDataSetChanged();
                Snackbar snackbar = Snackbar.make(Objects.requireNonNull(getView()),"Added to Bookmarks",Snackbar.LENGTH_SHORT);
                snackbar.show();
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
        }).attachToRecyclerView(mainrecyclerView);
    }


    private void initScrollListener(final View view) {
        mainrecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();

                if (!isLoading) {
                    if (linearLayoutManager != null && linearLayoutManager.findLastCompletelyVisibleItemPosition() == memeObject.size() - 1) {
                        //bottom of list!
                        loadMore(view);
                        recyclerViewAdapter.loading = -1;
                        isLoading = true;
                    }
                }
            }
        });


    }
}