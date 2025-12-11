package com.logan.flowbus

import androidx.annotation.MainThread
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch


/**
 * 订阅 **Application (全局)** 作用域的事件。
 *
 * 该扩展函数基于 [LifecycleOwner] (例如 Activity 或 Fragment) 的生命周期来管理订阅。
 * 当 [LifecycleOwner] 的状态达到 [minLifecycleState] 时开始收集事件，并在生命周期结束时自动停止。
 *
 * @receiver LifecycleOwner 事件订阅的生命周期所有者。
 * @param T 事件的数据类型。事件名默认为 T 的完整类名。
 * @param dispatcher 用于执行 [onReceived] lambda 的协程调度器，默认为主线程。
 * @param minLifecycleState 订阅开始收集所需的最小生命周期状态 (如 STARTED, RESUMED)。
 * @param isSticky 事件是否为粘性事件 (Sticky Event)。粘性事件会重放最新的一个事件给新的订阅者。
 * @param onReceived 接收到事件时执行的回调函数。
 * @return Job 事件收集的 Job，可用于手动取消订阅。
 */
@MainThread
inline fun <reified T> LifecycleOwner.subscribeGlobalEvent(
    dispatcher: CoroutineDispatcher = Dispatchers.Main.immediate,
    minLifecycleState: Lifecycle.State = Lifecycle.State.STARTED,
    isSticky: Boolean = false,
    noinline onReceived: (T) -> Unit
): Job {
    return ApplicationScopeViewModelProvider.get(FlowEventBus::class.java)
        .subscribe(
            this,
            T::class.java.name,
            minLifecycleState,
            dispatcher,
            isSticky,
            onReceived
        )
}

/**
 * 订阅 **ViewModelStoreOwner (例如 Activity 或 Fragment)** 作用域的事件。
 *
 * 该扩展函数基于当前 [ViewModelStoreOwner] 获取事件总线实例，并使用传入的 [lifecycleOwner] 管理订阅的生命周期。
 * 作用域事件的生命周期与其创建者 (Activity/Fragment) 绑定。
 *
 * @receiver ViewModelStoreOwner 提供 FlowEventBus 实例的作用域 (例如 Activity 或 Fragment)。
 * @param T 事件的数据类型。事件名默认为 T 的完整类名。
 * @param lifecycleOwner 用于管理订阅生命周期的所有者。
 * @param dispatcher 用于执行 [onReceived] lambda 的协程调度器，默认为主线程。
 * @param minLifecycleState 订阅开始收集所需的最小生命周期状态。
 * @param isSticky 事件是否为粘性事件。
 * @param onReceived 接收到事件时执行的回调函数。
 * @return Job 事件收集的 Job。
 */
@MainThread
inline fun <reified T> ViewModelStoreOwner.subscribeScopeEvent(
    lifecycleOwner: LifecycleOwner,
    dispatcher: CoroutineDispatcher = Dispatchers.Main.immediate,
    minLifecycleState: Lifecycle.State = Lifecycle.State.STARTED,
    isSticky: Boolean = false,
    noinline onReceived: (T) -> Unit
): Job {
    return ViewModelProvider(this).get(FlowEventBus::class.java)
        .subscribe(
            lifecycleOwner,
            T::class.java.name,
            minLifecycleState,
            dispatcher,
            isSticky,
            onReceived
        )
}

/**
 * 在独立的 **CoroutineScope** 内订阅 **Application (全局)** 作用域的事件。
 *
 * 此方法不依赖 Android [LifecycleOwner]，事件收集的生命周期由 [CoroutineScope] 自身管理。
 * 当 [CoroutineScope] 被取消时，订阅自动停止。适用于 ViewModel 或非 Android 组件。
 *
 * @receiver CoroutineScope 运行事件收集的协程作用域 (例如 viewModelScope)。
 * @param T 事件的数据类型。
 * @param isSticky 事件是否为粘性事件。
 * @param onReceived 接收到事件时执行的回调函数。
 * @return Job 事件收集的 Job。
 */
@MainThread
inline fun <reified T> CoroutineScope.subscribeGlobalEvent(
    isSticky: Boolean = false,
    noinline onReceived: (T) -> Unit
): Job = this.launch {
    ApplicationScopeViewModelProvider.get(FlowEventBus::class.java)
        .subscribeInScope(
            T::class.java.name,
            isSticky,
            onReceived
        )
}

/**
 * 在独立的 **CoroutineScope** 内订阅 **ViewModelStoreOwner** 作用域的事件。
 *
 * 此方法不依赖 Android [LifecycleOwner]，事件收集的生命周期由 [CoroutineScope] 自身管理。
 *
 * @receiver CoroutineScope 运行事件收集的协程作用域。
 * @param T 事件的数据类型。
 * @param scope 提供 FlowEventBus 实例的 [ViewModelStoreOwner] (例如 Activity 或 Fragment)。
 * @param isSticky 事件是否为粘性事件。
 * @param onReceived 接收到事件时执行的回调函数。
 * @return Job 事件收集的 Job。
 */
@MainThread
inline fun <reified T> CoroutineScope.subscribeScopeEvent(
    scope: ViewModelStoreOwner,
    isSticky: Boolean = false,
    noinline onReceived: (T) -> Unit
): Job = this.launch {
    ViewModelProvider(scope).get(FlowEventBus::class.java)
        .subscribeInScope(
            T::class.java.name,
            isSticky,
            onReceived
        )
}