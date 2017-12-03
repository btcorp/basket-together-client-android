package com.blackangel.baskettogether;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;

/**
 * Created by kimjeonghun on 2017. 12. 3..
 */

public class AppViewModel extends AndroidViewModel {
    private LiveData<Long> mSessionId;

    public AppViewModel(@NonNull Application application) {
        super(application);
    }

    public LiveData<Long> getSessionId() {
        if(mSessionId == null) {
            mSessionId = new MutableLiveData<>();
        }
        return mSessionId;
    }
}
