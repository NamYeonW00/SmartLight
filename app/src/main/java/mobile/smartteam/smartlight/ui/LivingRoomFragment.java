package mobile.smartteam.smartlight.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProviders;

import abak.tr.com.boxedverticalseekbar.BoxedVertical;
import mobile.smartteam.smartlight.BaseFragment;
import mobile.smartteam.smartlight.MainViewModel;
import mobile.smartteam.smartlight.databinding.FragmentRoomBinding;

public class LivingRoomFragment extends BaseFragment {
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

        binding.statusTextView.setText("현재 당신의 기기는 거실에 연결되어 있습니다.");

        binding.boxedVertical.setOnBoxedPointsChangeListener(new BoxedVertical.OnValuesChangeListener() {
            @Override
            public void onPointsChanged(BoxedVertical boxedPoints, int points) {
                binding.currentLevelTextView.setText(String.format("Level %d", points));
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
