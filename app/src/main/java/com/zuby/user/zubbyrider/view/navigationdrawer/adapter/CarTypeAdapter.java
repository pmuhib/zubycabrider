package com.zuby.user.zubbyrider.view.navigationdrawer.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.zuby.user.zubbyrider.R;
import com.zuby.user.zubbyrider.view.navigationdrawer.model.CarServiceModel;

import java.util.List;

public class CarTypeAdapter extends RecyclerView.Adapter<CarTypeAdapter.MyViewHolder> {

    private List<CarServiceModel.DataBean.DetailsBean.ArrayBean> moviesList;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView title;
        public RecyclerView cardata;

        public MyViewHolder(View view) {
            super(view);
        }
    }


    public CarTypeAdapter(List<CarServiceModel.DataBean.DetailsBean.ArrayBean> moviesList) {
        this.moviesList = moviesList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.car_data_layout, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        CarServiceModel.DataBean.DetailsBean.ArrayBean movie = moviesList.get(position);
       // holder.title.setText(movie.getAggregationName());
    }

    @Override
    public int getItemCount() {
        return moviesList.size();
    }
}
