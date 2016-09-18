package com.angel.black.baskettogether.recruit.googlemap;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.angel.black.baframework.core.base.BaseActivity;
import com.angel.black.baframework.logger.BaLog;
import com.angel.black.baframework.security.PermissionConstants;
import com.angel.black.baframework.ui.dialog.DialogClickListener;
import com.angel.black.baframework.util.StringUtil;
import com.angel.black.baskettogether.R;
import com.angel.black.baskettogether.core.intent.IntentConst;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.List;
import java.util.Locale;

/**
 * Created by KimJeongHun on 2016-09-17.
 */
public class RecruitPostLocationMapActivity extends BaseActivity implements OnMapReadyCallback, GoogleMap.OnMapClickListener, GoogleMap.OnMarkerDragListener {
    private static final double SEOUL_CITY_HALL_LATITUDE = 37.566367;
    private static final double SEOUL_CITY_HALL_LONGITUDE = 126.977929;

    private static final int ANIMATE_CAMERA_SPEED = 300;
    private MapFragment mMapFragment;
    private GoogleMap mGoogleMap;
    private Marker mMarker;

    private double mLatitude;
    private double mLongitude;
    private String mAddress;

    private MapMode mMapMode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recruit_post_regist_map);

        mMapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
        mMapFragment.getMapAsync(this);

        Intent intent = getIntent();

        String mapMode = intent.getStringExtra(IntentConst.KEY_EXTRA_MAP_MODE);
        mapMode = StringUtil.isEmptyString(mapMode) ? MapMode.VIEW.toString() : mapMode;
        mMapMode = MapMode.valueOf(mapMode);

        mLatitude = intent.getDoubleExtra(IntentConst.KEY_EXTRA_MAP_LATITUDE, 0);
        mLongitude = intent.getDoubleExtra(IntentConst.KEY_EXTRA_MAP_LONGITUDE, 0);
        mAddress = intent.getStringExtra(IntentConst.KEY_EXTRA_MAP_ADDRESS);

        if(mMapMode == MapMode.VIEW) {
            setTitle(R.string.view_court_location);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (mMapMode == MapMode.SELECT_LOCATION) {
            getMenuInflater().inflate(R.menu.recruit_post_regist_map, menu);
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        if (item.getItemId() == R.id.menu_complete) {
            BaLog.d("완료 버튼 클릭!");

            if (mMarker == null) {
                showToast(R.string.error_not_picked_place);
                return true;
            }

            LatLng latLng = mMarker.getPosition();
            Intent returnData = new Intent();
            returnData.putExtra(IntentConst.KEY_EXTRA_MAP_LATITUDE, latLng.latitude); // 위도
            returnData.putExtra(IntentConst.KEY_EXTRA_MAP_LONGITUDE, latLng.longitude); // 경도
            returnData.putExtra(IntentConst.KEY_EXTRA_MAP_ADDRESS, getRegion(latLng.latitude, latLng.longitude)); // 경도
            setResult(RESULT_OK, returnData);
            finish();
        }

        return false;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        BaLog.i("pemissions=" + permissions.length + ", grantResults=" + grantResults.length);
        if (requestCode == PermissionConstants.REQUEST_LOCATION_PERMISSION) {
            if (grantResults.length != 1 || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                // 퍼미션 거부
                showAlertDialogNotCancelable(R.string.error_require_access_location_permission, new DialogClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                });
            } else {
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                        ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    mGoogleMap.setMyLocationEnabled(true);
                    moveMapLocation();
                }
            }
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mGoogleMap = googleMap;
        mGoogleMap.setOnMapClickListener(this);
        mGoogleMap.setOnMarkerDragListener(this);

        UiSettings settings = mGoogleMap.getUiSettings();
        settings.setZoomControlsEnabled(true);
        settings.setCompassEnabled(true);
        settings.setMyLocationButtonEnabled(true);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermission(Manifest.permission.ACCESS_COARSE_LOCATION,
                    PermissionConstants.REQUEST_LOCATION_PERMISSION,
                    R.string.request_access_location_permission, true);
            return;
        } else {
            mGoogleMap.setMyLocationEnabled(true);
            moveMapLocation();
        }
    }

    private void moveMapLocation() {
        if(isValidLocation(mLatitude, mLongitude)) {        // 이전에 설정된 위치가 있으면
            setMarker(new LatLng(mLatitude, mLongitude));
            showCurrentLocation(mLatitude, mLongitude);
        } else {                                            // 현재 GPS상 내위치 표시
            startMyLocationService();
        }
    }

    public void startMyLocationService() {
        LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        boolean bLocated = false;
        Location location = null;

        if (manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
//           showToast("GPS 인식 가능");
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            location = manager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            bLocated = (location != null && isValidLocation(location));
        }

        if(!bLocated && manager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
//            showToast("네트워크 위치 인식 가능");
            location = manager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            bLocated = true;
        }

        if(bLocated && location != null) {
            String msg = "Last Known Location -> Latitude : " + location.getLatitude() +
                    "\nLongitude : " + location.getLongitude();
            BaLog.i(TAG, msg);
//            showToast(msg);
            showCurrentLocation(location.getLatitude(), location.getLongitude());
        }
    }

    /**
     * 현재 위치를 지도상에 표시한다.
     * @param latitude
     * @param longitude
     */
    private void showCurrentLocation(final Double latitude, final Double longitude) {
        LatLng curPoint = new LatLng(latitude, longitude);

        mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(curPoint, 15), ANIMATE_CAMERA_SPEED, null);
        mGoogleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
    }

    private boolean isValidLocation(final Location location) {
        if(location.getLatitude() == 0.0 && location.getLongitude() == 0.0) {
            Log.e(TAG, "유효하지 않은 위치!! 0,0");
            return false;
        } else {
            return true;
        }
    }

    private boolean isValidLocation(final double latitude, final double longitude) {
        if(latitude == 0.0 && longitude == 0.0) {
            Log.e(TAG, "유효하지 않은 위치!! 0,0");
            return false;
        } else {
            return true;
        }
    }

    @Override
    public void onMapClick(LatLng latLng) {
        if(mMapMode != MapMode.SELECT_LOCATION)
            return;

        mGoogleMap.animateCamera(CameraUpdateFactory.newLatLng(latLng), ANIMATE_CAMERA_SPEED, null);
        setMarker(latLng);
    }

    private void setMarker(LatLng latLng) {
        if(mMarker != null) {
            mMarker.remove();
        }

        mMarker = mGoogleMap.addMarker(new MarkerOptions()
                .position(latLng)
                .draggable(mMapMode == MapMode.SELECT_LOCATION)
                .title(mAddress == null ? "농구장 위치" : mAddress));
    }

    /**
     * 위도와 경도를 토대로 지역명을 얻는다.
     * @param latitude
     * @param longitude
     * @return
     */
    private String getRegion(double latitude, double longitude) {
        StringBuilder sb = new StringBuilder();

        Geocoder geocoder = new Geocoder(this, Locale.getDefault());

        List<Address> list = null;

        try {
            list = geocoder.getFromLocation(latitude, longitude, 1);
        } catch(Exception e) {
            e.printStackTrace();
        }

        if(list == null || list.size() <= 0) {
            Log.e("getRegion", "지역 데이터 얻기 실패");
            return "unknown";
        } else {
            Address addr = list.get(0);
//            for(int i=0; i < addr.getMaxAddressLineIndex(); i++) {
//                sb.append(addr.getAddressLine(i)).append("\n");
//            }
//            sb.append(addr.getCountryName()).append(" ");      // 나라
            sb.append(addr.getAdminArea()).append(" ");        // 도
            sb.append(addr.getLocality()).append(" ");         // 시, 군
            sb.append(addr.getSubLocality()).append(" ");      // 구 (null 일수있음)
            sb.append(addr.getThoroughfare()).append(" ");     // 읍,동,도로명
            sb.append(addr.getSubThoroughfare()).append(" ");  // 번지수

//            sb.append(addr.getFeatureName()).append(" ");
//            sb.append(addr.getPremises()).append(" ");
//            sb.append(addr.getSubAdminArea()).append(" ");
//            sb.append(addr.getPostalCode()).append(" ");
        }

        BaLog.i("주소 >> \n" + sb.toString());

        return sb.toString().replace("null ", "");
    }

    @Override
    public void onMarkerDragStart(Marker marker) {

    }

    @Override
    public void onMarkerDrag(Marker marker) {

    }

    @Override
    public void onMarkerDragEnd(Marker marker) {
        mGoogleMap.animateCamera(CameraUpdateFactory.newLatLng(marker.getPosition()), ANIMATE_CAMERA_SPEED, null);
        setMarker(marker.getPosition());
    }

    public enum MapMode {
        VIEW, SELECT_LOCATION
    }
}
