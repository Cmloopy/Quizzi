package com.cmloopy.quizzi.data.manager;

import android.content.Context;
import android.widget.Toast;

import com.cmloopy.quizzi.R;
import com.cmloopy.quizzi.data.RetrofitClient;
import com.cmloopy.quizzi.data.api.CollectionApi;
import com.cmloopy.quizzi.models.CollectionModel;
import com.cmloopy.quizzi.models.HomeCollection;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CollectionDataManager {
    private static CollectionDataManager instance;
    private List<CollectionModel> cachedCollections = new ArrayList<>();
    private List<HomeCollection> cachedHomeCollections = new ArrayList<>();
    private boolean dataLoaded = false;

    private CollectionDataManager() {
        // Constructor riêng tư
    }

    public static CollectionDataManager getInstance() {
        if (instance == null) {
            instance = new CollectionDataManager();
        }
        return instance;
    }

    public interface OnCollectionsLoadedListener {
        void onCollectionsLoaded(List<HomeCollection> collections);
        void onError(String message);
    }

    public void loadTopCollections(final Context context, final OnCollectionsLoadedListener listener) {
        // Nếu đã có cache, sử dụng cache
        if (dataLoaded && !cachedHomeCollections.isEmpty()) {
            if (listener != null) {
                listener.onCollectionsLoaded(cachedHomeCollections);
            }
            return;
        }

        // Nếu chưa có cache, tải từ API
        CollectionApi collectionApi = RetrofitClient.getCollectionApi();
        Call<List<CollectionModel>> call = collectionApi.getAllCollections();
        call.enqueue(new Callback<List<CollectionModel>>() {
            @Override
            public void onResponse(Call<List<CollectionModel>> call, Response<List<CollectionModel>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<CollectionModel> collections = response.body();

                    // Lưu vào cache
                    cachedCollections.clear();
                    cachedCollections.addAll(collections);

                    // Lọc chỉ các collection có thể hiển thị công khai
                    List<CollectionModel> visibleCollections = new ArrayList<>();
                    for (CollectionModel collection : collections) {
                        if (collection.isVisibleTo()) {
                            visibleCollections.add(collection);
                        }
                    }

                    // Sắp xếp theo timestamp (mới nhất lên đầu)
                    Collections.sort(visibleCollections, new Comparator<CollectionModel>() {
                        @Override
                        public int compare(CollectionModel c1, CollectionModel c2) {
                            return c2.getTimestamp().compareTo(c1.getTimestamp());
                        }
                    });

                    // Lấy 5 phần tử đầu hoặc ít hơn cho home view
                    int limit = Math.min(5, visibleCollections.size());

                    if (visibleCollections.size() > 0) {
                        List<CollectionModel> topCollections = visibleCollections.subList(0, limit);

                        // Chuyển đổi sang model UI
                        cachedHomeCollections.clear();
                        for (CollectionModel collection : topCollections) {
                            // Sử dụng hình ảnh mặc định nếu coverPhoto là null
                            int imageRes = R.drawable.ic_launcher_background;

                            HomeCollection homeCollection = new HomeCollection(
                                    imageRes,
                                    collection.getCategory()
                            );

                            // Đặt ID nếu có phương thức setId
                            try {
                                java.lang.reflect.Method setIdMethod = homeCollection.getClass().getMethod("setId", int.class);
                                setIdMethod.invoke(homeCollection, collection.getId());
                            } catch (Exception e) {
                                // Bỏ qua nếu không có phương thức setId
                            }

                            cachedHomeCollections.add(homeCollection);
                        }

                        dataLoaded = true;

                        // Gọi callback
                        if (listener != null) {
                            listener.onCollectionsLoaded(cachedHomeCollections);
                        }
                    } else {
                        // Không có collections hiển thị
                        if (listener != null) {
                            List<HomeCollection> sampleCollections = createSampleCollections();
                            cachedHomeCollections.clear();
                            cachedHomeCollections.addAll(sampleCollections);
                            listener.onCollectionsLoaded(sampleCollections);
                        }
                    }
                } else {
                    // Gọi callback với thông báo lỗi
                    if (listener != null) {
                        listener.onError("Không thể tải dữ liệu collections");
                        List<HomeCollection> sampleCollections = createSampleCollections();
                        cachedHomeCollections.clear();
                        cachedHomeCollections.addAll(sampleCollections);
                        listener.onCollectionsLoaded(sampleCollections);
                    }
                }
            }

            @Override
            public void onFailure(Call<List<CollectionModel>> call, Throwable t) {
                // Gọi callback với thông báo lỗi
                if (listener != null) {
                    listener.onError("Lỗi mạng: " + t.getMessage());
                    List<HomeCollection> sampleCollections = createSampleCollections();
                    cachedHomeCollections.clear();
                    cachedHomeCollections.addAll(sampleCollections);
                    listener.onCollectionsLoaded(sampleCollections);
                }
            }
        });
    }

    // Lấy tất cả các collection (cho màn hình TopCollections)
    public void loadAllCollections(final Context context, final OnCollectionsLoadedListener listener) {
        // Tải từ API, ngay cả khi đã có cache (để đảm bảo dữ liệu mới nhất)
        CollectionApi collectionApi = RetrofitClient.getCollectionApi();
        Call<List<CollectionModel>> call = collectionApi.getAllCollections();
        call.enqueue(new Callback<List<CollectionModel>>() {
            @Override
            public void onResponse(Call<List<CollectionModel>> call, Response<List<CollectionModel>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<CollectionModel> collections = response.body();

                    // Lưu vào cache
                    cachedCollections.clear();
                    cachedCollections.addAll(collections);

                    // Lọc chỉ các collection có thể hiển thị công khai
                    List<CollectionModel> visibleCollections = new ArrayList<>();
                    for (CollectionModel collection : collections) {
                        if (collection.isVisibleTo()) {
                            visibleCollections.add(collection);
                        }
                    }

                    // Sắp xếp theo timestamp (mới nhất lên đầu)
                    Collections.sort(visibleCollections, new Comparator<CollectionModel>() {
                        @Override
                        public int compare(CollectionModel c1, CollectionModel c2) {
                            return c2.getTimestamp().compareTo(c1.getTimestamp());
                        }
                    });

                    // Chuyển đổi toàn bộ các collection hiển thị sang model UI
                    List<HomeCollection> allHomeCollections = new ArrayList<>();
                    for (CollectionModel collection : visibleCollections) {
                        // Sử dụng hình ảnh mặc định nếu coverPhoto là null
                        int imageRes = R.drawable.ic_launcher_background;

                        HomeCollection homeCollection = new HomeCollection(
                                imageRes,
                                collection.getCategory()
                        );

                        // Đặt ID nếu có phương thức setId
                        try {
                            java.lang.reflect.Method setIdMethod = homeCollection.getClass().getMethod("setId", int.class);
                            setIdMethod.invoke(homeCollection, collection.getId());
                        } catch (Exception e) {
                            // Bỏ qua nếu không có phương thức setId
                        }

                        allHomeCollections.add(homeCollection);
                    }

                    // Gọi callback
                    if (listener != null) {
                        if (!allHomeCollections.isEmpty()) {
                            listener.onCollectionsLoaded(allHomeCollections);
                        } else {
                            // Nếu không có dữ liệu, dùng dữ liệu mẫu
                            listener.onCollectionsLoaded(createSampleCollections());
                        }
                    }
                } else {
                    // Gọi callback với thông báo lỗi
                    if (listener != null) {
                        listener.onError("Không thể tải dữ liệu collections");
                        listener.onCollectionsLoaded(createSampleCollections());
                    }
                }
            }

            @Override
            public void onFailure(Call<List<CollectionModel>> call, Throwable t) {
                // Gọi callback với thông báo lỗi
                if (listener != null) {
                    listener.onError("Lỗi mạng: " + t.getMessage());
                    listener.onCollectionsLoaded(createSampleCollections());
                }
            }
        });
    }

    public List<HomeCollection> getCachedHomeCollections() {
        return cachedHomeCollections;
    }

    public List<CollectionModel> getCachedCollections() {
        return cachedCollections;
    }

    public boolean isDataLoaded() {
        return dataLoaded;
    }

    public void clearCache() {
        cachedCollections.clear();
        cachedHomeCollections.clear();
        dataLoaded = false;
    }

    private List<HomeCollection> createSampleCollections() {
        List<HomeCollection> collections = new ArrayList<>();
        collections.add(new HomeCollection(R.drawable.ic_launcher_background, "Animals"));
        collections.add(new HomeCollection(R.drawable.ic_launcher_background, "Cars"));
        collections.add(new HomeCollection(R.drawable.ic_launcher_background, "Weathers"));
        collections.add(new HomeCollection(R.drawable.ic_launcher_background, "Water"));
        collections.add(new HomeCollection(R.drawable.ic_launcher_background, "Money"));
        return collections;
    }
}