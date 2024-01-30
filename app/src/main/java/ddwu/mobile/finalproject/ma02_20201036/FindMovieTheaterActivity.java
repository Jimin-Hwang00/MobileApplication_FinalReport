package ddwu.mobile.finalproject.ma02_20201036;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.libraries.places.api.model.PlaceTypes;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import ddwu.mobile.place.placebasic.OnPlaceBasicResult;
import ddwu.mobile.place.placebasic.PlaceBasicManager;
import ddwu.mobile.place.placebasic.pojo.PlaceBasic;

public class FindMovieTheaterActivity extends AppCompatActivity {
    final static String TAG = "FindMovieTheaterActivity";

    final int REQ_PERMISSION_CODE = 300;

    private GoogleMap mGoogleMap;
    Marker selectedMarker;

    FusedLocationProviderClient flpClient;

    EditText searchPlaceEditText;
    Button searchPlaceBtn;

    private PlaceBasicManager placeBasicManager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.find_movie_theater_layout);

        searchPlaceEditText = findViewById(R.id.searchPlaceEditText);
        searchPlaceBtn = findViewById(R.id.searchPlaceBtn);

        checkPermission();

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(mapReadyCallback);

        flpClient = LocationServices.getFusedLocationProviderClient(this);

        placeBasicManager = new PlaceBasicManager(getString(R.string.google_api_key));
        placeBasicManager.setOnPlaceBasicResult(onPlaceBasicResult);
    }

    class ReverseGeoTask extends AsyncTask<String, Void, List<Address>> {
        Geocoder geocoder = new Geocoder(FindMovieTheaterActivity.this, Locale.getDefault());

        @Override
        protected List<Address> doInBackground(String... strings) {
            List<Address> addresses = null;
            try {
                addresses = geocoder.getFromLocationName(strings[0], 1);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return addresses;
        }

        @Override
        protected void onPostExecute(List<Address> addresses) {
            if (addresses == null) {
                Toast.makeText(FindMovieTheaterActivity.this, "검색 결과가 없습니다. 다시 입력해주세요.", Toast.LENGTH_SHORT).show();
            } else {
                double lat = addresses.get(0).getLatitude();
                double lng = addresses.get(0).getLongitude();

                LatLng latLng = new LatLng(lat, lng);
                mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 17));

                placeBasicManager.searchPlaceBasic(lat, lng, 150, PlaceTypes.MOVIE_THEATER);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQ_PERMISSION_CODE:
                if (grantResults.length > 0 &&
                        grantResults[0] == PackageManager.PERMISSION_GRANTED &&
                        grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "위치권한 획득 완료", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "위치권한 미획득", Toast.LENGTH_SHORT).show();
                }
        }
    }

    private void checkPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                    checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show();
            } else {
                requestPermissions(new String[]{
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.ACCESS_FINE_LOCATION}, REQ_PERMISSION_CODE);
            }
        }
    }

    OnMapReadyCallback mapReadyCallback = new OnMapReadyCallback() {
        @Override
        public void onMapReady(@NonNull GoogleMap googleMap) {
            mGoogleMap = googleMap;

            Intent intent = getIntent();
            double lat = intent.getDoubleExtra("lat", 37.606320);
            double lng = intent.getDoubleExtra("lng", 127.041808);

            LatLng latLng = new LatLng(lat, lng);
            mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 17));

            mGoogleMap.setMyLocationEnabled(true);
            mGoogleMap.setOnInfoWindowLongClickListener(infoWindowLongClickListener);

            placeBasicManager.searchPlaceBasic(lat, lng, 150, PlaceTypes.MOVIE_THEATER);

            mGoogleMap.setOnMyLocationButtonClickListener(new GoogleMap.OnMyLocationButtonClickListener() {
                @Override
                public boolean onMyLocationButtonClick() {
                    flpClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            placeBasicManager.searchPlaceBasic(location.getLatitude(), location.getLongitude(), 150, PlaceTypes.MOVIE_THEATER);
                        }
                    });
                    return false;
                }
            });
        }
    };

    OnPlaceBasicResult onPlaceBasicResult = new OnPlaceBasicResult() {
        @Override
        public void onPlaceBasicResult(List<PlaceBasic> list) {
            for (PlaceBasic place : list) {
                BitmapDrawable bitmapDrawable = (BitmapDrawable) getResources().getDrawable(R.mipmap.black_marker);
                Bitmap b = bitmapDrawable.getBitmap();
                Bitmap smallBlackMarker = Bitmap.createScaledBitmap(b, 200, 200, false);

                MarkerOptions options = new MarkerOptions()
                        .title(place.getName())
                        .position(new LatLng(place.getLatitude(), place.getLongitude()))
                        .icon(BitmapDescriptorFactory.fromBitmap(smallBlackMarker));
                Marker marker = mGoogleMap.addMarker(options);
                /*현재 장소의 place_id 를 각각의 마커에 보관*/
                marker.setTag(place.getPlaceId());
            }
        }
    };

    GoogleMap.OnInfoWindowLongClickListener infoWindowLongClickListener = new GoogleMap.OnInfoWindowLongClickListener() {
        @Override
        public void onInfoWindowLongClick(@NonNull Marker marker) {
            BitmapDrawable bitmapDrawable = (BitmapDrawable) getResources().getDrawable(R.mipmap.red_marker);
            Bitmap b = bitmapDrawable.getBitmap();
            Bitmap smallRedMarker = Bitmap.createScaledBitmap(b, 200, 200, false);

            selectedMarker = marker;
            selectedMarker.setIcon(BitmapDescriptorFactory.fromBitmap(smallRedMarker));
        }
    };

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.selectTheaterBtn:
                if (selectedMarker == null) {
                    Toast.makeText(FindMovieTheaterActivity.this, "영화관을 선택해주세요.", Toast.LENGTH_SHORT).show();
                } else {
                    Log.d(TAG, "select movie theater");
                    Intent resultIntent = new Intent();
                    resultIntent.putExtra("lat", selectedMarker.getPosition().latitude);
                    resultIntent.putExtra("lng", selectedMarker.getPosition().longitude);
                    resultIntent.putExtra("movieTheaterName", selectedMarker.getTitle());
                    setResult(RESULT_OK, resultIntent);

                    finish();
                }
                break;
            case R.id.searchPlaceBtn:
                String place;
                if ((place = searchPlaceEditText.getText().toString()) == "") {
                    Toast.makeText(FindMovieTheaterActivity.this, "지역명을 작성해주세요.", Toast.LENGTH_SHORT).show();
                } else {
                    new ReverseGeoTask().execute(searchPlaceEditText.getText().toString());
                }
                break;
        }
    }
}
