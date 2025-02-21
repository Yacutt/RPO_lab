package ru.bmstu.rpoandroid;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.widget.TextView;

import java.nio.charset.StandardCharsets;
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

        String keyString = "1133123341145623";
        byte[] key = keyString.getBytes(StandardCharsets.US_ASCII);
        String dataString = "DataString";
        byte[] data = dataString.getBytes(StandardCharsets.UTF_8);
        byte[] encryptedData = encrypt(key, data);
        byte[] decryptedData = decrypt(key, encryptedData);

        // Example of a call to a native method
        TextView tv = binding.sampleText;
//        tv.setText(stringFromJNI());
        String result = stringFromJNI() + "\n"
                + "Исходные данные: " + Arrays.toString(data) + "\n"
                + "Закодированные данные: " + Arrays.toString(encryptedData) + "\n"
                + "Декодированные данные: " + Arrays.toString(decryptedData);
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