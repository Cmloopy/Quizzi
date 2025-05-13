package com.cmloopy.quizzi.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.cmloopy.quizzi.R;
import com.cmloopy.quizzi.adapter.DetailTopCollectionAdapter;
import com.cmloopy.quizzi.models.DetailTopCollectionItem;

import java.util.ArrayList;
import java.util.List;

public class AuthorCollectionsFragment extends Fragment {

    private RecyclerView recyclerView;
    private DetailTopCollectionAdapter adapter;
    private List<DetailTopCollectionItem> collectionList;
    private TextView collectionsCountText;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_author_collections, container, false);

        collectionsCountText = view.findViewById(R.id.collections_count_text);
        recyclerView = view.findViewById(R.id.recycler_collections);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));

        // Khởi tạo dữ liệu bộ sưu tập
        collectionList = new ArrayList<>();
        loadCollectionData();

        // Cập nhật số lượng bộ sưu tập
        collectionsCountText.setText("49 Collections");

        // Thiết lập adapter
        adapter = new DetailTopCollectionAdapter(collectionList);
        recyclerView.setAdapter(adapter);

        return view;
    }

    private void loadCollectionData() {
        // Sử dụng drawable mặc định từ Android thay vì các drawable tùy chỉnh
        int defaultDrawable = android.R.drawable.ic_menu_gallery; // Hoặc bất kỳ drawable mặc định nào

        // Thêm các mục bộ sưu tập sử dụng drawable mặc định
        collectionList.add(new DetailTopCollectionItem(
                defaultDrawable,
                "Education",
                "Rayford Eddings",
                "Updated 2 days ago",
                "1.2M plays"
        ));

        collectionList.add(new DetailTopCollectionItem(
                defaultDrawable,
                "Technology",
                "Rayford Eddings",
                "Updated 3 days ago",
                "890K plays"
        ));

        collectionList.add(new DetailTopCollectionItem(
                defaultDrawable,
                "Business",
                "Rayford Eddings",
                "Updated 5 days ago",
                "720K plays"
        ));

        collectionList.add(new DetailTopCollectionItem(
                defaultDrawable,
                "Fashion",
                "Rayford Eddings",
                "Updated 1 week ago",
                "540K plays"
        ));
    }
}