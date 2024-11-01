package com.example.myapplication.ui.absensi;

import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.myapplication.MainActivity;
import com.example.myapplication.databinding.FragmentAbsensiBinding;
import com.example.myapplication.interfaces.AbsensiApiService;
import com.example.myapplication.models.DropdownItem;
import com.example.myapplication.models.api.AbsensiRequest;
import com.example.myapplication.models.api.ApiResponse;
import com.example.myapplication.ui.slideshow.SlideshowViewModel;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import utils.ApiClient;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class CreateAbsensiFragment extends Fragment {
    private FragmentAbsensiBinding binding;
    private String selectedKelas;
    private String selectedGuru;
    private String selectedMatpel;

    private Calendar calendar;
    private EditText jamMulai, jamAkhir;

    private static final String API_URL = "http://192.168.100.70:8000/absensi/create";

    private AbsensiApiService absensiApiService;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        SlideshowViewModel slideshowViewModel =
                new ViewModelProvider(this).get(SlideshowViewModel.class);

        binding = FragmentAbsensiBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        absensiApiService = ApiClient.getClient(requireContext()).create(AbsensiApiService.class);

        // Initialize spinner spinnerKelasType
        Spinner spinnerKelasType = binding.spinnerKelasType;

        ArrayList<DropdownItem> kelasItems = new ArrayList<>();
        kelasItems.add(new DropdownItem("10 IPA", "1"));
        kelasItems.add(new DropdownItem("11 IPS", "2"));

        ArrayAdapter<DropdownItem> adapterKelas = new ArrayAdapter<>(
                getContext(),
                android.R.layout.simple_spinner_item,
                kelasItems
        );
        adapterKelas.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerKelasType.setAdapter(adapterKelas);

        // Spinner spinnerAbsensiType selection handling
        spinnerKelasType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                DropdownItem selectedItem = (DropdownItem) parent.getItemAtPosition(position);
                selectedKelas = selectedItem.getValue();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                selectedKelas = null;
            }
        });

        // Initialize spinner selectedGuru
        Spinner spinnerGuru = binding.spinnerGuru;

        ArrayList<DropdownItem> guruItems = new ArrayList<>();
        guruItems.add(new DropdownItem("Alice", "1"));
        guruItems.add(new DropdownItem("Diana", "3"));

        ArrayAdapter<DropdownItem> adapterGuru = new ArrayAdapter<>(
                getContext(),
                android.R.layout.simple_spinner_item,
                guruItems
        );
        adapterGuru.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerGuru.setAdapter(adapterGuru);

        // Spinner spinnerGuru selection handling
        spinnerGuru.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                DropdownItem selectedItem = (DropdownItem) parent.getItemAtPosition(position);
                selectedGuru = selectedItem.getValue();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                selectedGuru = null;
            }
        });

        // Initialize spinner selectedMatPel
        Spinner spinnerMatpel = binding.spinnerMataPelajaran;

        ArrayList<DropdownItem> matpelItems = new ArrayList<>();
        matpelItems.add(new DropdownItem("Biologi", "1"));
        matpelItems.add(new DropdownItem("Fisika", "2"));
        matpelItems.add(new DropdownItem("Kimia", "3"));
        matpelItems.add(new DropdownItem("Matematika", "4"));
        matpelItems.add(new DropdownItem("Sejarah", "5"));

        ArrayAdapter<DropdownItem> adapterMatpel = new ArrayAdapter<>(
                getContext(),
                android.R.layout.simple_spinner_item,
                matpelItems
        );
        adapterMatpel.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerMatpel.setAdapter(adapterMatpel);

        // Spinner spinnerGuru selection handling
        spinnerMatpel.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                DropdownItem selectedItem = (DropdownItem) parent.getItemAtPosition(position);
                selectedMatpel = selectedItem.getValue();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                selectedMatpel = null;
            }
        });

        // Initialize time input fields
        jamMulai = binding.etJamMulai;
        jamAkhir = binding.etJamAkhir;

        calendar = Calendar.getInstance();
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());

        // Set default time to current time for both fields
        jamMulai.setText(timeFormat.format(calendar.getTime()));
        jamAkhir.setText(timeFormat.format(calendar.getTime()));

        jamMulai.setOnClickListener(v -> showTimePickerDialog(jamMulai));
        jamAkhir.setOnClickListener(v -> showTimePickerDialog(jamAkhir));

        // Handle form submission
        binding.submitButton.setOnClickListener(v -> {
            if (selectedKelas != null && selectedGuru != null && selectedMatpel != null) {
                sendAbsensiData();
            } else {
                Toast.makeText(getContext(), "Please select an absensi type: " + selectedKelas + selectedGuru + selectedMatpel, Toast.LENGTH_SHORT).show();
            }
        });

        return root;
    }

    private void sendAbsensiData() {
        String jamMulaiText = jamMulai.getText().toString();
        String jamAkhirText = jamAkhir.getText().toString();

        AbsensiRequest request = new AbsensiRequest();
        request.setKelasId(Integer.valueOf(selectedKelas));
        request.setIdGuru(Integer.valueOf(selectedGuru));
        request.setMapelId(Integer.valueOf(selectedMatpel));
        request.setJamMulai(jamMulaiText);
        request.setJamAkhir(jamAkhirText);

        Call<ApiResponse<Void>> call = absensiApiService.createAbsensi(request);
        call.enqueue(new Callback<ApiResponse<Void>>() {
            @Override
            public void onResponse(Call<ApiResponse<Void>> call, Response<ApiResponse<Void>> response) {
                if (response.isSuccessful() && response.body() != null && "00".equals(response.body().getResponseCode())) {
                    Toast.makeText(getContext(), "Absensi created successfully", Toast.LENGTH_SHORT).show();

                    // Navigate to HomeActivity
                    Intent intent = new Intent(getActivity(), MainActivity.class);
                    startActivity(intent);
                } else {
                    Toast.makeText(getContext(), "Failed to create absensi", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<Void>> call, Throwable t) {
                Toast.makeText(getContext(), "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showTimePickerDialog(EditText timeField) {
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(getContext(),
                (view, hourOfDay, minuteOfHour) -> {
                    calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                    calendar.set(Calendar.MINUTE, minuteOfHour);
                    SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
                    timeField.setText(timeFormat.format(calendar.getTime()));
                }, hour, minute, true);

        timePickerDialog.show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
