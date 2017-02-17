package com.musicapp.pojos;

import java.util.ArrayList;

/**
 * Created by PalseeTrivedi on 12/22/2016.
 */
public class SearchListItem {
    public String CategoryName;
    public String CategoryId;
 ArrayList<SearchListSectionItem> sectionList;

    public void setCategoryName(String categoryName) {
        CategoryName = categoryName;
    }

    public void setCategoryId(String categoryId) {
        CategoryId = categoryId;
    }

    public void setSectionList(ArrayList<SearchListSectionItem> sectionList) {
        this.sectionList = sectionList;
    }
    public String getCategoryName() {
        return CategoryName;
    }

    public String getCategoryId() {
        return CategoryId;
    }





    public ArrayList<SearchListSectionItem> getSectionList() {
        return sectionList;
    }
}
