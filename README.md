基于 Kotlin Coroutines 和 Flows 实现的 FlowBus 是一个 Kotlin 事件总线。

FlowBus支持：Sticky、切换线程、多个订阅、延迟发送、生命周期感知、有序接收消息

 优点         | 详细解释                                                                                                                                                                                                                                                                                                                                          
------------|-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
 实现粘性效果  | MutableSharedFlow 构造函数中的 replay 参数是实现粘性事件的关键。当 replay 设置为 1 时（如您的 stickyEventFlows），Flow 会缓存最近发出的 1 个值。任何新的订阅者在开始收集时，会立即接收到这个缓存的值，即使该值是在订阅开始之前发布的。                                                                                                                                                                                            
 轻松切换线程  | via Coroutines,SharedFlow 的 collect 操作是在协程中进行的。在您的 subscribe 方法中，您使用了 lifecycleOwner.lifecycleScope.launch 并在 collect 内部再次使用 this.launch(dispatcher) 来处理接收到的事件。这意味着：* 收集 (Collect)：可以在任何线程（通常是主线程/Dispatchers.Main）上安全地开始/停止。* 处理 (Handle)：接收到数据后，可以轻松地切换到指定的 CoroutineDispatcher（例如 Dispatchers.IO 或 Dispatchers.Default）来执行耗时操作，避免阻塞 UI 线程。 
 多个订阅    | SharedFlow 是一种热流 (Hot Flow)，它支持多播（Multicast）。这意味着同一个 Flow 实例可以被多个 collect 调用同时订阅。当事件通过 tryEmit() 发布时，所有当前活跃的观察者都会同时接收到该事件。这与单播的 Flow（如 flow {  }）形成鲜明对比。                                                                                                                                                                                   
 自动清除事件  | (No Backlog without Observers),当一个 MutableSharedFlow 的 replay 设置为 0（如您的 normalEventFlows）时，如果没有活跃的订阅者在监听，发布事件时（通过 tryEmit 或 emit），该事件将直接被丢弃。这有效地避免了事件积压（Backlog）问题，防止应用在长时间无订阅者的情况下因事件过多而导致内存占用升高。                                                                                                                                            
 生命周期感知  | 这是 Android 官方推荐的 Flow 收集方式。它确保只有当 lifecycleOwner (如 Activity/Fragment) 的状态 大于或等于 startState 时，repeatOnLifecycle 块内的协程（即 collect 操作）才会被启动。                                                                                                                                                                                                     
 启动时机控制  | 您可以通过传入不同的 Lifecycle.State 来控制何时开始响应事件                                                                                                                                                                                                                                                                                                        
 协程暂停与恢复 | 当 lifecycleOwner 的状态低于 startState 时（例如从 STARTED 到 STOPPED），repeatOnLifecycle 会自动取消其内部的 collect 协程。当状态再次达到 startState 时，会自动重启一个新的 collect 协程。这完美地解决了屏幕旋转、进入后台等场景下的事件处理。                                                                                                                                                                        

## 发送实例
```kotlin
//Global Scope
postEvent(GlobalEvent("Test GlobalEvent"))
postEvent(GlobalEvent(value = "Test GlobalEvent"), 1000)

//Activity Scope
postEvent(requireActivity(), ActivityEvent("Test ActivityEvent"))
postEvent(requireActivity(), ActivityEvent("Test ActivityEvent"), 1000)

//Fragment Scope
postEvent(this@TestFragment, FragmentEvent("Test FragmentEvent"))
postEvent(this@TestFragment, FragmentEvent("Test FragmentEvent"), 1000)
```

## 订阅实例
```kotlin
//subscribeForever 指定coroutineScope
coroutineScope.subscribeGlobalEvent<GlobalEvent> {
    
}

//subscribe GlobalScopeEvent
subscribeScopeEvent<GlobalEvent> {
    
}
//subscribe ActivityScopeEvent
//In Activity
subscribeScopeEvent<ActivityEvent>(requireActivity()) {
    
}
//In Fragment
requireActivity().subscribeScopeEvent<ActivityEvent>(requireActivity()) {
    
}

//subscribe FragmentScopeEvent
subscribeScopeEvent<FragmentEvent>(fragment) {
    
}

```

## 切换线程实例
```kotlin
subscribeGlobalEvent<XEvent>(Dispatchers.IO) {
    
}
subscribeScopeEvent<XEvent>(Dispatchers.IO) {
    
}
```

## 感知生命周期实例

```kotlin
subscribeGlobalEvent<XEvent>(minLifecycleState = Lifecycle.State.RESUMED) {
    
}
subscribeScopeEvent<XEvent>(minLifecycleState = Lifecycle.State.RESUMED) {
    
}
```

## 粘性方式订阅实例

```kotlin
subscribeGlobalEvent<XEvent>(isSticky = true) {
    
}
subscribeScopeEvent<XEvent>(isSticky = true) {
    
}
```

## removeStickyEvent

```kotlin
//从全局总线中永久移除事件类型及其重放缓存
removeStickyEvent<XEvent>()
//从本地总线中永久移除事件类型及其重放缓存。
removeStickyEvent<XEvent>(fragment)
removeStickyEvent<XEvent>(activity)
```

- 自己控制取消监听
```kotlin
val job = observeEvent<Event> {
    
}
//取消监听
job.cancel()
```
## clearStickyEvent

```kotlin
//清除全局粘性事件类型 T 的重放缓存，但保留 Flow 实例。
clearStickyEvent<GlobalEvent>()
//清除本地粘性事件类型 T 的重放缓存，但保留 Flow 实例。
clearStickyEvent<GlobalEvent>(scope = this)
```

## 引入

### Gradle:

1. 在Project的 **build.gradle** 或 **setting.gradle** 中添加远程仓库

    ```gradle
    repositories {
        //
        mavenCentral()
    }
    ```

2. 在Module的 **build.gradle** 中添加依赖项
   [![Maven Central](https://img.shields.io/maven-central/v/io.github.logan0817/FlowBus.svg?label=Latest%20Release)](https://central.sonatype.com/artifact/io.github.logan0817/FlowBus)

    ```gradle
   implementation 'io.github.logan0817:FlowBus:1.0.0' // 替换为上方徽章显示的最新版本
    ```

### License

```
MIT License

Copyright (c) 2025 Logan Gan

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
```
