package ptsiogas.gr.securestorage

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.newFixedThreadPoolContext
import kotlinx.coroutines.runBlocking
import ptsiogas.gr.securebox.SecureBoxHelper
import ptsiogas.gr.securestorage.databinding.ActivityMainBinding
import java.util.*
import kotlin.random.Random

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
            binding?.resultTextView?.text =
                SecureBoxHelper.instance.decryptString("testVar", "wrongPass")
        }

        binding?.deleteTestButton?.setOnClickListener {
            if (SecureBoxHelper.instance.deleteString("testVar")) {
                binding?.resultTextView?.text = "deleted succesfully"
            } else {
                binding?.resultTextView?.text = "error!"
            }
        }
    }

    /** This is a debug function for testing purposes */
    @Suppress("MagicNumber", "UnusedPrivateMember")
    private fun testThreadSafety() {
        val randomPassword = UUID.randomUUID().toString()
        val randomText = UUID.randomUUID().toString()
        val filename = "threadSafetyTest" + Random.nextInt(0, 1000)
        runBlocking {
            val scope = CoroutineScope(
                newFixedThreadPoolContext(
                    4,
                    "synchronizationPool"
                )
            ) // We want our code to run on 4 threads
            scope.launch {
                val coroutines = 1.rangeTo(100).map {
                    //create 1000 coroutines (light-weight threads).
                    launch {
                        for (i in 1..2) { // and in each of them, increment the sharedCounter 1000 times.
                            SecureBoxHelper.instance.encryptString(
                                filename,
                                randomText,
                                randomPassword
                            )
                            SecureBoxHelper.instance.decryptString(filename, randomPassword)
                        }
                    }
                }

                coroutines.forEach { coroutine ->
                    coroutine.join() // wait for all coroutines to finish their jobs.
                }
            }.join()

            val result = SecureBoxHelper.instance.decryptString(filename, randomPassword)
            assert(result == randomText)
        }
    }

}
