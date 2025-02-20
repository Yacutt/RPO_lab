package ru.bmstu.rpoandroid;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.widget.TextView;

import java.util.Arrays;

import ru.bmstu.rpoandroid.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    // Used to load the 'rpoandroid' library on application startup.
    static {
        System.loadLibrary("rpoandroid");
        System.loadLibrary("mbedcrypto");
    }

    private ActivityMainBinding binding;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        String result;

        int res = initRng();
        byte[] data = randomBytes(10);
        byte[] key = randomBytes(1);
        byte[] encryptData = encrypt(key, data);
        byte[] decryptData = decrypt(key, encryptData);

        // Example of a call to a native method
        TextView tv = binding.sampleText;
//        tv.setText(stringFromJNI());
        result = Arrays.toString(data) + "  ==  " + Arrays.toString(encryptData) + "  ==  " + Arrays.toString(decryptData);
        tv.setText(result);
    }

    /**
     * A native method that is implemented by the 'rpoandroid' native library,
     * which is packaged with this application.
     */
    public native String stringFromJNI();
    public static native int initRng();
    public static native byte[] randomBytes(int no);
    public static native byte[] encrypt(byte[] key, byte[] data);
    public static native byte[] decrypt(byte[] key, byte[] data);
}