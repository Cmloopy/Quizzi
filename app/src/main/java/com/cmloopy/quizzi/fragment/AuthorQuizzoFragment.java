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

    private String authorId;
    private String authorName;
    private String authorUsername;
    private int authorAvatar;
    private String authorAvatarUrl;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Lấy dữ liệu tác giả từ arguments
        if (getArguments() != null) {
            authorId = getArguments().getString("AUTHOR_ID");
            authorName = getArguments().getString("AUTHOR_NAME");
            authorUsername = getArguments().getString("AUTHOR_USERNAME");
            authorAvatar = getArguments().getInt("AUTHOR_AVATAR");
            if (getArguments().containsKey("AUTHOR_AVATAR_URL")) {
                authorAvatarUrl = getArguments().getString("AUTHOR_AVATAR_URL");
            }
        }
    }

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

        // Cập nhật số lượng Quizzo từ dữ liệu truyền vào
        int totalQuizs = 0;
        if (getArguments() != null) {
            totalQuizs = getArguments().getInt("AUTHOR_TOTAL_QUIZS", 0);
        }
        quizzoCountText.setText(totalQuizs + " Quizzo");

        // Thiết lập adapter
        adapter = new QuizAdapter(quizList, 1);
        recyclerView.setAdapter(adapter);

        return view;
    }

    private void loadQuizData() {
        // Tạo danh sách các câu hỏi mẫu
        List<Question> questions1 = new ArrayList<>();
        questions1.add(new Question("Sample question 1", 0, 1, new ArrayList<>()));
        questions1.add(new Question("Sample question 2", 1, 1, new ArrayList<>()));

        List<Question> questions2 = new ArrayList<>();
        questions2.add(new Question("Sample question 1", 0, 1, new ArrayList<>()));
        questions2.add(new Question("Sample question 2", 1, 1, new ArrayList<>()));
        questions2.add(new Question("Sample question 3", 2, 1, new ArrayList<>()));

        // Thêm các mục quizzo mẫu
        // Ví dụ (bạn cần điều chỉnh theo cấu trúc Quiz của mình):
        // quizList.add(new Quiz("Sample Quiz 1", authorName, questions1, "10K plays"));
        // quizList.add(new Quiz("Sample Quiz 2", authorName, questions2, "5K plays"));
    }

    // Phương thức để tải dữ liệu quizzes của một tác giả cụ thể
    private void loadQuizzesByAuthorId(String authorId) {
        // Tải dữ liệu quizzes từ API hoặc cơ sở dữ liệu dựa trên authorId
    }
}