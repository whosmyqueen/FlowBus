package com.logan.flowbus

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.ViewModelStoreOwner
/**
 * ApplicationScopeViewModelProvider
 *
 * @author logan
 * @email notwalnut@163.com
 * @date 2025/12/11
 */
object ApplicationScopeViewModelProvider : ViewModelStoreOwner {

    private val viewModelStoreInstance: ViewModelStore = ViewModelStore()

    override val viewModelStore: ViewModelStore
        get() = viewModelStoreInstance

    private val applicationProvider: ViewModelProvider by lazy {
        ViewModelProvider(ApplicationScopeViewModelProvider, ViewModelProvider.NewInstanceFactory())
    }

    fun <T : ViewModel> get(modelClass: Class<T>): T {
        return applicationProvider[modelClass]
    }

}