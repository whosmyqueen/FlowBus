package com.logan.flowbus

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import com.logan.flowbus.databinding.FragmentTestBinding
import com.logan.flowbus.event.ActivityEvent
import com.logan.flowbus.event.FragmentEvent
import com.logan.flowbus.event.GlobalEvent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class TestFragment : Fragment() {

    companion object {
        val TAG = "TestFragmentTAG"
    }

    private var _binding: FragmentTestBinding? = null

    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentTestBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setListeners()
        subscribeGlobalEvents()
        subscribeScopeEvents()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        removeStickyEvent<GlobalEvent>()
    }

    private fun setListeners() {
        binding.btnSendGlobalEvent.setOnClickListener {
            postEvent(GlobalEvent("Test GlobalEvent"))
        }
        binding.btnSendActivityEvent.setOnClickListener {
            postEvent(scope = requireActivity(), ActivityEvent("Test ActivityEvent"))
        }
        binding.btnSendFragmentEvent.setOnClickListener {
            postEvent(scope = this@TestFragment, FragmentEvent("Test FragmentEvent"))
        }
    }

    fun getCurrentTime() = SimpleDateFormat("HH:mm:ss.SSS", Locale.getDefault()).format(Calendar.getInstance().time)

    @SuppressLint("SetTextI18n")
    private fun subscribeGlobalEvents() {
        subscribeGlobalEvent<GlobalEvent> {
            Log.d(TAG, "TestFragment received GlobalEvent 1:${it.name}")
            binding.tvGlobalEvent01.text = "${getCurrentTime()}-onReceived0-1:${it.name} "
        }
        subscribeGlobalEvent<GlobalEvent>(isSticky = true) {
            Log.d(TAG, "TestFragment received GlobalEvent 1:${it.name}")
            binding.tvGlobalEvent02.text = "${getCurrentTime()}-onReceived0-2:${it.name} "
        }
        subscribeGlobalEvent<GlobalEvent>(dispatcher = Dispatchers.Main) {
            Log.d(TAG, "TestFragment received GlobalEvent 1:${it.name}")
            binding.tvGlobalEvent03.text = "${getCurrentTime()}-onReceived0-3:${it.name} "
        }

    }

    @SuppressLint("SetTextI18n")
    private fun subscribeScopeEvents() {
        //ActivityEvent
        requireActivity().subscribeScopeEvent<ActivityEvent>(lifecycleOwner = requireActivity()) {
            Log.d(TAG, "received GlobalEvent1:${it.name}")
            binding.tvActivityEvent1.text = "${getCurrentTime()}-onReceived1:${it.name} "
        }
        requireActivity().subscribeScopeEvent<ActivityEvent>(lifecycleOwner = requireActivity(), minLifecycleState = Lifecycle.State.RESUMED) {
            Log.d(TAG, "received GlobalEvent2:${it.name}")
            binding.tvActivityEvent2.text = "${getCurrentTime()}-onReceived2:${it.name} "
        }
        requireActivity().subscribeScopeEvent<ActivityEvent>(lifecycleOwner = requireActivity(), dispatcher = Dispatchers.IO, minLifecycleState = Lifecycle.State.STARTED) {
            Log.d(TAG, "received ActivityEvent3:${it.name}")
            binding.tvActivityEvent3.text = "${getCurrentTime()}-onReceived3:${it.name} "
        }

        //FragmentEvent
        subscribeScopeEvent<FragmentEvent>(lifecycleOwner = this@TestFragment) {
            Log.d(TAG, "received FragmentEvent1:${it.name}")
            binding.tvFragmentEvent1.text = "${getCurrentTime()}-onReceived1:${it.name} "
        }
        subscribeScopeEvent<FragmentEvent>(lifecycleOwner = this@TestFragment, minLifecycleState = Lifecycle.State.RESUMED) {
            Log.d(TAG, "received FragmentEvent2:${it.name}")
            binding.tvFragmentEvent2.text = "${getCurrentTime()}-onReceived2:${it.name} "
        }
        subscribeScopeEvent<FragmentEvent>(lifecycleOwner = this@TestFragment, dispatcher = Dispatchers.Main, minLifecycleState = Lifecycle.State.STARTED) {
            Log.d(TAG, "received FragmentEvent3:${it.name}")
            binding.tvFragmentEvent3.text = "${getCurrentTime()}-onReceived3:${it.name} "
        }
    }
}