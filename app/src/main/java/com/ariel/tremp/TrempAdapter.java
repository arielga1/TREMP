package com.ariel.tremp;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class TrempAdapter extends RecyclerView.Adapter<TrempAdapter.TrempHolder> {
    private ArrayList<Tremp> tremps;
    private Context context;

    public TrempAdapter(ArrayList<Tremp> tremps, Context context) { //, RoomAdapterInterface anInterface) {
        this.tremps = tremps;
        this.context = context;
    }

    @NonNull
    @Override
    public TrempHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.style_tremp, parent, false);
        return new TrempHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TrempHolder holder, int position) {
        Tremp tremp = tremps.get(position);
        holder.tvFrom.setText(String.format("From: %s", tremp.getFromAddress()));
        holder.tvTo.setText(String.format("To: %s", tremp.getToAddress()));
        holder.tvDuration.setText(String.format("Duration: %s", tremp.getDuration()));
        holder.tvDistance.setText(String.format("Distance: %s", tremp.getDistance()));

        Thread imageFrom = new Thread(() -> {
            try  {
                getImageByPoint(tremp.getFromPoint(), holder.ivFrom);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        imageFrom.start();

        Thread imageTo = new Thread(() -> {
            try  {
                getImageByPoint(tremp.getToPoint(), holder.ivTo);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        imageTo.start();
    }

    @Override
    public int getItemCount() {
        return tremps.size();
    }

    private void getImageByPoint(PointData pointData, ImageView imageView) throws IOException {
        String url = "https://maps.googleapis.com/maps/api/streetview?size=100x100";
        url += "&location=" + pointData.getLatitude() + "," + pointData.getLongitude();
        url += "&key=" + context.getResources().getString(R.string.api_key);

        URL newurl = new URL(url);
        Bitmap bitmap = BitmapFactory.decodeStream(newurl.openConnection().getInputStream());
        imageView.setImageBitmap(bitmap);
    }

    public static class TrempHolder extends RecyclerView.ViewHolder {
        private final TextView tvFrom;
        private final TextView tvTo;
        private final TextView tvDuration;
        private final TextView tvDistance;
        private final ImageView ivFrom;
        private final ImageView ivTo;

        public TrempHolder(@NonNull View itemView) {
            super(itemView);

            tvFrom = itemView.findViewById(R.id.tv_from);
            tvTo = itemView.findViewById(R.id.tv_to);
            tvDuration = itemView.findViewById(R.id.tv_duration);
            tvDistance = itemView.findViewById(R.id.tv_distance);
            ivFrom = itemView.findViewById(R.id.iv_from);
            ivTo = itemView.findViewById(R.id.iv_to);
        }
    }
}
