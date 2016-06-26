package com.angel.black.baskettogether.core.view.recyclerview;

import android.view.ViewGroup;

import com.angel.black.baskettogether.util.MyLog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by KimJeongHun on 2016-06-12.
 */
public interface RecyclerViewAdapterData<T1 extends Object, T2 extends Object> {
    AbsRecyclerViewHolder createViewHolder(ViewGroup parent);

    void onBindViewHolder(AbsRecyclerViewHolder holder, int position, T2 data);

    RecyclerViewColletionData provideData();

    void populateList(T1 dataset);

    abstract class RecyclerViewColletionData<T1 extends Object, T2 extends Object> {
        public abstract int addDataset(T1 dataset);

        public abstract int length();

        public abstract long getItemId(int position);

        public abstract T2 getData(int position);

        public abstract void addData(T2 data);

        public abstract void removeData(int position);

        public abstract void setDataset(T1 dataset);
    }

    class JSONRecyclerViewCollectionData extends RecyclerViewColletionData<JSONArray, JSONObject> {
        private JSONArray mDataset = new JSONArray();

        @Override
        public int addDataset(JSONArray dataset) {
            int count = 0;
            try {
                for (int i = 0; i < dataset.length(); i++, count++) {
                    JSONObject jsonObject = dataset.getJSONObject(i);
                    mDataset.put(jsonObject);
                    MyLog.d("addDataset >> " + jsonObject);
                }

                return count;
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return 0;
        }

        @Override
        public int length() {
            return mDataset.length();
        }

        @Override
        public long getItemId(int position) {
            try {
                return ((JSONObject) mDataset.get(position)).optLong("id");
            } catch (JSONException e) {
                e.printStackTrace();
                return 0;
            }
        }

        @Override
        public JSONObject getData(int position) {
            try {
                return mDataset.getJSONObject(position);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        public void addData(JSONObject data) {
            mDataset.put(data);
        }

        @Override
        public void removeData(int position) {
            mDataset.remove(position);
        }

        @Override
        public void setDataset(JSONArray dataset) {
            mDataset = null;
            mDataset = dataset;
        }
    }
}
