package com.mb.memeboss.ui.notifications;

import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;


import com.mb.memeboss.R;
import com.mb.memeboss.ui.home.RecyclerViewAdapter;

public class NotificationsFragment extends Fragment {

//    private NotificationsViewModel notificationsViewModel;
    private DBManager dbManager;
    RecyclerView recyclerView;
    private RecyclerViewAdapter recyclerViewAdapter;
    final String[] from = new String[] { DatabaseHelper._ID,
            DatabaseHelper.MEME_OBJ};

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
//        notificationsViewModel =
//                ViewModelProviders.of(this).get(NotificationsViewModel.class);
        View root = inflater.inflate(R.layout.fragment_notifications, container, false);
        dbManager = new DBManager(root.getContext());
        dbManager.open();
        Cursor cursor = dbManager.fetch();
        recyclerView = root.findViewById(R.id.bookmarkrecyclerView);

        return root;
    }
}