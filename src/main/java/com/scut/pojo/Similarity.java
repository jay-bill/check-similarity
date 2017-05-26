package com.scut.pojo;

import java.util.ArrayList;

/**
 * 重复率
 * @author jaybill
 *
 */
public class Similarity {
	private String id;
	private ArrayList<Double> similarity = new  ArrayList<Double>();
	public ArrayList<Double> getSimilarity() {
		return similarity;
	}
	public void setSimilarity(ArrayList<Double> similarity) {
		this.similarity = similarity;
	}
	private ArrayList<String> sId = new  ArrayList<String>();//和学号为id的学生相比，最高重复率的学生学号
	public ArrayList<String> getsId() {
		return sId;
	}
	public void setsId(ArrayList<String> sId) {
		this.sId = sId;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	@Override
	public String toString() {
		return "Similarity [id=" + id + ", similarity=" + similarity + ", sId=" + sId + ", getSimilarity()="
				+ getSimilarity() + ", getsId()=" + getsId() + ", getId()=" + getId() + "]";
	}	
}
