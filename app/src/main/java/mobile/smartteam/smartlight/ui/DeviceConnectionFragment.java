package mobile.smartteam.smartlight.ui;

import android.bluetooth.BluetoothDevice;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.function.Consumer;

import mobile.smartteam.smartlight.BaseFragment;
import mobile.smartteam.smartlight.MainViewModel;
import mobile.smartteam.smartlight.databinding.FragmentDeviceConnectionBinding;
import mobile.smartteam.smartlight.databinding.ItemDeviceBinding;

public class DeviceConnectionFragment extends BaseFragment {
    private FragmentDeviceConnectionBinding binding;
    private MainViewModel viewModel;
    private DeviceAdapter adapter;

    private final OnBackPressedCallback backPressedCallback = new OnBackPressedCallback(false) {
        @Override
        public void handleOnBackPressed() {
        }
    };


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentDeviceConnectionBinding.inflate(inflater, container, false);
        requireActivity().getOnBackPressedDispatcher().addCallback(backPressedCallback);

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

        adapter = new DeviceAdapter();
        adapter.setOnItemClickListener(device -> {
            new NoticeDialog(requireContext())
                    .setMessage(String.format("%s\n기기와 연결을 원하시면\n'yes'를 선택해 주세요.", device.getName()))
                    .setOnPositiveListener((dialog) -> {
                        setProgressVisibility(true);
                        viewModel.connect(device);
                    })
                    .show();
        });
        binding.recyclerView.setAdapter(adapter);

        binding.btnRoom.setOnClickListener(v -> viewModel.refreshPairedDevices());

        viewModel.getPairedDeviceList().observe(
                getViewLifecycleOwner(),
                devices -> adapter.submitList(new ArrayList<>(devices)));

        viewModel.getConnectionStatus().observe(
                getViewLifecycleOwner(),
                status -> {
                    if (status == MainViewModel.ConnectionStatus.CONNECTED) {
                        setProgressVisibility(false);
                        requireActivity().getOnBackPressedDispatcher().onBackPressed();
                    }
                }
        );
    }

    private void setProgressVisibility(boolean visible) {
        binding.progressView.setVisibility(visible ? View.VISIBLE : View.GONE);
        backPressedCallback.setEnabled(visible);
    }


    private static class DeviceAdapter extends ListAdapter<BluetoothDevice, DeviceAdapter.DeviceItemViewHolder> {
        private Consumer<BluetoothDevice> onItemClickListener;

        protected DeviceAdapter() {
            super(new DiffUtil.ItemCallback<BluetoothDevice>() {
                @Override
                public boolean areItemsTheSame(@NonNull BluetoothDevice oldItem, @NonNull BluetoothDevice newItem) {
                    return oldItem == newItem;
                }

                @Override
                public boolean areContentsTheSame(@NonNull BluetoothDevice oldItem, @NonNull BluetoothDevice newItem) {
                    return true;
                }
            });
        }

        public void setOnItemClickListener(Consumer<BluetoothDevice> onItemClickListener) {
            this.onItemClickListener = onItemClickListener;
        }

        @NonNull
        @Override
        public DeviceItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            ItemDeviceBinding binding = ItemDeviceBinding.inflate(inflater, parent, false);
            return new DeviceItemViewHolder(binding);
        }

        @Override
        public void onBindViewHolder(@NonNull DeviceItemViewHolder holder, int position) {
            BluetoothDevice device = getItem(position);
            holder.binding.nameTextView.setText(device.getName());
            holder.binding.getRoot().setOnClickListener(v -> {
                if (onItemClickListener != null) {
                    onItemClickListener.accept(device);
                }
            });
        }

        static class DeviceItemViewHolder extends RecyclerView.ViewHolder {
            final ItemDeviceBinding binding;

            DeviceItemViewHolder(ItemDeviceBinding binding) {
                super(binding.getRoot());
                this.binding = binding;
            }
        }
    }
}
