package comp5216.sydney.edu.au.timefiles;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.bumptech.glide.Glide;

import java.text.DecimalFormat;
import java.util.ArrayList;

import comp5216.sydney.edu.au.timefiles.model.AppUsage;
import comp5216.sydney.edu.au.timefiles.utils.AppInMachine;

public class MainActivityAdapter extends RecyclerView.Adapter<MainActivityAdapter.MyViewHolder>{
    Context context;
    ArrayList<AppUsage> list;


    public MainActivityAdapter(Context context, ArrayList<AppUsage> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public MainActivityAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.one_app_view_for_usage_list, parent, false);
        MainActivityAdapter.MyViewHolder holder = new MainActivityAdapter.MyViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull MainActivityAdapter.MyViewHolder holder, int position) {

        AppUsage app = list.get(position);

        // set icon from AppInfo to recycler view imageView
        Glide.with(this.context).load(AppInMachine.getIcon(app.getPackageName(), context)).into(holder.icon);

        // set app name from AppInfo to recycler view textView
        holder.appName.setText(app.getAppName());
        holder.usage.setText(app.getUsage());

        if(!MainActivity.settingsInfo.getBlackListArray().contains(app.getPackageName()) || AppInMachine.getTotalTime(context) == 0){
            holder.blackRate.setVisibility(View.GONE);
        }else{
            DecimalFormat df = new DecimalFormat("0");
            holder.blackRate.setText(df.format((float) app.getTotalTime() / AppInMachine.getTotalTime(context) * 100)
                    + "% of monitored screen time");
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView icon;
        TextView appName;
        TextView usage;
        TextView blackRate;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            icon = itemView.findViewById(R.id.imageView);
            appName = itemView.findViewById(R.id.appName);
            usage = itemView.findViewById(R.id.usageView);
            blackRate = itemView.findViewById(R.id.inBlackRate);
        }
    }
}