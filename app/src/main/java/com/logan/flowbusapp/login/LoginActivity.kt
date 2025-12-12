package com.logan.flowbusapp.login

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import com.logan.flowbusapp.login.event.LoginComponent
import com.logan.flowbusapp.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {
    companion object {
        val TAG = "TestActivityTAG"
    }

    private var _binding: ActivityLoginBinding? = null
    private val binding get() = _binding!!

    val loginComponent: LoginComponent by lazy { LoginComponent() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityLoginBinding.inflate(layoutInflater)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        setContentView(binding.root)
        setListeners()

        loginComponent.subscribe(this)
    }


    @SuppressLint("SetTextI18n")
    private fun setListeners() {
        binding.loginSub.setOnClickListener {
            login()
        }
        binding.registerSub.setOnClickListener {
            registerAndLogin()
        }
    }

    fun login() {
        val userName = binding.userName.text.toString()
        val password = binding.password.text.toString()
        loginComponent.login(this, userName, password)
    }

    fun registerAndLogin() {
        val userName = binding.userName.text.toString()
        val password = binding.password.text.toString()
        loginComponent.registerAndLogin(this, userName, password)
    }

    fun printLog(value: String) {
        binding.tvLog.text = value
    }

    fun showLoading() {
        binding.progressBar.show()
    }

    fun hideLoading() {
        binding.progressBar.hide()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}