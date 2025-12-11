FlowBus: A Kotlin Event Bus Based on Kotlin Coroutines and Flows
FlowBus is a Kotlin Event Bus implemented using Kotlin Coroutines and Flows.

FlowBus supports the following features: Sticky Events, Thread Switching, Multiple Subscriptions,
Delayed Posting, Lifecycle Awareness, and Ordered Message Reception.

 Feature                             | Detailed Explanation                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                  
-------------------------------------|-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
 Implements Sticky Effect            | The replay parameter in the MutableSharedFlow constructor is key to implementing sticky events. When replay is set to 1 (as in your stickyEventFlows), the Flow caches the most recently emitted value (1 value). Any new subscriber immediately receives this cached value upon starting collection, even if the value was published before the subscription began.                                                                                                                                                                                                                                  
 Easy Thread Switching               | via Coroutines. SharedFlow's collect operation occurs within a Coroutine. In your subscribe method, you use lifecycleOwner.lifecycleScope.launch and then use this.launch(dispatcher) inside the collect block to process the received event. This means: * Collection (Collect): Can safely start/stop on any thread (typically the main thread/Dispatchers.Main). * Handling (Handle): After receiving data, you can easily switch to a specified CoroutineDispatcher (e.g., Dispatchers.IO or Dispatchers.Default) to execute time-consuming operations, preventing the blocking of the UI thread. 
 Multiple Subscriptions              | SharedFlow is a Hot Flow that supports Multicast. This means the same Flow instance can be subscribed to by multiple collect calls simultaneously. When an event is published via tryEmit(), all currently active observers receive the event at the same time. This contrasts sharply with a Unicast Flow (like flow { }).                                                                                                                                                                                                                                                                           
 Automatic Event Cleanup             | (No Backlog without Observers). When a MutableSharedFlow has replay set to 0 (as in your normalEventFlows), if there are no active subscribers listening, publishing an event (via tryEmit or emit) will cause the event to be discarded immediately. This effectively prevents the event backlog problem, stopping the application from increasing memory usage due to too many events when there are no subscribers for a long time.                                                                                                                                                                
 Lifecycle Awareness                 | This is the official Android recommended way to collect Flows. It ensures that the coroutine inside the repeatOnLifecycle block (i.e., the collect operation) is only launched when the lifecycleOwner's state (e.g., Activity/Fragment) is greater than or equal to startState.                                                                                                                                                                                                                                                                                                                      
 Control over Start Timing           | You can control when to start responding to events by passing a different Lifecycle.State.                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                            
 Coroutine Suspension and Resumption | When the lifecycleOwner's state drops below startState (e.g., from STARTED to STOPPED), repeatOnLifecycle automatically cancels its internal collect coroutine. When the state reaches startState again, a new collect coroutine is automatically restarted. This perfectly handles event processing in scenarios like screen rotations or entering the background.                                                                                                                                                                                                                                   

## post Examples

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

## subscribe Examples

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

## Thread Switching Example

```kotlin
subscribeGlobalEvent<XEvent>(Dispatchers.IO) {

}
subscribeScopeEvent<XEvent>(Dispatchers.IO) {

}
```

## Lifecycle Awareness Example

```kotlin
subscribeGlobalEvent<XEvent>(minLifecycleState = Lifecycle.State.RESUMED) {

}
subscribeScopeEvent<XEvent>(minLifecycleState = Lifecycle.State.RESUMED) {

}
```

## Sticky Subscription Example

```kotlin
subscribeGlobalEvent<XEvent>(isSticky = true) {

}
subscribeScopeEvent<XEvent>(isSticky = true) {

}
```

## removeStickyEvent

```kotlin
// Permanently remove the event type and its replay cache from the global bus.
removeStickyEvent<XEvent>()
// Permanently remove the event type and its replay cache from the local bus.
removeStickyEvent<XEvent>(fragment)
removeStickyEvent<XEvent>(activity)
```

- Self-controlled Listener Cancellation

```kotlin
val job = observeEvent<Event> {

}
// Cancel the listener
job.cancel()
```

## clearStickyEvent

```kotlin
// Clears the replay cache for the global sticky event type T, but keeps the Flow instance.
clearStickyEvent<GlobalEvent>()
// Clears the replay cache for the local sticky event type T, but keeps the Flow instance.
clearStickyEvent<GlobalEvent>(scope = this)
```

## Integration

### Gradle:

1. Add the remote repository to your Project's **build.gradle** or **setting.gradle**

    ```gradle
    repositories {
        //
        mavenCentral()
    }
    ```

2. Add the dependency to your Module's **build.gradle**
   [![Maven Central](https://img.shields.io/maven-central/v/io.github.logan0817/FlowBus.svg?label=Latest%20Release)](https://central.sonatype.com/artifact/io.github.logan0817/FlowBus)

    ```gradle
   implementation 'io.github.logan0817:FlowBus:1.0.0' // Replace with the latest version shown by the badge above
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
