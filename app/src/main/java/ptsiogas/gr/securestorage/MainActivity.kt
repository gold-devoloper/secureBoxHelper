package ptsiogas.gr.securestorage

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import ptsiogas.gr.securebox.SecureBoxHelper
import ptsiogas.gr.securestorage.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private var binding: ActivityMainBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        binding?.encryptButton?.setOnClickListener {
            val text = binding?.editText?.text?.toString() ?: return@setOnClickListener
            SecureBoxHelper.instance.encryptString("testVar", text)
            binding?.resultTextView?.text = "encrypted succesfully"
        }

        binding?.testButton?.setOnClickListener {
            binding?.resultTextView?.text = SecureBoxHelper.instance.decryptString("testVar")
        }

        binding?.wrongPassTestButton?.setOnClickListener {
            binding?.resultTextView?.text = SecureBoxHelper.instance.decryptString("testVar", "wrongPass")
        }

        binding?.deleteTestButton?.setOnClickListener {
            if (SecureBoxHelper.instance.deleteString("testVar")) {
                binding?.resultTextView?.text = "deleted succesfully"
            } else {
                binding?.resultTextView?.text = "error!"
            }
        }
    }


}
