package mobile.smartteam.smartlight;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProviders;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import mobile.smartteam.smartlight.databinding.ActivityMainBinding;
import mobile.smartteam.smartlight.ui.MainFragment;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;
    private MainViewModel viewModel;

    private final ActivityResultLauncher<String[]> requestPermissions = registerForActivityResult(
            new ActivityResultContracts.RequestMultiplePermissions(),
            result -> {
                if (result.containsValue(false)) {
                    Toast.makeText(this, "권한이 없으면 기기를 연결할 수 없습니다.", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    if (!BluetoothAdapter.getDefaultAdapter().isEnabled()) {
                        requestBluetoothActivation();
                    }
                }
            });

    private final ActivityResultLauncher<Intent> requestBluetoothActivation = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() != Activity.RESULT_OK) {
                    Toast.makeText(this, "기기 연결을 위해 먼저 기기의 블루투스를 활성화 해 주세요.", Toast.LENGTH_SHORT).show();
                    finish();
                }
            });

    private final String[] requiredPermissions = Build.VERSION.SDK_INT >= Build.VERSION_CODES.S ? new String[]{
            android.Manifest.permission.BLUETOOTH_SCAN,
            android.Manifest.permission.BLUETOOTH_CONNECT,
            android.Manifest.permission.ACCESS_COARSE_LOCATION,
            android.Manifest.permission.ACCESS_FINE_LOCATION
    } : new String[]{
            android.Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        viewModel = ViewModelProviders.of(this).get(MainViewModel.class);
        if (!viewModel.setupViewModel()) {
            finish();
            return;
        }

        if (!checkPermissions()) {
            requestPermissions.launch(requiredPermissions);
        } else {
            if (!BluetoothAdapter.getDefaultAdapter().isEnabled()) {
                requestBluetoothActivation();
            }
        }

        viewModel.refreshPairedDevices();

        getSupportFragmentManager().beginTransaction()
                .add(R.id.fragment_container, new MainFragment())
                .commit();
    }

    private boolean checkPermissions() {
        List<Integer> status = Arrays.stream(requiredPermissions)
                .map(permission -> ContextCompat.checkSelfPermission(this, permission))
                .collect(Collectors.toList());
        return !status.contains(PackageManager.PERMISSION_DENIED);
    }

    private void requestBluetoothActivation() {
        Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        requestBluetoothActivation.launch(enableBtIntent);
    }
}

//public class MainActivity extends AppCompatActivity {
//
//    final static String TAG = "MainActivity";
//
//    private Retrofit retrofit;
//    private LightAPIService lightAPIService;
//
//    String apiUrl;
//
//    TextView tvResult;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);
//
//        tvResult = findViewById(R.id.tv);
//
//        apiUrl = getResources().getString(R.string.api_url);
//
//        OkHttpClient client = new OkHttpClient.Builder()
//                .connectTimeout(150, TimeUnit.SECONDS)
//                .readTimeout(100, TimeUnit.SECONDS)
//                .writeTimeout(100, TimeUnit.SECONDS)
//                .build();
//
//        if(retrofit == null){
//            try {
//                retrofit = new Retrofit.Builder()
//                        .baseUrl(apiUrl)
//                        .client(client)
//                        .addConverterFactory(GsonConverterFactory.create())
//                        .build();
//            } catch (Exception e){
//                e.printStackTrace();
//            }
//        }
//        lightAPIService = retrofit.create(LightAPIService.class); //retrofit 실제 객체 만들기
//
//    }
//
//    public void onClick(View v){
//        switch (v.getId()){
//            case R.id.bnt_living_room:
//                Intent intent = new Intent(this, LivingRoomActivity.class);
//                startActivity(intent);
//                break;
//            case R.id.btn_room:
//                Intent intent1 = new Intent(this, RoomActivity.class);
//                startActivity(intent1);
//                break;
//
//            case R.id.btn_test:
//                Call<HomeResult> apiCall = lightAPIService.getSituationResult();
//                apiCall.enqueue(apiCallback);
//                break;
//        }
//    }
//
//    Callback<HomeResult> apiCallback = new Callback<HomeResult>() {
//        @Override
//        public void onResponse(Call<HomeResult> call, Response<HomeResult> response) {
//            if(response.isSuccessful()){
//                HomeResult boxOfficeRoot = response.body();
//                List<RoomResult> list = boxOfficeRoot.getRoomResultList();
//                List<TimeResult> timeResultList = list.get(0).getTimeResultList();
//
//                tvResult.setText(timeResultList.get(0).getSituationResult().toString());
//            }
//        }
//
//        @Override
//        public void onFailure(Call<HomeResult> call, Throwable t) {
//            Log.e(TAG,t.toString());
//        }
//    };
//}
