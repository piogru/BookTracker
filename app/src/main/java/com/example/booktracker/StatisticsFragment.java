package com.example.booktracker;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.booktracker.database.BookViewModel;
import com.example.booktracker.database.entities.BookWithAuthors;

import java.util.ArrayList;
import java.util.List;

public class StatisticsFragment extends Fragment {

    private BookViewModel bookViewModel;
    private List<BookWithAuthors> books;

    private int totalBooksStarted;
    private int totalBooksFinished;
    private int totalAverageTime;
    private int totalTimeSpent;

    private List<Long> bookTimes;

    private TextView totalBooksStartedTextView;
    private TextView totalBooksFinishedTextView;
    private TextView totalAverageTimeTextView;
    private TextView totalTimeSpentTextView;

    public StatisticsFragment() {
        // Required empty public constructor
        totalBooksStarted = 0;
        totalBooksFinished = 0;
        totalAverageTime = 0;
        totalTimeSpent = 0;
        bookTimes = new ArrayList<>();
    }

    public static StatisticsFragment newInstance() {
        StatisticsFragment fragment = new StatisticsFragment();
        return fragment;
    }

    private int calculateAverage(List<Long> times) {
        Long sum = 0L;
        if(!times.isEmpty()) {
            for (Long time : times) {
                sum += time;
            }
            return (int)sum.doubleValue() / times.size();
        }
        return sum.intValue();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        bookViewModel = ViewModelProviders.of(this).get(BookViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_statistics, container, false);

        bookViewModel.findAllBooksWithAuthors().observe(getViewLifecycleOwner(), new Observer<List<BookWithAuthors>>() {
            @Override
            public void onChanged(List<BookWithAuthors> select) {
                books = select;

                totalBooksStarted = books.size();

                for(BookWithAuthors book : books) {
                    if(book.book.getEndDate() != null) {
                        totalBooksFinished += 1;
                        totalTimeSpent += book.book.getTimeSpent();

                        long time = book.book.getEndDate().getTime() - book.book.getStartDate().getTime();
                        bookTimes.add(time);
                    }


//            bookTime.add(Long.valueOf(time));
                }
                totalAverageTime = calculateAverage(bookTimes);

                totalBooksStartedTextView = view.findViewById(R.id.statistics_total_books_started);
                totalBooksFinishedTextView = view.findViewById(R.id.statistics_total_books_finished);
                totalAverageTimeTextView = view.findViewById(R.id.statistics_total_average_time);
                totalTimeSpentTextView = view.findViewById(R.id.statistics_total_time_spent);

                totalBooksStartedTextView.setText(String.valueOf(totalBooksStarted));
                totalBooksFinishedTextView.setText(String.valueOf(totalBooksFinished));
                totalAverageTimeTextView.setText(String.valueOf(totalAverageTime));
                totalTimeSpentTextView.setText(String.valueOf(totalTimeSpent));
            }
        });



        return view;
    }
}