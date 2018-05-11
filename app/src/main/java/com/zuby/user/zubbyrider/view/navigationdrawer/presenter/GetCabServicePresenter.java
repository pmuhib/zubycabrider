package com.zuby.user.zubbyrider.view.navigationdrawer.presenter;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.google.gson.Gson;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.zuby.user.zubbyrider.interfaces.ResultInterface;
import com.zuby.user.zubbyrider.utils.ApiKeys;
import com.zuby.user.zubbyrider.utils.PreferenceConnector;
import com.zuby.user.zubbyrider.view.navigationdrawer.model.CarServiceModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class GetCabServicePresenter {
    private ResultInterface mResultInterface;
    private Context mContext;
    private final String TAG = GetCabServicePresenter.class.getSimpleName();
    String mData;

    public void show(ResultInterface resultInterface, Context context) {
        mContext = context;
        mResultInterface = resultInterface;
        HashMap hashMap = new HashMap();
        hashMap.put("tokenid", PreferenceConnector.readString(mContext, ApiKeys.TOKEN, ""));
        Log.e(TAG, new Gson().toJson(hashMap));
        addService(new Gson().toJson(hashMap));
    }

    public void addService(String data) {
        mData = data;
        new MainClass().execute();
    }

    class MainClass extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // display a progress dialog for good user experiance
        }

        @Override
        protected String doInBackground(String... params) {
            String current = "";
            try {
                try {
                    OkHttpClient client = new OkHttpClient();
                    com.squareup.okhttp.MediaType mediaType = com.squareup.okhttp.MediaType.parse("application/x-www-form-urlencoded");
                    com.squareup.okhttp.RequestBody body = com.squareup.okhttp.RequestBody.create(mediaType, mData);
                    Request request = new Request.Builder()
                            .url("http://ec2-52-64-109-74.ap-southeast-2.compute.amazonaws.com/car_service/byAggregationCarCategories")
                            .post(body)
                            .addHeader("content-type", "application/x-www-form-urlencoded")
                            .build();
                    com.squareup.okhttp.Response response = client.newCall(request).execute();
                    String jsonData = response.body().string();
                    JSONObject Jobject = new JSONObject(jsonData);
                    System.out.println(Jobject.toString());
                    current = Jobject.toString();
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                }
            } catch (Exception e) {
                e.printStackTrace();
                return "Exception: " + e.getMessage();
            }
            return current;
        }

        @Override
        protected void onPostExecute(String s) {
            Log.e(TAG, s);
            try {
                JSONObject oneObject = new JSONObject(s);
                Log.e("opme", "" + oneObject);
                String type = oneObject.getString("type");
                if (type.equalsIgnoreCase("success")) {
                    CarServiceModel carServiceModel = new CarServiceModel();
                    carServiceModel.setMessage(oneObject.getString("message"));
                    carServiceModel.setType(oneObject.getString("type"));
                    String cardata = oneObject.getString("data");
                    JSONObject jsonObject = new JSONObject(cardata);
                    String datastring = jsonObject.getString("details");
                    JSONArray array = new JSONArray(datastring);
                    List<CarServiceModel.DataBean.DetailsBean> detailsBeans = new ArrayList<>();
                    for (int i = 0; i < array.length(); i++) {
                        CarServiceModel.DataBean.DetailsBean detailsBean = new CarServiceModel.DataBean.DetailsBean();
                        JSONObject jsonObject1 = (JSONObject) array.get(i);
                        int aggregaitonid = jsonObject1.getInt("AggregationId");
                        String aggregationname = jsonObject1.getString("AggregationName");
                        String array1 = jsonObject1.getString("array");
                        JSONArray jsonArray = new JSONArray(array1);
                        List<CarServiceModel.DataBean.DetailsBean.ArrayBean> arrayBeans = new ArrayList<>();
                        for (int j = 0; j < jsonArray.length(); j++) {
                            JSONObject jsonObject2 = (JSONObject) jsonArray.get(j);
                            CarServiceModel.DataBean.DetailsBean.ArrayBean arrayBean = new CarServiceModel.DataBean.DetailsBean.ArrayBean();
                            //Log.e("pbke", "" + jsonObject2);
                            // {"car_category_id":1,"category_name":"Small","cat_aggregation_id":1,"max_no_of_seats":5}
                            int car_category_id = jsonObject2.getInt("car_category_id");
                            String category_name = jsonObject2.getString("category_name");
                            int cat_aggregation_id = jsonObject2.getInt("cat_aggregation_id");
                            int max_no_of_seats = jsonObject2.getInt("max_no_of_seats");
                            arrayBean.setCar_category_id(car_category_id);
                            arrayBean.setCat_aggregation_id(cat_aggregation_id);
                            arrayBean.setCategory_name(category_name);
                            arrayBean.setMax_no_of_seats(max_no_of_seats);
                            arrayBeans.add(arrayBean);
                            Log.e("category", category_name);
                        }
                        detailsBean.setAggregationId(aggregaitonid);
                        detailsBean.setAggregationName(aggregationname);
                        detailsBean.setArray(arrayBeans);
                        detailsBeans.add(detailsBean);
                    }
                    CarServiceModel.DataBean dataBean = new CarServiceModel.DataBean();
                    dataBean.setDetails(detailsBeans);
                    carServiceModel.setData(dataBean);
                    mResultInterface.onSuccess(carServiceModel);
                } else {
                    mResultInterface.onFailed(oneObject.getString("message"));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }
}