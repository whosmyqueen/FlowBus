package com.logan.flowbus

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.TextUtils.replace
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import com.logan.flowbus.databinding.ActivityTestFragmentBinding
import com.logan.flowbus.event.ActivityEvent
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class TestFragmentActivity : AppCompatActivity() {
    companion object {
        val TAG = "TestFragmentActivity"
    }

    private var _binding: ActivityTestFragmentBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityTestFragmentBinding.inflate(layoutInflater)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        setContentView(binding.root)
        setupInsets()
        initView()
        setListeners()
        subscribeScopeEvents()

    }

    private fun initView() {
        with(supportFragmentManager.beginTransaction()) {
            replace(R.id.content, TestFragment())
            commitAllowingStateLoss()
        }
    }

    private fun setupInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(binding.toolbar) { view, windowInsets ->
            val insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.updatePadding(top = insets.top)
            WindowInsetsCompat.CONSUMED
        }
        setSupportActionBar(binding.toolbar)
        supportActionBar?.title = TestFragmentActivity::class.java.simpleName
    }


    @SuppressLint("SetTextI18n")
    private fun subscribeScopeEvents() {

    }

    @SuppressLint("SetTextI18n")
    private fun setListeners() {

    }

    fun getCurrentTime() = SimpleDateFormat("HH:mm:ss.SSS", Locale.getDefault()).format(Calendar.getInstance().time)

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}
