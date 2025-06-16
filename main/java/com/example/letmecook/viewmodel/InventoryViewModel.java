package com.example.letmecook.viewmodel;

import android.app.Application;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import com.example.letmecook.db.entity.InventoryItem;
import com.example.letmecook.repository.InventoryRepository;
import java.util.List;

public class InventoryViewModel extends AndroidViewModel {

    private final InventoryRepository mRepository;
    private final LiveData<List<InventoryItem>> mAllItems;

    public InventoryViewModel(Application application) {
        super(application);
        mRepository = new InventoryRepository(application);
        mAllItems = mRepository.getAllItems();
    }

    public LiveData<List<InventoryItem>> getAllItems() {
        return mAllItems;
    }

    public void insert(InventoryItem item) {
        mRepository.insert(item);
    }

    public void delete(InventoryItem item) {
        mRepository.delete(item);
    }
}