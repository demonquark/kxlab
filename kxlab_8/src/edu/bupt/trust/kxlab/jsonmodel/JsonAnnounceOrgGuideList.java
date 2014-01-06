package edu.bupt.trust.kxlab.jsonmodel;

import java.util.List;

import edu.bupt.trust.kxlab.utils.JsonTools;

public class JsonAnnounceOrgGuideList {
	
	List<JsonAnnounceFAQ> content;
	List<JsonSortFeatures> sort;
	int totalElements;
	int numberOfElements;
	int totalPages;
	boolean firstPage;
	boolean lastPage;
	int size;
	int number;
	
	public List<JsonAnnounceFAQ> getContent() {
		return content;
	}
	public List<JsonSortFeatures> getSort() {
		return sort;
	}
	public int getTotalElements() {
		return totalElements;
	}
	public int getNumberOfElements() {
		return numberOfElements;
	}
	public int getTotalPages() {
		return totalPages;
	}
	public boolean isFirstPage() {
		return firstPage;
	}
	public boolean isLastPage() {
		return lastPage;
	}
	public int getSize() {
		return size;
	}
	public int getNumber() {
		return number;
	}
	public void setContent(List<JsonAnnounceFAQ> content) {
		this.content = content;
	}
	public void setSort(List<JsonSortFeatures> sort) {
		this.sort = sort;
	}
	public void setTotalElements(int totalElements) {
		this.totalElements = totalElements;
	}
	public void setNumberOfElements(int numberOfElements) {
		this.numberOfElements = numberOfElements;
	}
	public void setTotalPages(int totalPages) {
		this.totalPages = totalPages;
	}
	public void setFirstPage(boolean firstPage) {
		this.firstPage = firstPage;
	}
	public void setLastPage(boolean lastPage) {
		this.lastPage = lastPage;
	}
	public void setSize(int size) {
		this.size = size;
	}
	public void setNumber(int number) {
		this.number = number;
	}
	
	
	public boolean updateWithNew(JsonAnnounceOrgGuideList b, boolean pushToEnd) {
		
		JsonTools.updateAnnounce(content, b.content, pushToEnd);
		
		sort = b.sort;
		totalElements = b.totalElements;
		numberOfElements = b.numberOfElements;
		totalPages = b.totalPages;
		firstPage = b.firstPage;
		lastPage = b.lastPage;
		size = b.size;
		number = b.number;

		return false;
	}

	
}
