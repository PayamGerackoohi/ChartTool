# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile

-keepclasseswithmembers class com.payam1991gr.chart.tool.ChartView { public *; }
-keepclasseswithmembers class com.payam1991gr.chart.tool.ChartLegend { public *; }
-keepclasseswithmembers class com.payam1991gr.chart.tool.ChartCategory { public *; }
-keepclasseswithmembers class com.payam1991gr.chart.tool.ChartLabel { public *; }
-keepclasseswithmembers class com.payam1991gr.chart.tool.ChartTooltip { public *; }
-keepclasseswithmembers class com.payam1991gr.chart.tool.data.** { public *; }
-keepclasseswithmembers class com.payam1991gr.chart.tool.ICTWidgetParent { *; }
-keepclasseswithmembers class com.payam1991gr.chart.tool.IRendererParent { *; }

#-keep,allowoptimization class com.payam1991gr.chart.tool.ChartView { public *; }
#-keep,allowoptimization class com.payam1991gr.chart.tool.ChartLegend { public *; }
#-keep,allowoptimization class com.payam1991gr.chart.tool.ChartCategory { public *; }
#-keep,allowoptimization class com.payam1991gr.chart.tool.ChartLabel { public *; }
#-keep,allowoptimization class com.payam1991gr.chart.tool.ChartTooltip { public *; }
#-keep,allowoptimization class com.payam1991gr.chart.tool.data.** { public *; }
