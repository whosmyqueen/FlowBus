package com.logan.flowbus

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import com.logan.flowbus.databinding.ActivityTestBinding
import com.logan.flowbus.event.GlobalEvent
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class TestActivity : AppCompatActivity() {
    companion object {
        val TAG = "TestActivityTAG"
    }

    private var _binding: ActivityTestBinding? = null
    private val binding get() = _binding!!


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityTestBinding.inflate(layoutInflater)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        setContentView(binding.root)
        setupInsets()
        setListeners()
        subscribeGlobalEvents()

    }

    private fun setupInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(binding.toolbar) { view, windowInsets ->
            val insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.updatePadding(top = insets.top)
            WindowInsetsCompat.CONSUMED
        }
        setSupportActionBar(binding.toolbar)
        supportActionBar?.title  = TestActivity::class.java.simpleName
    }


    @SuppressLint("SetTextI18n")
    private fun subscribeGlobalEvents() {
        subscribeEvent<GlobalEvent>(isSticky = true) {
            Log.d(TAG, "onReceived:${it.name}")
            val string = "${binding.tvEventText.text} \r\n"
            binding.tvEventText.text = "${string}${getCurrentTime()}-onReceived:${it.name}"
        }
    }

    @SuppressLint("SetTextI18n")
    private fun setListeners() {
        binding.btnSendCustomEvent.setOnClickListener {
            postEvent(GlobalEvent("Test CustomEvent"))
        }
        binding.btnSendDelayCustomEvent.setOnClickListener {
            postEvent(GlobalEvent("Test DelayCustomEvent"), 1000)
        }
        binding.btnSendManyEvent.setOnClickListener {
            (1..200).forEach { index ->
                postEvent(GlobalEvent(name = "Test ManyEvent-$index"))
            }
        }
    }

    fun getCurrentTime() = SimpleDateFormat("HH:mm:ss.SSS", Locale.getDefault()).format(Calendar.getInstance().time)

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
        removeStickyEvent<GlobalEvent>()
        removeStickyEvent<GlobalEvent>(scope = this)
        clearStickyEvent<GlobalEvent>()
        clearStickyEvent<GlobalEvent>(scope = this)
    }
}
