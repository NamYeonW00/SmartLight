package mobile.smartteam.smartlight;

import android.app.Application;
import android.bluetooth.BluetoothDevice;
import android.text.TextUtils;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.harrysoft.androidbluetoothserial.BluetoothManager;
import com.harrysoft.androidbluetoothserial.SimpleBluetoothDeviceInterface;

import org.jetbrains.annotations.NotNull;

import java.util.Collection;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

public class MainViewModel extends AndroidViewModel {
    private boolean viewModelSetup = false;
    private BluetoothManager bluetoothManager;

    private final MutableLiveData<Collection<BluetoothDevice>> pairedDeviceList = new MutableLiveData<>();

    private final CompositeDisposable compositeDisposable = new CompositeDisposable();

    @Nullable
    private SimpleBluetoothDeviceInterface deviceInterface;

    private final MutableLiveData<ConnectionStatus> connectionStatusData = new MutableLiveData<>(ConnectionStatus.DISCONNECTED);
    private final MutableLiveData<String> deviceNameData = new MutableLiveData<>();
    private boolean connectionAttemptedOrMade = false;


    public MainViewModel(@NotNull Application application) {
        super(application);
    }

    public boolean setupViewModel() {
        if (!viewModelSetup) {
            viewModelSetup = true;

            bluetoothManager = BluetoothManager.getInstance();
            if (bluetoothManager == null) {
                Toast.makeText(getApplication(), "블루투스를 지원하지 않는 기기 입니다.", Toast.LENGTH_LONG).show();
                return false;
            }
        }

        return true;
    }

    public void refreshPairedDevices() {
        pairedDeviceList.postValue(bluetoothManager.getPairedDevicesList());
    }

    @Override
    protected void onCleared() {
        disconnect();
        compositeDisposable.dispose();

        if (bluetoothManager != null) bluetoothManager.close();
    }

    public LiveData<Collection<BluetoothDevice>> getPairedDeviceList() {
        return pairedDeviceList;
    }

    public void connect(BluetoothDevice d) {
        // Check we are not already connecting or connected
        if (!connectionAttemptedOrMade) {
            deviceNameData.postValue(d.getName());

            compositeDisposable.add(bluetoothManager.openSerialDevice(d.getAddress())
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(device -> onConnected(device.toSimpleDeviceInterface()), t -> {
                        t.printStackTrace();

                        toast("연결에 실패하였습니다.");
                        connectionAttemptedOrMade = false;

                        deviceNameData.postValue("");
                        connectionStatusData.postValue(ConnectionStatus.DISCONNECTED);
                    }));

            connectionAttemptedOrMade = true;
            connectionStatusData.postValue(ConnectionStatus.CONNECTING);
        }
    }

    public void disconnect() {
        if (connectionAttemptedOrMade && deviceInterface != null) {
            connectionAttemptedOrMade = false;

            bluetoothManager.closeDevice(deviceInterface);
            deviceInterface = null;

            deviceNameData.postValue("");
            connectionStatusData.postValue(ConnectionStatus.DISCONNECTED);
        }
    }

    private void onConnected(SimpleBluetoothDeviceInterface deviceInterface) {
        this.deviceInterface = deviceInterface;
        if (this.deviceInterface != null) {
            connectionStatusData.postValue(ConnectionStatus.CONNECTED);
            this.deviceInterface.setListeners(null, null, t -> t.printStackTrace());

        } else {
            toast("연결에 실패하였습니다.");

            deviceNameData.postValue("");
            connectionStatusData.postValue(ConnectionStatus.DISCONNECTED);
        }
    }

    public void sendMessage(String message) {
        if (deviceInterface != null && !TextUtils.isEmpty(message)) {
            deviceInterface.sendMessage(message);
        }
    }

    private void toast(String message) {
        Toast.makeText(getApplication(), message, Toast.LENGTH_SHORT).show();
    }

    public LiveData<ConnectionStatus> getConnectionStatus() {
        return connectionStatusData;
    }

    public LiveData<String> getDeviceName() {
        return deviceNameData;
    }

    public enum ConnectionStatus {
        DISCONNECTED,
        CONNECTING,
        CONNECTED
    }
}