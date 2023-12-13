package mobile.smartteam.smartlight.ui;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProviders;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import abak.tr.com.boxedverticalseekbar.BoxedVertical;
import mobile.smartteam.smartlight.BaseFragment;
import mobile.smartteam.smartlight.MainViewModel;
import mobile.smartteam.smartlight.databinding.FragmentRoomBinding;

public class RoomFragment extends BaseFragment {
    private FragmentRoomBinding binding;
    private MainViewModel viewModel;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentRoomBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = ViewModelProviders.of(requireActivity()).get(MainViewModel.class);

        binding.statusTextView.setText("현재 당신의 기기는 방에 연결되어 있습니다. ");

        binding.boxedVertical.setOnBoxedPointsChangeListener(new BoxedVertical.OnValuesChangeListener() {
            @Override
            public void onPointsChanged(BoxedVertical boxedPoints, int points) {
                binding.currentLevelTextView.setText(String.format("Level %d", points));

                // TODO: 0~5 사이의 값을 BT 를 통해 전송
                viewModel.sendMessage(String.valueOf(points));
            }

            @Override
            public void onStartTrackingTouch(BoxedVertical boxedPoints) {
            }

            @Override
            public void onStopTrackingTouch(BoxedVertical boxedPoints) {
            }
        });

        viewModel.getConnectionStatus().observe(
                getViewLifecycleOwner(),
                status -> {
                    if (status == MainViewModel.ConnectionStatus.CONNECTED) {
                        binding.connectedDeviceTextView.setText(viewModel.getDeviceName().getValue());
                    } else {
                        binding.connectedDeviceTextView.setText("연결된 기기 없음");
                    }
                }
        );
    }
}
