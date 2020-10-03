package com.mb.memeboss.ui.home;

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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.baoyz.widget.PullRefreshLayout;
import com.mb.memeboss.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Objects;

public class HomeFragment extends Fragment {

    RecyclerView mainrecyclerView;
    RecyclerViewAdapter recyclerViewAdapter;
    ArrayList<JSONObject> memeObject = new ArrayList<>();
    boolean isLoading = false;
    ProgressBar progressBar,scrollPB;
    PullRefreshLayout layout;
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
//        homeViewModel =
//                ViewModelProviders.of(this).get(HomeViewModel.class);
        final View root = inflater.inflate(R.layout.fragment_home, container, false);
        mainrecyclerView = root.findViewById(R.id.mainrecyclerView);
        // Populate()
        //initAdapater
        //initScroll
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

        return root;
    }

    private void populateData(final View root) {
        String apiurl = "https://meme-api.herokuapp.com/gimme/10";
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
//                            Toast.makeText(getApplicationContext(),"Error",Toast.LENGTH_LONG).show();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getContext(),"Error",Toast.LENGTH_LONG).show();
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
                final int nextLimit = currentSize + 10;
//                    populateData(view);
                String apiurl = "https://meme-api.herokuapp.com/gimme/10";
                RequestQueue queue = Volley.newRequestQueue(Objects.requireNonNull(view.getContext()));
                StringRequest stringRequest = new StringRequest(Request.Method.GET, apiurl,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {

//                        Log.d("Response","Value"+response);
                                try {
                                    JSONObject respon = new JSONObject(response);
                                    JSONArray memes = respon.getJSONArray("memes");
//                            Log.d("memes","GOT"+memes);
                                    Log.d("SIZE","current :"+currentSize+" "+nextLimit);
                                    for(int meme = currentSize;meme< nextLimit;meme++){

                                        JSONObject obj = memes.getJSONObject(nextLimit- meme - 1);
//                                Log.d("memes","GOT"+obj);
                                        memeObject.add(obj);
                                    }
                                    Log.d("ARRAY","msg load more"+memeObject.size());
//                                    progressBar.setVisibility(View.INVISIBLE);
                                    recyclerViewAdapter.loading = 1;
//                                    intiAdapter(view);
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
//                        progressBar.setVisibility(View.GONE);
                        recyclerViewAdapter.loading = -1;
                        isLoading = true;
                    }
                }
            }
        });


    }
}