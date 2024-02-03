package com.snnafi.variable_app_icon

import android.content.ComponentName;
import android.content.Context;
import android.content.pm.PackageManager;
import android.util.Log;

import androidx.annotation.NonNull

import io.flutter.embedding.engine.plugins.FlutterPlugin
import io.flutter.embedding.engine.plugins.activity.ActivityAware
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding
import io.flutter.embedding.android.FlutterActivity
import io.flutter.plugin.common.BinaryMessenger
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugin.common.MethodChannel.MethodCallHandler
import io.flutter.plugin.common.MethodChannel.Result


/** VariableAppIconPlugin */
class VariableAppIconPlugin : FlutterPlugin, MethodCallHandler, ActivityAware {
    private var activity: FlutterActivity? = null
    private lateinit var channel: MethodChannel

    override fun onAttachedToEngine(@NonNull flutterPluginBinding: FlutterPlugin.FlutterPluginBinding) {
        channel = MethodChannel(flutterPluginBinding.binaryMessenger, "variable_app_icon")
        channel.setMethodCallHandler(this)
    }

    override fun onMethodCall(@NonNull call: MethodCall, @NonNull result: Result) {
        if (call.method == "changeAppIcon") {
            var iconId: String = call.argument<String>("androidIconId")!!
            val iconIds: List<String> = call.argument<List<String>>("androidIcons")!!
            val ctx: Context = activity?.applicationContext ?: return result.error("NO_ACTIVITY", "Activity not attached", null)
            val pm: PackageManager = ctx.packageManager
            for (i in iconIds) {
                pm.setComponentEnabledSetting(
                    ComponentName(ctx.packageName, i),
                    if (i == iconId) PackageManager.COMPONENT_ENABLED_STATE_ENABLED else PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                    PackageManager.DONT_KILL_APP
                )
            }
            result.success(null)
        } else {
            result.notImplemented()
        }
    }

    override fun onAttachedToActivity(binding: ActivityPluginBinding) {
        this.activity = binding.activity as? FlutterActivity
        // MethodChannel is already initialized in onAttachedToEngine.
    }

    override fun onDetachedFromEngine(@NonNull binding: FlutterPlugin.FlutterPluginBinding) {
        channel.setMethodCallHandler(null)
    }

    override fun onDetachedFromActivity() {
        // Clean up references to prevent memory leaks.
        activity = null
    }

    override fun onDetachedFromActivityForConfigChanges() {
        // Clean up references to prevent memory leaks.
        activity = null
    }

    override fun onReattachedToActivityForConfigChanges(binding: ActivityPluginBinding) {
        this.activity = binding.activity as? FlutterActivity
    }
}
