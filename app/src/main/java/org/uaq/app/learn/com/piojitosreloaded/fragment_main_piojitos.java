package org.uaq.app.learn.com.piojitosreloaded;

import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.Image;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import petrov.kristiyan.colorpicker.ColorPicker;

public class fragment_main_piojitos extends Fragment implements View.OnClickListener {
    private Button btn1sec,btn15sec;
    private ImageButton btnpalette,btnblu;
    ArrayList<String> items = new ArrayList<>();
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_main_piojitos,container,false);
        btn1sec = (Button)root.findViewById(R.id.btn1sec);
        btn1sec.setOnClickListener(this);
        btn15sec = (Button)root.findViewById(R.id.btn15sec);
        btn15sec.setOnClickListener(this);
        btnpalette = (ImageButton) root.findViewById(R.id.btncolordialog);
        btnpalette.setOnClickListener(this);
        btnblu = (ImageButton)root.findViewById(R.id.btnblue);
        btnblu.setOnClickListener(this);
        return root;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btn1sec:
                Toast.makeText(getContext(),"Btn1",Toast.LENGTH_SHORT).show();
                break;
            case R.id.btn15sec:
                Toast.makeText(getContext(),"Btn1.5",Toast.LENGTH_SHORT).show();
                break;
            case R.id.btncolordialog:
                ArrayList<String> colors = new ArrayList<>();
                colors.add("#9400d3");
                colors.add("#4b0082");
                colors.add("#0000ff");
                colors.add("#00ff00");
                colors.add("#ffff00");
                colors.add("#ff7f00");
                colors.add("#ff0000");
                final ColorPicker colorPicker = new ColorPicker(getActivity());
                        colorPicker.setTitle("Elige color");
                        colorPicker.setColors(colors);
                        colorPicker.addListenerButton("Ok", new ColorPicker.OnButtonListener() {
                            @Override
                            public void onClick(View v, int position, int color) {

                            }
                        });
                        colorPicker.addListenerButton("Cancelar", new ColorPicker.OnButtonListener() {
                            @Override
                            public void onClick(View v, int position, int color) {
                                colorPicker.dismissDialog();
                            }
                        });
                        colorPicker.disableDefaultButtons(true);
                colorPicker.show();
                break;
            case R.id.btnblue:
                doBluetoothThings();
                break;
        }
    }
    private void doBluetoothThings(){
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if(bluetoothAdapter == null){
            Toast.makeText(getContext(),"Tu dispositivo no es compatible con Bluetooth",Toast.LENGTH_SHORT).show();
        }else if(!bluetoothAdapter.isEnabled()){
            Intent blueInt = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(blueInt,1);
        }else{
            Set<BluetoothDevice> devices = bluetoothAdapter.getBondedDevices();
            if(devices.size() > 0){
                for(BluetoothDevice device: devices){
                    items.add(device.getName()+" - "+device.getAddress());
                }
                showDialogDevices(items);
            }else{
                Toast.makeText(getContext(),"Buscando dispositivos cercanos",Toast.LENGTH_SHORT).show();
                IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
                getActivity().registerReceiver(mReceiver,filter);
                bluetoothAdapter.startDiscovery();
                showDialogDevices(items);
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getActivity().unregisterReceiver(mReceiver);
    }

    public void showDialogDevices(ArrayList<String> items){
        LayoutInflater inflater = getLayoutInflater();
        View vd = inflater.inflate(R.layout.dialog_dispositivos,null);
        ListView devs = vd.findViewById(R.id.list);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(),android.R.layout.simple_expandable_list_item_1,items);
        devs.setAdapter(adapter);
        AlertDialog.Builder dialog = new AlertDialog.Builder(getContext());
        dialog.setView(vd)
                .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                })
                .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                    }
                });
        dialog.show();
    }
    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            // When discovery finds a device
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                // Get the BluetoothDevice object from the Intent
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                // Add the name and address to an array adapter to show in a ListView
                items.add(device.getName() + "-" + device.getAddress());
                Log.d("DEVICE",""+device.getName()+" - "+device.getAddress());
            }
        }
    };
}
