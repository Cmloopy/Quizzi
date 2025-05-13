package com.cmloopy.quizzi.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.cmloopy.quizzi.R;
import com.cmloopy.quizzi.adapter.QuizAdapter;
import com.cmloopy.quizzi.models.Quiz;
import com.cmloopy.quizzi.models.Question;

import java.util.ArrayList;
import java.util.List;

public class AuthorQuizzoFragment extends Fragment {

    private RecyclerView recyclerView;
    private QuizAdapter adapter;
    private List<Quiz> quizList;
    private TextView quizzoCountText;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_author_quizzo, container, false);

        quizzoCountText = view.findViewById(R.id.quizzo_count_text);
        recyclerView = view.findViewById(R.id.recycler_quizzo);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Khởi tạo dữ liệu quizzo
        quizList = new ArrayList<>();
        loadQuizData();

        // Cập nhật số lượng Quizzo
        quizzoCountText.setText("265 Quizzo");

        // Thiết lập adapter
        adapter = new QuizAdapter(quizList);
        recyclerView.setAdapter(adapter);

        return view;
    }

    private void loadQuizData() {
        // Tạo danh sách các câu hỏi mẫu
        // Tạo danh sách các câu hỏi mẫu
        List<Question> questions1 = new ArrayList<>();
        questions1.add(new Question("Sample question 1", 0, 1, new ArrayList<>()));
        questions1.add(new Question("Sample question 2", 1, 1, new ArrayList<>()));

        List<Question> questions2 = new ArrayList<>();
        questions2.add(new Question("Sample question 1", 0, 1, new ArrayList<>()));
        questions2.add(new Question("Sample question 2", 1, 1, new ArrayList<>()));
        questions2.add(new Question("Sample question 3", 2, 1, new ArrayList<>()));

        // Thêm các mục quizzo mẫu như trong ảnh chụp màn hình


        // Thêm thêm quizzes nếu cần
    }
}