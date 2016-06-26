package com.angel.black.baskettogether.core.view.recyclerview;

import android.view.ViewGroup;

import com.angel.black.baskettogether.util.MyLog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by KimJeongHun on 2016-06-12.
 */
public interface RecyclerViewAdapterData {
    AbsRecyclerViewHolder createViewHolder(ViewGroup parent);

    void onBindViewHolder(AbsRecyclerViewHolder holder, int position, Object data);

    RecyclerViewColletionData provideData();

    interface RecyclerViewColletionData {
        int addDataset(Object dataset);

        int length();

        long getItemId(int position);

        Object getData(int position);

        void addData(Object data);

        void removeData(int position);

        void setDataset(Object dataset);
    }

    class JSONRecyclerViewCollectionData implements RecyclerViewColletionData {
        private JSONArray mDataset = new JSONArray();

        @Override
        public int addDataset(Object dataset) {
            if(dataset instanceof JSONArray) {
                JSONArray jsonArray = (JSONArray) dataset;
                int count = 0;
                try {
                    for (int i = 0; i < jsonArray.length(); i++, count++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        mDataset.put(jsonObject);
                        MyLog.d("addDataset >> " + jsonObject);
                    }

                    return count;
                } catch (JSONException e) {
                    e.printStackTrace();
                }
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
        public Object getData(int position) {
            try {
                return mDataset.getJSONObject(position);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        public void addData(Object data) {
            mDataset.put((JSONObject) data);
        }

        @Override
        public void removeData(int position) {
            mDataset.remove(position);
        }

        @Override
        public void setDataset(Object dataset) {
            mDataset = null;
            if(dataset instanceof JSONArray) {
                mDataset = (JSONArray) dataset;
            }
        }
    }
}
