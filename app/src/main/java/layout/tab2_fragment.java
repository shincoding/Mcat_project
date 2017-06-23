package layout;

import java.util.Enumeration;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import java.util.Iterator;
import android.support.annotation.IntegerRes;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;


import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;


import com.example.shinaegi.mcat.ItemHolder;
import com.example.shinaegi.mcat.MainActivity;
import com.example.shinaegi.mcat.R;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class tab2_fragment extends Fragment {

    private Button btnTEST;
    private CustomAdapter recyclerViewAdapter;
    public static int cur_index;
    public static HashMap<String, String> hash_messages;
    public static HashMap<String,String[]> tab2_Markers;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tab2_fragment, container, false);

        cur_index = 0;
        hash_messages = new HashMap<String, String>();
        tab2_Markers = new HashMap<String, String[]>();

        recyclerViewAdapter = new CustomAdapter();

        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.recyclerview_items);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(recyclerViewAdapter);

        final Handler handler = new Handler();
        final Runnable worker = new Runnable() {
            @Override
            public void run() {

                try {

                    for (String key : MainActivity.hashMapTime.keySet()) {
                        Log.d("2rfgouewh", key);

                        if (!tab2_Markers.containsKey(key)) {
                            String message = MainActivity.hashMapMarker.get(key).getSnippet();
                            String time = MainActivity.hashMapTime.get(key);

                            String[] string_array = {message, time, String.valueOf(cur_index)};
                            if (string_array == null) {
                                Log.d("sadf", "ru23hro2iofwesn: ");
                            }
                            Log.d("sadfho24ifj2", string_array[1]);

                            tab2_Markers.put(key, string_array);
                            hash_messages.put(key, MainActivity.hashMapMarker.get(key).getSnippet());

                            recyclerViewAdapter.addMessage(string_array[1], key);
                            cur_index += 1;


                        }
                    }
                }
                catch(Exception e){
                    Log.d("EXCEPTION(add)F:", e.getMessage());
                }

                try{
                    ArrayList<String> keys_deleted = new ArrayList<String>();


                    for (String key : tab2_Markers.keySet()) {
                        if (!MainActivity.hashMapTime.containsKey(key)) {
                            keys_deleted.add(key);
                            Log.d("aa","1231a3dd");

                        }
                    }
                    for (String key : keys_deleted) {
                        Log.d("valUE OF INDEX:", tab2_Markers.get(key)[2]);
                        String[] deleted_list = tab2_Markers.remove(key);
                        hash_messages.remove(key);

                        recyclerViewAdapter.removeItem(key);
                        cur_index -= 1;
                    }
                }
                catch(Exception e){
                    Log.d("EXCEPTION(delete)", e.getMessage() + "INDEX :: " + String.valueOf(cur_index));
                }


                handler.postDelayed(this, 2000);

            }
        };
        handler.post(worker);

        setHasOptionsMenu(true);

        return view;
    }

    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Do something that differs the Activity's menu here
        super.onCreateOptionsMenu(menu, inflater);
        menu.getItem(0).setEnabled(false);
    }

    private class CustomAdapter extends RecyclerView.Adapter<ItemHolder>
    {

        private List<String> items = new ArrayList<>();
        private List<String> items_keys = new ArrayList<>();

        public CustomAdapter()
        {
            super();
            setHasStableIds(true);
        }



        @Override
        public long getItemId(int position)
        {
            return items.get(position).hashCode();
        }

        @Override
        public ItemHolder onCreateViewHolder(ViewGroup parent, int viewType)
        {
            return ItemHolder.make(parent);

        }

        @Override
        public void onBindViewHolder(ItemHolder holder, final int position)
        {
            holder.setModel(items.get(position));
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
                    dialog.setTitle(hash_messages.get(position));
                    dialog.setNegativeButton("Close", null);
                    dialog.show();
                }
            });
        }

        @Override
        public int getItemCount()
        {
            return items.size();
        }

        public void addMessage(String message, String key)
        {
            Log.d("sadfho24ifj2", message);

            items.add(cur_index, message);
            items_keys.add(cur_index, key);
            notifyDataSetChanged();
        }

        public void shuffleExisting()
        {
            Collections.shuffle(items);
            notifyDataSetChanged();
        }

        public void removeItem(String index)
        {
            if (!items.isEmpty())
            {
                items.remove(items_keys.indexOf(index));
                items_keys.remove(index);
                notifyDataSetChanged();
            }
        }
    }

}
