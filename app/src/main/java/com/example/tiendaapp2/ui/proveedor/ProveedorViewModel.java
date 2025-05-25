package com.example.tiendaapp2.ui.proveedor;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class ProveedorViewModel extends ViewModel {

    private final MutableLiveData<String> mText;

    public ProveedorViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is slideshow fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}