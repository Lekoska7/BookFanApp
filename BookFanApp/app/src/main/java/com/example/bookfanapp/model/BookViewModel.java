package com.example.bookfanapp.model;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;

public class BookViewModel extends ViewModel {
    private final MutableLiveData<Book> selectedBook= new MutableLiveData<>();
    private final MutableLiveData <List<Book>> selectedBooks=new MutableLiveData<>();
    private final MutableLiveData <String> selectedDocumentRef=new MutableLiveData<>();

    public void setSelectedDocumentRef (String id){
        selectedDocumentRef.setValue(id);
    }

    public  LiveData<String> getSelectedDocumentRef(){
        return selectedDocumentRef;
    }

    public void setSelectedBook (Book book){
        selectedBook.setValue(book);
    }
    public LiveData<Book> getSelectedBook(){
        return selectedBook;
    }

    public void setSelectedBooks(List<Book> books){
        selectedBooks.setValue(books);
    }

    public LiveData <List<Book>> getSelectedBooks(){
        return selectedBooks;
    }
}
