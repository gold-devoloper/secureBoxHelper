package ptsiogas.gr.securebox

import android.content.Context
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.newFixedThreadPoolContext
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import java.util.*

class ThreadSafetyTests {

    @Mock
    private val mockApplicationContext: Context? = null

    @Before
    fun setupTests() {
        // Mockito has a very convenient way to inject mocks by using the @Mock annotation. To
        // inject the mocks in the test the initMocks method needs to be called.
        MockitoAnnotations.initMocks(this);
    }

    @Test
    fun testThreadSafety() {
        SecureBoxHelper.instance.init(mockApplicationContext)
        val randomPassword = UUID.randomUUID().toString()
        val randomText = UUID.randomUUID().toString()
        val filename = "threadSafetyTest"
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