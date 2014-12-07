package com.gipsyz.safe;

import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.AdvertiseCallback;
import android.bluetooth.le.AdvertiseData;
import android.bluetooth.le.AdvertiseSettings;
import android.bluetooth.le.BluetoothLeAdvertiser;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.ParcelUuid;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.Switch;

import com.beeva.beaconsutils.BeaconListener;
import com.beeva.beaconsutils.BeaconManager;
import com.beeva.beaconsutils.BluetoothLENotSuportedException;
import com.beeva.beaconsutils.BluetoothNotActivatedException;
import com.beeva.beaconsutils.BluetoothNotSuportedException;
import com.getbase.floatingactionbutton.FloatingActionButton;
import com.gipsyz.safe.dto.BeaconList;
import com.gipsyz.safe.dto.SimpleBeacon;
import com.gipsyz.safe.gcm.GcmRegisterActivity;
import com.google.gson.reflect.TypeToken;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import uk.co.alt236.bluetoothlelib.device.IBeaconDevice;
import uk.co.alt236.bluetoothlelib.util.IBeaconUtils;

/**
 * Created by batman on 06/12/2014.
 */
public class MainActivity extends GcmRegisterActivity {

    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothManager bluetoothManager;
    private android.bluetooth.le.BluetoothLeAdvertiser mLeAdvertiser;

    private DeviceAdapter adapter;
    private ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getActionBar().setElevation(0f);
        Switch switc = (Switch) findViewById(R.id.switch1);
        switc.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

            }
        });

        listView = (ListView) findViewById(R.id.listView);
        adapter = new DeviceAdapter(this, R.layout.device, new ArrayList<SimpleBeacon>());
        listView.setAdapter(adapter);

        getBeacons();

        FloatingActionButton button = (FloatingActionButton) findViewById(R.id.floating);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final ProgressDialog dialog = ProgressDialog.show(MainActivity.this, "Beacon add",
                        "Approach a Beacon to pair", true);

                dialog.show();
                try {
                    BeaconManager manager = BeaconManager.createBeaconManager(MainActivity.this);
                    manager.setBeaconListener(new BeaconListener() {
                        @Override
                        public void onBeaconScanFinished(List<IBeaconDevice> beacons) {
                            dialog.setMessage("Checking...");
                            for (IBeaconDevice b : beacons) {
                                if (b.getDistanceDescriptor().equals(IBeaconUtils.IBeaconDistanceDescriptor.IMMEDIATE)){
                                    postBeacon(new SimpleBeacon(b));
                                }
                            }
                            dialog.dismiss();

                        }

                        @Override
                        public void noBeaconDevicesFound() {
                            dialog.dismiss();
                        }

                        @Override
                        public void onNewBeaconFound(IBeaconDevice beacon) {


                        }

                        @Override
                        public void onUpdatedBeaconFound(IBeaconDevice beacon) {

                        }
                    });
                    manager.startSingleScan();
                    dialog.setMessage("Scanning");
                    dialog.show();
                } catch (BluetoothNotActivatedException e) {
                    dialog.dismiss();
                    e.printStackTrace();
                } catch (BluetoothLENotSuportedException e) {
                    dialog.dismiss();
                    e.printStackTrace();
                } catch (BluetoothNotSuportedException e) {
                    dialog.dismiss();
                    e.printStackTrace();
                }

            }
        });
    }

    private void getBeacons() {
        Ion.with(this)
                .load(AppUtils.BEACON_URL)
                .as(new TypeToken<BeaconList>() {
                })
                .setCallback(new FutureCallback<BeaconList>() {
                    @Override
                    public void onCompleted(Exception e, BeaconList b) {
                        if (b != null && b.getObjets() != null)
                            for (SimpleBeacon beacon : b.getObjets())
                                adapter.add(beacon);
                        else e.printStackTrace();
                    }
                });
    }

    public void postBeacon(SimpleBeacon beacon){
        Ion.with(this)
                .load(AppUtils.BEACON_URL)
                .setJsonPojoBody(beacon)
                .as(new TypeToken<SimpleBeacon>() {
                })

                .setCallback(new FutureCallback<SimpleBeacon>() {
                    @Override
                    public void onCompleted(Exception e, SimpleBeacon b) {
                        if (b != null)
                            adapter.add(b);
                        else e.printStackTrace();
                    }
                });
    }

    public void open(View v){
        Ion.with(this)
                .load(AppUtils.OPEN_URL)
                .asString()
                .setCallback(new FutureCallback<String>() {
                    @Override
                    public void onCompleted(Exception e, String result) {

                    }
                });
    }

    private void check(){
        bluetoothManager = (BluetoothManager) this.getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();
        if (bluetoothManager == null) Log.e("onCreate", "bluetoothManager service is NULL!!!!");
        if (mBluetoothAdapter == null) Log.e("onCreate", "mBluetoothAdapter is NULL!!!!!");

        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            Log.d("onCreate", "BLE feature is NOT available");
        } else {
            Log.d("onCreate", "BLE feature is available");
            startAdvertising();
        }
    }


    public static void enableBluetooth(boolean enable){
        BluetoothAdapter bluetoothAdapter=BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter == null) {
            Log.e("enableBluetooth", "Not switching bluetooth since its not present");
            return;
        }
        if (bluetoothAdapter.isEnabled() == enable) {
            Log.e("enableBluetooth", "BT is enabled");
            if (bluetoothAdapter.isMultipleAdvertisementSupported()) {
                Log.d("Capability", "Multiple Advertisements supported");
            } else {
                Log.d("Capability", "Multiple Advertisements NOT supported!");
            }
            return;
        }
        if (enable) {
            bluetoothAdapter.enable();
        }
        else {
            bluetoothAdapter.disable();
        }
    }

    private void startAdvertising() {
        ParcelUuid mAdvParcelUUID = ParcelUuid.fromString("0000FEFF-0000-1000-8000-00805F9B34FB");

        mLeAdvertiser = (BluetoothLeAdvertiser)((BluetoothAdapter)((BluetoothManager)this.getSystemService(Context.BLUETOOTH_SERVICE)).getAdapter()).getBluetoothLeAdvertiser();
        if (mLeAdvertiser == null)
        {
            Log.e("startAdvertising", "didn't get a bluetooth le advertiser");
            return;
        }

        AdvertiseSettings.Builder mLeAdvSettingsBuilder =
                new AdvertiseSettings.Builder().setTxPowerLevel(AdvertiseSettings.ADVERTISE_TX_POWER_HIGH);
        mLeAdvSettingsBuilder.setAdvertiseMode(AdvertiseSettings.ADVERTISE_MODE_LOW_POWER);
        mLeAdvSettingsBuilder.setConnectable(false);
        AdvertiseData.Builder mLeAdvDataBuilder = new AdvertiseData.Builder();

        List<ParcelUuid> myUUIDs = new ArrayList<ParcelUuid>();
        myUUIDs.add(ParcelUuid.fromString("0000FE00-0000-1000-8000-00805F9B34FB"));
        byte mServiceData[] = { (byte)0xff, (byte)0xfe, (byte)0x00, (byte)0x01, (byte)0x02, (byte)0x03, (byte)0x04 };
        mLeAdvDataBuilder.addServiceData(mAdvParcelUUID, mServiceData);

        AdvertiseSettings.Builder advSetBuilder = new AdvertiseSettings.Builder();
        advSetBuilder.setAdvertiseMode(AdvertiseSettings.ADVERTISE_MODE_LOW_LATENCY);
        advSetBuilder.setConnectable(false);
        advSetBuilder.setTxPowerLevel(AdvertiseSettings.ADVERTISE_TX_POWER_MEDIUM);
        advSetBuilder.setTimeout(10000);
        Log.d("advBuild", "settings:" + advSetBuilder.build());

        AdvertiseData.Builder advDataBuilder = new AdvertiseData.Builder();
        advDataBuilder.setIncludeDeviceName(false);
        advDataBuilder.setIncludeTxPowerLevel(true);
        advDataBuilder.addServiceData(mAdvParcelUUID, mServiceData);
        mLeAdvertiser.startAdvertising(mLeAdvSettingsBuilder.build(), mLeAdvDataBuilder.build(), mLeAdvCallback);
    }

    private static BluetoothLeAdvertiser getAdvertiserHack(BluetoothAdapter adapter) {
        try {
            Class<? extends BluetoothAdapter> adapterClass = adapter.getClass();
            Field advertiserField = adapterClass.getDeclaredField("sBluetoothLeAdvertiser");
            advertiserField.setAccessible(true);
            Object advertiser = advertiserField.get(adapter);
            if (advertiser == null) {
                Field bluetoothManagerServiceField = adapterClass.getDeclaredField("mManagerService");
                bluetoothManagerServiceField.setAccessible(true);
                Object bluetoothManagerService = bluetoothManagerServiceField.get(adapter);

                Constructor<?> constructor = BluetoothLeAdvertiser.class.getDeclaredConstructor(
                        bluetoothManagerServiceField.getType());
                constructor.setAccessible(true);
                advertiser = constructor.newInstance(bluetoothManagerService);

                advertiserField.set(adapter, advertiser);
            }
            return (BluetoothLeAdvertiser) advertiser;
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Stop Advertisements
     */
    public void stopAdvertisements() {
        if (mLeAdvertiser != null) {
            mLeAdvertiser.stopAdvertising(mLeAdvCallback);
        }
    }

private final AdvertiseCallback mLeAdvCallback = new AdvertiseCallback() {
    public void onStartSuccess (AdvertiseSettings settingsInEffect) {
        Log.d("AdvertiseCallback", "onStartSuccess:" + settingsInEffect);
    }

    public void onStartFailure(int errorCode) {
        String description = "";
        if (errorCode == AdvertiseCallback.ADVERTISE_FAILED_FEATURE_UNSUPPORTED) description = "ADVERTISE_FAILED_FEATURE_UNSUPPORTED";
        else if (errorCode == AdvertiseCallback.ADVERTISE_FAILED_TOO_MANY_ADVERTISERS) description = "ADVERTISE_FAILED_TOO_MANY_ADVERTISERS";
        else if (errorCode == AdvertiseCallback.ADVERTISE_FAILED_ALREADY_STARTED) description = "ADVERTISE_FAILED_ALREADY_STARTED";
        else if (errorCode == AdvertiseCallback.ADVERTISE_FAILED_DATA_TOO_LARGE) description = "ADVERTISE_FAILED_DATA_TOO_LARGE";
        else if (errorCode == AdvertiseCallback.ADVERTISE_FAILED_INTERNAL_ERROR) description = "ADVERTISE_FAILED_INTERNAL_ERROR";
        else description = "unknown";
        Log.e("AdvertiseCB", "onFailure error:" + errorCode + " " + description);
    }
};


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
