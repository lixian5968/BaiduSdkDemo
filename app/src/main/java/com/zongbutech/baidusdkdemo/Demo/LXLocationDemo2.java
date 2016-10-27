package com.zongbutech.baidusdkdemo.Demo;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.InfoWindow;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.PoiInfo;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeOption;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.zongbutech.baidusdkdemo.R;

import java.util.List;

public class LXLocationDemo2 extends Activity {

    LocationClient mLocClient;
    public MyLocationListenner myListener = new MyLocationListenner();

    MapView mMapView;
    BaiduMap mBaiduMap;

    boolean isFirstLoc = true;
    float Radiu = 30;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SDKInitializer.initialize(getApplicationContext());
        setContentView(R.layout.activity_location_lx);


        mMapView = (MapView) findViewById(R.id.bmapView);
        mBaiduMap = mMapView.getMap();
        mBaiduMap.setMyLocationEnabled(true);
        mLocClient = new LocationClient(this);
        mLocClient.registerLocationListener(myListener);
        mBaiduMap.setOnMapClickListener(mOnMapClickListener);


        LocationClientOption option = new LocationClientOption();
        option.setOpenGps(true);
        option.setCoorType("bd09ll");
        option.setScanSpan(1000);
        mLocClient.setLocOption(option);
        mLocClient.start();
    }

    double latitude = 30.287049280917927;
    double longitud = 120.12777277744277;
    public class MyLocationListenner implements BDLocationListener {

        @Override
        public void onReceiveLocation(BDLocation location) {
            if (location == null || mMapView == null) {
                return;
            }


            if (isFirstLoc) {
                Radiu = location.getRadius();
                isFirstLoc = false;

                if(latitude>0 &&longitud>0 ){
                    LatLng point = new LatLng(latitude, longitud);
                    setMapOverlay(point);
                }else{
                    LatLng point = new LatLng(location.getLatitude(), location.getLongitude());
                    setMapOverlay(point);
                }
            }
        }

        public void onReceivePoi(BDLocation poiLocation) {
            Log.e("lx", "");
        }
    }

    BaiduMap.OnMapClickListener mOnMapClickListener = new BaiduMap.OnMapClickListener() {
        public void onMapClick(LatLng point) {
            Log.e("lx", "");
            setMapOverlay(point);
        }

        public boolean onMapPoiClick(MapPoi poi) {
            setMapOverlay(poi.getPosition());
            return false;
        }
    };


    private void setMapOverlay(LatLng point) {
        LatLng ll = new LatLng(point.latitude, point.longitude);
        MapStatus.Builder builder = new MapStatus.Builder();
        builder.target(ll).zoom(19.0f);
        mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(builder.build()));

        mBaiduMap.clear();
        BitmapDescriptor bitmapDescriptor = BitmapDescriptorFactory.fromResource(R.drawable.icon_gcoding);
        MarkerOptions mMarkerOptions = new MarkerOptions().position(point);
        OverlayOptions options = mMarkerOptions.icon(bitmapDescriptor);
        //添加mark
        Marker marker = (Marker) (mBaiduMap.addOverlay(options));//地图上添加mark
        //弹出View(气泡，意即在地图中显示一个信息窗口)，显示当前mark位置信息
        setPopupTipsInfo(marker);


//        getInfoFromLAL(point);
    }

    private static final String TAG = "MainActivity";   //日志的TAG

    //想根据Mark中的经纬度信息，获取当前的位置语义化结果，需要使用地理编码查询和地理反编码请求
    //在地图中显示一个信息窗口
    private void setPopupTipsInfo(Marker marker) {
        //获取当前经纬度信息
        final LatLng latLng = marker.getPosition();
        final String[] addr = new String[1];
        //实例化一个地理编码查询对象
        GeoCoder geoCoder = GeoCoder.newInstance();
        //设置反地理编码位置坐标
        ReverseGeoCodeOption option = new ReverseGeoCodeOption();
        option.location(latLng);
        //发起反地理编码请求
        geoCoder.reverseGeoCode(option);
        //为地理编码查询对象设置一个请求结果监听器
        geoCoder.setOnGetGeoCodeResultListener(new OnGetGeoCoderResultListener() {
            @Override
            public void onGetGeoCodeResult(GeoCodeResult geoCodeResult) {

                Log.d(TAG, "地理编码信息 ---> \nAddress : " + geoCodeResult.getAddress()
                        + "\ntoString : " + geoCodeResult.toString()
                        + "\ndescribeContents : " + geoCodeResult.describeContents());
            }

            //当获取到反编码信息结果的时候会调用
            @Override
            public void onGetReverseGeoCodeResult(ReverseGeoCodeResult reverseGeoCodeResult) {
                //获取地理反编码位置信息
                addr[0] = reverseGeoCodeResult.getAddress();
                //获取地址的详细内容对象，此类表示地址解析结果的层次化地址信息。
                ReverseGeoCodeResult.AddressComponent addressDetail = reverseGeoCodeResult.getAddressDetail();
                Log.d(TAG, "反地理编码信息 ---> \nAddress : " + addr[0]
                        + "\nBusinessCircle : " + reverseGeoCodeResult.getBusinessCircle()//位置所属商圈名称
                        + "\ncity : " + addressDetail.city  //所在城市名称
                        + "\ndistrict : " + addressDetail.district  //区县名称
                        + "\nprovince : " + addressDetail.province  //省份名称
                        + "\nstreet : " + addressDetail.street      //街道名
                        + "\nstreetNumber : " + addressDetail.streetNumber);//街道（门牌）号码

                StringBuilder poiInfoBuilder = new StringBuilder();
                //poiInfo信息
                List<PoiInfo> poiInfoList = reverseGeoCodeResult.getPoiList();
                if (poiInfoList != null) {
                    poiInfoBuilder.append("\nPoilist size : " + poiInfoList.size());
                    for (PoiInfo p : poiInfoList) {
                        poiInfoBuilder.append("\n\taddress: " + p.address);//地址信息
                        poiInfoBuilder.append(" name: " + p.name + " postCode: " + p.postCode);//名称、邮编
                        //还有其他的一些信息，我这里就不打印了，请参考API
                    }
                }
                Log.d(TAG, "poiInfo --> " + poiInfoBuilder.toString());

                //动态创建一个View用于显示位置信息
                Button button = new Button(getApplicationContext());
                //设置view是背景图片
//                button.setBackgroundResource(R.drawable.location_tips);
                //设置view的内容（位置信息）
                button.setText(addr[0]);
                //在地图中显示一个信息窗口，可以设置一个View作为该窗口的内容，也可以设置一个 BitmapDescriptor 作为该窗口的内容
                InfoWindow infoWindow = new InfoWindow(BitmapDescriptorFactory.fromView(button), latLng, -47, new InfoWindow.OnInfoWindowClickListener() {
                    @Override
                    public void onInfoWindowClick() {
                        //当InfoWindow被点击后隐藏
                        mBaiduMap.hideInfoWindow();
                    }
                });
                //InfoWindow infoWindow = new InfoWindow(button, latLng, -47);
                //显示信息窗口
                mBaiduMap.showInfoWindow(infoWindow);
            }
        });
        /*不能放在下面 因为可能得到的数据还没来得及回调
        Button button = new Button(getApplicationContext());
        button.setBackgroundResource(R.drawable.location_tips);
        button.setText(addr[0]);
        //InfoWindow infoWindow = new InfoWindow(BitmapDescriptorFactory.fromView(button),latLng,-47,null);
        InfoWindow infoWindow = new InfoWindow(button,latLng,-47);
        baiduMap.showInfoWindow(infoWindow);
        */
        return;
    }


    private void getInfoFromLAL(final LatLng point) {
        GeoCoder gc = GeoCoder.newInstance();
        gc.reverseGeoCode(new ReverseGeoCodeOption().location(point));
        gc.setOnGetGeoCodeResultListener(new OnGetGeoCoderResultListener() {
            @Override
            public void onGetReverseGeoCodeResult(ReverseGeoCodeResult result) {
                if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
                } else {
                    Log.e("lx", "latitudeE6" + point.latitudeE6 + "latitudeE6" + point.latitudeE6
                            + "\n" + result.getAddress());
                }
            }

            @Override
            public void onGetGeoCodeResult(GeoCodeResult result) {

            }
        });
    }

    @Override
    protected void onPause() {
        mMapView.onPause();
        super.onPause();
    }

    @Override
    protected void onResume() {
        mMapView.onResume();
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        mLocClient.stop();
        mBaiduMap.setMyLocationEnabled(false);
        mMapView.onDestroy();
        mMapView = null;
        super.onDestroy();
    }

}
