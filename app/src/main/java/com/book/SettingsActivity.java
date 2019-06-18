package com.book;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.book.databinding.ActivitySettingsBinding;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;

import static com.book.MainActivity.iso_values_arr;

public class SettingsActivity extends AppCompatActivity implements View.OnClickListener {

    private ActivitySettingsBinding binding;
    private String selected_iso = "";
    private int selected_pos = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_settings);
        setSupportActionBar(binding.toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        LinearLayoutManager layoutManager
                = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        binding.isoRecycler.setLayoutManager(layoutManager);
        binding.isoRecycler.setAdapter(new IsoAdapter(iso_values_arr));

        binding.tvUhd.setOnClickListener(this);
        binding.tvFhd.setOnClickListener(this);
        binding.tvHd.setOnClickListener(this);
        binding.tvNeHd.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_uhd:
                selected_pos = 0;
                binding.tvUhd.setBackgroundColor(Color.parseColor("#dadada"));
                binding.tvFhd.setBackgroundColor(Color.parseColor("#ffffff"));
                binding.tvHd.setBackgroundColor(Color.parseColor("#ffffff"));
                binding.tvNeHd.setBackgroundColor(Color.parseColor("#ffffff"));
                break;
            case R.id.tv_fhd:
                selected_pos = 1;
                binding.tvUhd.setBackgroundColor(Color.parseColor("#ffffff"));
                binding.tvFhd.setBackgroundColor(Color.parseColor("#dadada"));
                binding.tvHd.setBackgroundColor(Color.parseColor("#ffffff"));
                binding.tvNeHd.setBackgroundColor(Color.parseColor("#ffffff"));
                break;
            case R.id.tv_hd:
                selected_pos = 2;
                binding.tvUhd.setBackgroundColor(Color.parseColor("#ffffff"));
                binding.tvFhd.setBackgroundColor(Color.parseColor("#ffffff"));
                binding.tvHd.setBackgroundColor(Color.parseColor("#dadada"));
                binding.tvNeHd.setBackgroundColor(Color.parseColor("#ffffff"));
                break;
            case R.id.tv_ne_hd:
                selected_pos = 3;
                binding.tvUhd.setBackgroundColor(Color.parseColor("#ffffff"));
                binding.tvFhd.setBackgroundColor(Color.parseColor("#ffffff"));
                binding.tvHd.setBackgroundColor(Color.parseColor("#ffffff"));
                binding.tvNeHd.setBackgroundColor(Color.parseColor("#dadada"));
                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.settings_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.save:
                if (!selected_iso.equals("") && selected_pos != -1 && !binding.seconds.getText().toString().isEmpty()) {
                    int duration = Integer.valueOf(binding.seconds.getText().toString())*1000;
                    if (duration >= 10000 && duration <= 30000) {
                        Log.d("LOGGERR", "save: " + selected_iso + " " + selected_pos);
                        Intent intent = new Intent();
                        intent.putExtra("iso", selected_iso);
                        intent.putExtra("quality", selected_pos);
                        intent.putExtra("duration", duration);
                        setResult(3030, intent);
                        finish();
                    } else {
                        Snackbar.make(binding.mainView, "Продолжительность должна быть от 10 до 60 секунд", Snackbar.LENGTH_LONG).show();
                    }
                } else {
                    Snackbar.make(binding.mainView, "Все параметры обязательны", Snackbar.LENGTH_LONG).show();
                }
                break;
            case android.R.id.home:
                onBackPressed();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    class IsoAdapter extends RecyclerView.Adapter<IsoAdapter.IsoViewHolder> {

        private ArrayList<String> isoList;
        private int selectedPosition = -1;

        public IsoAdapter(ArrayList<String> isoList) {
            this.isoList = isoList;
        }

        @NonNull
        @Override
        public IsoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new IsoViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.iso_item, parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull IsoViewHolder holder, int position) {
            holder.textView.setText(getItem(position));
            if (selectedPosition != position) {
                holder.itemView.setBackgroundColor(Color.parseColor("#ffffff"));
            } else {
                holder.itemView.setBackgroundColor(Color.parseColor("#DADADA"));
            }
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    selected_iso = getItem(position);
                    selectedPosition = position;
                    notifyDataSetChanged();
                }
            });
        }

        @Override
        public int getItemCount() {
            return isoList.size();
        }

        private String getItem(int position) {
            return isoList.get(position);
        }

        class IsoViewHolder extends RecyclerView.ViewHolder {

            TextView textView;

            public IsoViewHolder(@NonNull View itemView) {
                super(itemView);
                textView = itemView.findViewById(R.id.tv_iso);
            }
        }
    }
}
