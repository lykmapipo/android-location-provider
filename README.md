android-location-provider(WIP)
==============================

[![](https://jitpack.io/v/lykmapipo/android-location-provider.svg)](https://jitpack.io/#lykmapipo/android-location-provider)

A pack of helpful helpers to obtain location(s) from fused location provider.

## Installation
Add [https://jitpack.io](https://jitpack.io) to your build.gradle with:
```gradle
allprojects {
    repositories {
        maven { url "https://jitpack.io" }
    }
}
```
add `android-location-provider` dependency into your project

```gradle
dependencies {
    compile 'com.github.lykmapipo:android-location-provider:v0.1.0'
}
```

## Usage

In activity(or other component) request for last and location updates

```java
public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();
    private TextView tvLongitude;
    private TextView tvLatitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // request last known location
        tvLongitude = findViewById(R.id.tvLongitude);
        tvLatitude = findViewById(R.id.tvLatitude);
        Button btnRequestLastLocation = findViewById(R.id.btnRequestLastLocation);
        btnRequestLastLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LocationProvider.requestLastLocation(getApplicationContext(), new LocationProvider.OnLastLocationListener() {
                    @Override
                    public void onSuccess(Location location) {
                        Toast.makeText(MainActivity.this, "Location Success: " + location.toString(), Toast.LENGTH_SHORT).show();
                        double longitude = location.getLongitude();
                        double latitude = location.getLatitude();
                        tvLatitude.setText(String.format(Locale.ENGLISH, "%s: %f", "Latitude", latitude));
                        tvLongitude.setText(String.format(Locale.ENGLISH, "%s: %f", "Longitude", longitude));
                    }

                    @Override
                    public void onFailure(Exception error) {
                        Toast.makeText(MainActivity.this, "Location Failed: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }
}
```


## Test
```sh
./gradlew test
```

## Contribute
It will be nice, if you open an issue first so that we can know what is going on, then, fork this repo and push in your ideas.
Do not forget to add a bit of test(s) of what value you adding.

## License

(The MIT License)

Copyright (c) lykmapipo && Contributors

Permission is hereby granted, free of charge, to any person obtaining
a copy of this software and associated documentation files (the
'Software'), to deal in the Software without restriction, including
without limitation the rights to use, copy, modify, merge, publish,
distribute, sublicense, and/or sell copies of the Software, and to
permit persons to whom the Software is furnished to do so, subject to
the following conditions:

The above copyright notice and this permission notice shall be
included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED 'AS IS', WITHOUT WARRANTY OF ANY KIND,
EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY
CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT,
TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE
SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.