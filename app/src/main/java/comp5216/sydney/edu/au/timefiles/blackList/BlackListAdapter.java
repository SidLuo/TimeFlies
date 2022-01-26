package comp5216.sydney.edu.au.timefiles.blackList;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

import comp5216.sydney.edu.au.timefiles.R;
import comp5216.sydney.edu.au.timefiles.model.AppInfo;
import comp5216.sydney.edu.au.timefiles.utils.AppInMachine;
import comp5216.sydney.edu.au.timefiles.whiteList.WhiteListAdapter;

public class BlackListAdapter extends RecyclerView.Adapter<BlackListAdapter.MyViewHolder> {

    Context context;
    ArrayList<AppInfo> list;

    public BlackListAdapter(Context context, ArrayList<AppInfo> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public BlackListAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.one_app_view_for_black_list, parent, false);
        BlackListAdapter.MyViewHolder holder = new BlackListAdapter.MyViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        AppInfo app = list.get(position);

        // set icon from AppInfo to recycler view imageView
        Glide.with(this.context).load(AppInMachine.getIcon(app.getPackageName(), context)).into(holder.icon);

        // set app name from AppInfo to recycler view textView
        holder.appName.setText(app.getAppName());

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView icon;
        TextView appName;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            icon = itemView.findViewById(R.id.imageView);
            appName = itemView.findViewById(R.id.appName);
        }
    }
}