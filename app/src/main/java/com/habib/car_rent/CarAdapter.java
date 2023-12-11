package com.habib.car_rent;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.List;

public class CarAdapter extends RecyclerView.Adapter<CarAdapter.CarViewHolder> {

    private Context context;
    private List<ListCarsActivity.Car> carList;

    public CarAdapter(Context context, List<ListCarsActivity.Car> carList) {
        this.context = context;
        this.carList = carList;
    }

    @NonNull
    @Override
    public CarViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_car, parent, false);
        return new CarViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CarViewHolder holder, int position) {
        ListCarsActivity.Car car = carList.get(position);
        holder.bind(car);
    }

    @Override
    public int getItemCount() {
        return carList.size();
    }

    class CarViewHolder extends RecyclerView.ViewHolder {

        TextView carMakeTextView, carModelTextView, carDailyRateTextView;
        ImageView carImageView;
        Button bookButton;

        public CarViewHolder(View itemView) {
            super(itemView);
            carMakeTextView = itemView.findViewById(R.id.car_make);
            carModelTextView = itemView.findViewById(R.id.car_model);
            carDailyRateTextView = itemView.findViewById(R.id.car_daily_rate);
            carImageView = itemView.findViewById(R.id.car_image);
            bookButton = itemView.findViewById(R.id.book_button);
        }

        public void bind(ListCarsActivity.Car car) {
            carMakeTextView.setText(car.getMake());
            carModelTextView.setText(car.getModel());
            carDailyRateTextView.setText(String.format("TND%.2f/day", car.getDailyRate()));
            Picasso.get().load(car.getImageUrl()).into(carImageView);

            bookButton.setOnClickListener(view -> {
                Intent intent = new Intent(context, BookingActivity.class);
                intent.putExtra("CarId", car.getId());
                intent.putExtra("CarMake", car.getMake());
                intent.putExtra("CarModel", car.getModel());
                intent.putExtra("CarDailyRate", car.getDailyRate());
                intent.putExtra("CarImageUrl", car.getImageUrl());
                context.startActivity(intent);
            });
        }
    }
}
