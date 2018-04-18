# PhoenixMarketCity Demo Powered By Intripper Android SDK

This is a sample app which demonstrates integration of Intripper SDK in an Android app.

The app has two projects. Main application (`app`) which contains the splash screen and Intripper SDK wrapper library(`intripperLibrary`). You can fork or download it and run the app to see it in action. If you need to implement the SDK in your own app then copy and add intripperLibrary module in your Android project and simply open `MapActivity` screen.

```
Intent intent;        
intent = new Intent(getApplicationContext(), MapActivity.class);
startActivity(intent);
```

You can download integration document from [here](https://api.intripper.com/docs/InTripper-Maps-SDK-Android.pdf)

