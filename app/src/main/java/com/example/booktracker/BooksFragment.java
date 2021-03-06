package com.example.booktracker;

import static com.example.booktracker.MainActivity.IMAGE_URL_BASE;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.core.util.Pair;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProviders;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.provider.OpenableColumns;
import android.text.TextUtils;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.example.booktracker.database.BookViewModel;
import com.example.booktracker.database.entities.BookWithAuthors;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;
import com.google.android.material.snackbar.Snackbar;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import androidx.lifecycle.Observer;

public class BooksFragment extends Fragment {

    private static final String KEY_SELECTED_DATE_START = "selected_date_start";
    private static final String KEY_SELECTED_DATE_END = "selected_date_end";

    BookViewModel bookViewModel;
    BookWithAuthors editedBook;
    List<BookWithAuthors> bookList;
    List<BookWithAuthors> bookSelection;

    private Button pickDateButton;
    private Button clearDateButton;
    private TextView selectedDateTextView;
    private View selectedDateLayout;
    Pair dateRange;

    public BooksFragment() {
        dateRange = null;
    }

    public static BooksFragment newInstance() {
        BooksFragment fragment = new BooksFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_books, container, false);

        RecyclerView recyclerView = view.findViewById(R.id.recyclerview);
        final BookAdapter adapter = new BookAdapter();
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this.getContext()));

        bookViewModel = ViewModelProviders.of(this).get(BookViewModel.class);
        bookViewModel.findAllBooksWithAuthors().observe(getViewLifecycleOwner(), new Observer<List<BookWithAuthors>>() {
            @Override
            public void onChanged(List<BookWithAuthors> books) {
                bookList = books;
                Collections.sort(bookList);
                adapter.setBooks(books);
            }
        });

        // DatePicker
        pickDateButton = view.findViewById(R.id.pick_date_button);
        clearDateButton = view.findViewById(R.id.clear_date_button);
        selectedDateTextView = view.findViewById(R.id.selected_date);
        selectedDateLayout = view.findViewById(R.id.selected_date_layout);
        clearDateButton.setVisibility(View.GONE);

        MaterialDatePicker.Builder<Pair<Long, Long>> materialDateBuilder = MaterialDatePicker.Builder.dateRangePicker();
        Calendar cal = Calendar.getInstance();

        Long start = MaterialDatePicker.thisMonthInUtcMilliseconds();
        Long end = (start + new Long(cal.getActualMaximum(Calendar.DATE)) * 24 * 3600 * 1000) - 1;
        Pair<Long, Long> pair = new Pair<>(
                start,
                end
        );

        materialDateBuilder.setTitleText(getResources().getString(R.string.date_picker_header));
        materialDateBuilder.setSelection(pair);
        final MaterialDatePicker materialDatePicker = materialDateBuilder.build();

        selectedDateTextView.setText(R.string.select_date);

        if(savedInstanceState != null) {
            //Restore the fragment's state here
            Long startDate = savedInstanceState.getLong(KEY_SELECTED_DATE_START);
            Long endDate = savedInstanceState.getLong(KEY_SELECTED_DATE_END);

            if(!startDate.equals(0L)) {
                dateRange = new Pair<>(
                        startDate,
                        endDate
                );

                String pattern = "MMM DD";
                SimpleDateFormat simpleDateFormat =new SimpleDateFormat(pattern);
                String date1 = simpleDateFormat.format(new Date((Long)dateRange.first));
                String date2 = simpleDateFormat.format(new Date((Long)dateRange.second));
                String selection = new String(date1 + " - " + date2);

                selectedDateTextView.setText(selection);
                selectedDateLayout.setVisibility(View.VISIBLE);
                clearDateButton.setVisibility(View.VISIBLE);

                bookSelection = new ArrayList<>();

                bookViewModel.findAllBooksWithAuthors().observe(getViewLifecycleOwner(), new Observer<List<BookWithAuthors>>() {
                    @Override
                    public void onChanged(List<BookWithAuthors> books) {
                        bookList = books;
//                        adapter.setBooks(books);

                        for(BookWithAuthors book : bookList) {
                            Long bookTime = book.book.getStartDate().getTime();

                            if(bookTime.compareTo((Long)dateRange.first) >= 0 && bookTime.compareTo((Long)dateRange.second) <= 0) {
                                bookSelection.add(book);
                            }
                        }

                        adapter.setBooks(bookSelection);
                    }
                });

            }
        }

        pickDateButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        clearDateButton.setVisibility(View.VISIBLE);
                        materialDatePicker.show(getParentFragmentManager(), "MATERIAL_DATE_PICKER");
                    }
                });

        clearDateButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        clearDateButton.setVisibility(View.GONE);
                        selectedDateTextView.setText(R.string.select_date);

                        dateRange = null;

                        adapter.setBooks(bookList);
                    }
                });

        materialDatePicker.addOnPositiveButtonClickListener(
                new MaterialPickerOnPositiveButtonClickListener() {
                    @Override
                    public void onPositiveButtonClick(Object selection) {
                        selectedDateTextView.setText(materialDatePicker.getHeaderText());
                        selectedDateLayout.setVisibility(View.VISIBLE);

                        dateRange = (Pair) selection;

                        bookSelection = new ArrayList<>();

                        for(BookWithAuthors book : bookList) {
                            Long bookTime = book.book.getStartDate().getTime();

                            if(bookTime.compareTo((Long)dateRange.first) >= 0 && bookTime.compareTo((Long)dateRange.second) <= 0) {
                                bookSelection.add(book);
                            }
                        }

                        adapter.setBooks(bookSelection);
                    }
                });

        return view;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        //Save the fragment's state here
        if(dateRange != null) {
            outState.putLong(KEY_SELECTED_DATE_START, (Long)dateRange.first);
            outState.putLong(KEY_SELECTED_DATE_END, (Long)dateRange.second);
        }
    }


    private class BookHolder extends RecyclerView.ViewHolder {
        private TextView bookTitleTextView;
        private TextView bookAuthorTextView;
        private ImageView iconImageView;
        private BookWithAuthors book;

        public BookHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.book_list_item, parent, false));

            bookTitleTextView = itemView.findViewById(R.id.book_title);
            bookAuthorTextView = itemView.findViewById(R.id.book_author);
            iconImageView = itemView.findViewById(R.id.icon);
            View bookItem = itemView.findViewById(R.id.book_item);


            bookItem.setOnLongClickListener(v -> {
                //Creating the instance of PopupMenu
                PopupMenu popup = new PopupMenu(getContext(), bookItem, Gravity.END);
                popup.getMenuInflater().inflate(R.menu.book_menu, popup.getMenu());

                //registering popup with OnMenuItemClickListener
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    public boolean onMenuItemClick(MenuItem item) {
                        switch(item.getItemId()){
                            case R.id.book_menu_delete:
                                bookViewModel.delete(book);
                                Snackbar.make(getActivity().findViewById(R.id.coordinator_layout),
                                        getString(R.string.book_deleted),
                                        Snackbar.LENGTH_LONG)
                                        .setAnchorView(R.id.bottom_navigation)
                                        .show();
                        }
                        return true;
                    }
                });
                popup.show(); //showing popup menu

                return true;
            });

            bookItem.setOnClickListener(v -> {
                editedBook = book;
                Intent intent = new Intent(getActivity(), BookDetailsActivity.class);
                intent.putExtra(BookDetailsActivity.EXTRA_BOOK_TITLE, bookTitleTextView.getText());
                intent.putExtra(BookDetailsActivity.EXTRA_BOOK_AUTHOR, bookAuthorTextView.getText());

                intent.putExtra(BookDetailsActivity.EXTRA_BOOK_PAGE_COUNT, book.book.getPageCount());
                intent.putExtra(BookDetailsActivity.EXTRA_BOOK_START_DATE, book.book.getStartDate().getTime());
                if(book.book.getEndDate() != null){
                    intent.putExtra(BookDetailsActivity.EXTRA_BOOK_END_DATE, book.book.getEndDate().getTime());
                }
                intent.putExtra(BookDetailsActivity.EXTRA_BOOK_TIME_SPENT, book.book.getTimeSpent());
                intent.putExtra(BookDetailsActivity.EXTRA_BOOK_COVER, book.book.getCover());

                activityResultLaunch.launch(intent);
            });
        }

        public void bind(BookWithAuthors book) {
            bookTitleTextView.setText(book.book.getTitle());
            bookAuthorTextView.setText(TextUtils.join(", ", book.authors));
            if(book.book.getEndDate() != null) {
                iconImageView.setImageResource(R.drawable.ic_check_box_24dp);
            } else {
                iconImageView.setImageResource(R.drawable.ic_check_box_outline_24dp);
            }

            this.book = book;
        }
    }

        ActivityResultLauncher<Intent> activityResultLaunch = registerForActivityResult(
        new ActivityResultContracts.StartActivityForResult(),
        new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {
                Activity activity = getActivity();

                if (result.getResultCode() == 100) {
                    Snackbar.make(activity.findViewById(R.id.coordinator_layout), getString(R.string.book_added),
                            Snackbar.LENGTH_LONG)
                            .setAnchorView(R.id.bottom_navigation)
                            .show();
                } else if (result.getResultCode() == 110) {
                    Snackbar.make(activity.findViewById(R.id.coordinator_layout), getString(R.string.book_not_added),
                            Snackbar.LENGTH_LONG)
                            .setAnchorView(R.id.bottom_navigation)
                            .show();
                } else if (result.getResultCode() == 200) {
                    Date endDate = new Date();

                    LiveData bookLiveData = bookViewModel.findBookWithTitle(editedBook.book.getTitle());
                    Observer observer = new Observer<BookWithAuthors>() {
                        @Override
                        public void onChanged(BookWithAuthors select) {
                            bookLiveData.removeObservers(getActivity());

                            editedBook = select;
                            editedBook.book.setEndDate(endDate);
                            bookViewModel.update(editedBook.book);
                            Snackbar.make(activity.findViewById(R.id.coordinator_layout), getString(R.string.book_finished),
                                    Snackbar.LENGTH_LONG)
                                    .setAnchorView(R.id.bottom_navigation)
                                    .show();
                        }
                    };

                    bookLiveData.observe(getActivity(), observer);
                }
            }
        });

    private class BookAdapter extends RecyclerView.Adapter<BookHolder> {
        private List<BookWithAuthors> books;

        @NonNull
        @Override
        public BookHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new BookHolder(getLayoutInflater(), parent);
        }

        @Override
        public void onBindViewHolder(@NonNull BookHolder holder, int position) {
            if (books != null) {
                BookWithAuthors book = books.get(position);
                holder.bind(book);
            } else {
                Log.d("MainActivity", "No books");
            }
        }

        @Override
        public int getItemCount() {
            if(books != null) {
                return books.size();
            } else {
                return 0;
            }
        }

        void setBooks(List<BookWithAuthors> books) {
            this.books = books;
            notifyDataSetChanged();
        }
    }
}