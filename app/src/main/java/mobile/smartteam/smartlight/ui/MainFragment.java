package mobile.smartteam.smartlight.ui;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProviders;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import mobile.smartteam.smartlight.BaseFragment;
import mobile.smartteam.smartlight.LightAPIService;
import mobile.smartteam.smartlight.MainViewModel;
import mobile.smartteam.smartlight.databinding.FragmentMainBinding;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class MainFragment extends BaseFragment {
    private FragmentMainBinding binding;
    private MainViewModel viewModel;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentMainBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onDestroyView() {
        binding = null;
        super.onDestroyView();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = ViewModelProviders.of(requireActivity()).get(MainViewModel.class);

//        String resultText = "[NULL]";
//
//        try {
//            resultText = new Task().execute().get();
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        } catch (ExecutionException e) {
//            e.printStackTrace();
//        }
//
//        binding.statusTextView.setText(resultText);

        binding.connectButton.setOnClickListener(v -> {
            if (!(v.getTag() instanceof MainViewModel.ConnectionStatus)) return;

            MainViewModel.ConnectionStatus status = (MainViewModel.ConnectionStatus) v.getTag();

            if (status == MainViewModel.ConnectionStatus.DISCONNECTED) {
                replaceFragment(new DeviceConnectionFragment(), "DeviceConnection");
            } else if (status == MainViewModel.ConnectionStatus.CONNECTED) {
                viewModel.disconnect();
            }
        });

        binding.roomButton.setOnClickListener(v -> {
            replaceFragment(new RoomFragment(), "Room");
        });

        binding.livingRoomButton.setOnClickListener(v -> {
            replaceFragment(new RoomFragment(), "LivingRoom");
        });

        binding.testButton.setOnClickListener(v -> {
            //sendServerDataToBluetooth();
            new NoticeDialog(requireContext())
                    .setMessage(String.format("자동 밝기 조절을 원하시면 'yes'를 선택해 주세요.",
                            new SimpleDateFormat("a hh:mm", Locale.KOREA).format(new Date())))
                    .setOnPositiveListener(dialogInterface -> {
                        replaceFragment(new AutoFragment(), "AutoRoom");
                    })
                    .show();
        });

        viewModel.getConnectionStatus().observe(
                getViewLifecycleOwner(),
                status -> {
                    if (status == MainViewModel.ConnectionStatus.CONNECTED) {
                        binding.connectedDeviceTextView.setText(viewModel.getDeviceName().getValue());
                        binding.livingRoomButton.setEnabled(true);
                        binding.roomButton.setEnabled(true);
                        binding.testButton.setEnabled(true);

                    } else {
                        binding.connectedDeviceTextView.setText("연결된 기기 없음");
                        binding.livingRoomButton.setEnabled(true);
                        binding.roomButton.setEnabled(true);
                        binding.testButton.setEnabled(true);
                    }

                    binding.connectButton.setTag(status);

                    switch (status) {
                        case DISCONNECTED:
                            binding.connectButton.setText("기기 연결하기");
                            break;
                        case CONNECTING:
                            binding.connectButton.setText("기기 연결중");
                            break;
                        case CONNECTED:
                            binding.connectButton.setText("연결 해제하기");
                            break;
                    }
                }
        );
    }

    private void sendServerDataToBluetooth() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://3.39.239.227")
                .addConverterFactory(ScalarsConverterFactory.create())
                .build();

        LightAPIService service = retrofit.create(LightAPIService.class);
        service.test().enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if (response.isSuccessful()) {
                    if (isDetached()) return;

                    String body = response.body();
                    Log.d("MainFragment", body);

                    // TODO: 서버의 데이터를 BT 를 통해 전송
                    viewModel.sendMessage(body);
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }
}
