package com.example.booktracker;

import android.os.Bundle;

import androidx.core.util.Pair;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.booktracker.database.BookViewModel;
import com.example.booktracker.database.entities.BookWithAuthors;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class StatisticsFragment extends Fragment {

    private BookViewModel bookViewModel;
    private List<BookWithAuthors> books;

    private int totalBooksStarted;
    private int totalBooksFinished;
    private int totalAverageTime;
    private int totalTimeSpent;
    private int totalPagesRead;

    private int monthlyBooksStarted;
    private int monthlyBooksFinished;
    private int monthlyAverageTime;
    private int monthlyTimeSpent;
    private int monthlyPagesRead;

    private List<Long> totalBookTimes;
    private List<Long> monthlyBookTimes;

    private List<BookWithAuthors> monthlyBooks;

    private TextView totalBooksStartedTextView;
    private TextView totalBooksFinishedTextView;
    private TextView totalAverageTimeTextView;
    private TextView totalTimeSpentTextView;
    private TextView totalPagesReadTextView;

    private TextView monthlyBooksStartedTextView;
    private TextView monthlyBooksFinishedTextView;
    private TextView monthlyAverageTimeTextView;
    private TextView monthlyTimeSpentTextView;
    private TextView monthlyPagesReadTextView;

    private Button pickDateButton;
    private TextView selectedDateTextView;

    private Long startDate;
    private Long endDate;

    public StatisticsFragment() {
        // Required empty public constructor
        totalBooksStarted = 0;
        totalBooksFinished = 0;
        totalAverageTime = 0;
        totalTimeSpent = 0;
        totalPagesRead = 0;
        totalBookTimes = new ArrayList<>();

        monthlyBooksStarted = 0;
        monthlyBooksFinished = 0;
        monthlyAverageTime = 0;
        monthlyTimeSpent = 0;
        monthlyPagesRead = 0;
        monthlyBookTimes = new ArrayList<>();

        monthlyBooks = new ArrayList<>();
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
        Calendar cal = Calendar.getInstance();

        bookViewModel.findAllBooksWithAuthors().observe(getViewLifecycleOwner(), new Observer<List<BookWithAuthors>>() {
            @Override
            public void onChanged(List<BookWithAuthors> select) {
                books = select;
                Date currentDate = new Date();

                cal.setTime(currentDate);
                int month = cal.get(Calendar.MONTH);
                int bookMonth;

                // total statistics
                totalBooksStarted = books.size();

                for(BookWithAuthors book : books) {
                    cal.setTime(book.book.getStartDate());
                    bookMonth = cal.get(Calendar.MONTH);
                    if(month == bookMonth) {
                        monthlyBooksStarted +=1;
                    }

                    if(book.book.getEndDate() != null) {
                        totalBooksFinished += 1;
                        totalTimeSpent += book.book.getTimeSpent();
                        totalPagesRead += book.book.getPageCount();

                        long time = book.book.getEndDate().getTime() - book.book.getStartDate().getTime();
                        time = time / 1000 / 60; // time in minutes
                        totalBookTimes.add(time);

                        cal.setTime(book.book.getEndDate());
                        bookMonth = cal.get(Calendar.MONTH);
                        if(month == bookMonth) {
                            monthlyBooks.add(book);
                        }
                    }
                }
                totalAverageTime = calculateAverage(totalBookTimes);

                totalBooksStartedTextView = view.findViewById(R.id.statistics_total_books_started);
                totalBooksFinishedTextView = view.findViewById(R.id.statistics_total_books_finished);
                totalAverageTimeTextView = view.findViewById(R.id.statistics_total_average_time);
                totalTimeSpentTextView = view.findViewById(R.id.statistics_total_time_spent);
                totalPagesReadTextView = view.findViewById(R.id.statistics_total_pages_read);

                totalBooksStartedTextView.setText(String.valueOf(totalBooksStarted));
                totalBooksFinishedTextView.setText(String.valueOf(totalBooksFinished));
                totalAverageTimeTextView.setText(String.valueOf(totalAverageTime));
                totalTimeSpentTextView.setText(String.valueOf(totalTimeSpent));
                totalPagesReadTextView.setText(String.valueOf(totalPagesRead));

                // monthly statistics

                for(BookWithAuthors book : monthlyBooks) {
                    if(book.book.getEndDate() != null) {
                        monthlyBooksFinished += 1;
                        monthlyTimeSpent += book.book.getTimeSpent();
                        monthlyPagesRead += book.book.getPageCount();

                        long time = book.book.getEndDate().getTime() - book.book.getStartDate().getTime();
                        time = time / 1000 / 60; // time in minutes
                        monthlyBookTimes.add(time);
                    }
                }
                monthlyAverageTime = calculateAverage(monthlyBookTimes);

                monthlyBooksStartedTextView = view.findViewById(R.id.statistics_monthly_books_started);
                monthlyBooksFinishedTextView = view.findViewById(R.id.statistics_monthly_books_finished);
                monthlyAverageTimeTextView = view.findViewById(R.id.statistics_monthly_average_time);
                monthlyTimeSpentTextView = view.findViewById(R.id.statistics_monthly_time_spent);
                monthlyPagesReadTextView = view.findViewById(R.id.statistics_monthly_pages_read);

                monthlyBooksStartedTextView.setText(String.valueOf(monthlyBooksStarted));
                monthlyBooksFinishedTextView.setText(String.valueOf(monthlyBooksFinished));
                monthlyAverageTimeTextView.setText(String.valueOf(monthlyAverageTime));
                monthlyTimeSpentTextView.setText(String.valueOf(monthlyTimeSpent));
                monthlyPagesReadTextView.setText(String.valueOf(monthlyPagesRead));
            }
        });

        // DatePicker
        pickDateButton = view.findViewById(R.id.pick_date_button);
        selectedDateTextView = view.findViewById(R.id.selected_date);

        MaterialDatePicker.Builder<Pair<Long, Long>> materialDateBuilder = MaterialDatePicker.Builder.dateRangePicker();


        Long start = MaterialDatePicker.thisMonthInUtcMilliseconds();
        Long end = (start + new Long(cal.getActualMaximum(Calendar.DATE)) * 24 * 3600 * 1000) - 1;
        Pair<Long, Long> pair = new Pair<>(
                start,
                end
        );

        materialDateBuilder.setTitleText("Select a date range");
        materialDateBuilder.setSelection(pair);

        final MaterialDatePicker materialDatePicker = materialDateBuilder.build();

        pickDateButton.setOnClickListener(
            new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    materialDatePicker.show(getParentFragmentManager(), "MATERIAL_DATE_PICKER");
                }
            });

        materialDatePicker.addOnPositiveButtonClickListener(
            new MaterialPickerOnPositiveButtonClickListener() {
                @Override
                public void onPositiveButtonClick(Object selection) {
                    selectedDateTextView.setText("Selected Date is : " + materialDatePicker.getHeaderText());
                    Pair dateRange = (Pair) selection;

                    monthlyBooksStarted = 0;
                    monthlyBooksFinished = 0;
                    monthlyAverageTime = 0;
                    monthlyTimeSpent = 0;
                    monthlyPagesRead = 0;
                    monthlyBookTimes = new ArrayList<>();
                    monthlyBooks = new ArrayList<>();

                    for(BookWithAuthors book : books) {
                        Long bookTime = book.book.getStartDate().getTime();

                        if(bookTime.compareTo((Long)dateRange.first) >= 0 && bookTime.compareTo((Long)dateRange.second) <= 0) {
//                                monthlyBooks.add(book);
                            monthlyBooksStarted += 1;
                        }

                        if(book.book.getEndDate() != null) {
                            bookTime = book.book.getEndDate().getTime();

                            if(bookTime.compareTo((Long)dateRange.first) >= 0 && bookTime.compareTo((Long)dateRange.second) <= 0) {
                                monthlyBooks.add(book);
                            }
                        }
                    }
                    // monthly statistics

                    for(BookWithAuthors book : monthlyBooks) {
                        if(book.book.getEndDate() != null) {
                            monthlyBooksFinished += 1;
                            monthlyTimeSpent += book.book.getTimeSpent();
                            monthlyPagesRead += book.book.getPageCount();

                            long time = book.book.getEndDate().getTime() - book.book.getStartDate().getTime();
                            time = time / 1000 / 60; // time in minutes
                            monthlyBookTimes.add(time);
                        }
                    }
                    monthlyAverageTime = calculateAverage(monthlyBookTimes);

                    monthlyBooksStartedTextView = view.findViewById(R.id.statistics_monthly_books_started);
                    monthlyBooksFinishedTextView = view.findViewById(R.id.statistics_monthly_books_finished);
                    monthlyAverageTimeTextView = view.findViewById(R.id.statistics_monthly_average_time);
                    monthlyTimeSpentTextView = view.findViewById(R.id.statistics_monthly_time_spent);
                    monthlyPagesReadTextView = view.findViewById(R.id.statistics_monthly_pages_read);

                    monthlyBooksStartedTextView.setText(String.valueOf(monthlyBooksStarted));
                    monthlyBooksFinishedTextView.setText(String.valueOf(monthlyBooksFinished));
                    monthlyAverageTimeTextView.setText(String.valueOf(monthlyAverageTime));
                    monthlyTimeSpentTextView.setText(String.valueOf(monthlyTimeSpent));
                    monthlyPagesReadTextView.setText(String.valueOf(monthlyPagesRead));
                }
            });

        return view;
    }
}