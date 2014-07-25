package com.bestfunforever.thichngamgaixinhvn;

import java.util.Comparator;

import com.thichngamgaixinh.model.Photo;

public class TimePhotoComparator implements Comparator<Photo> {

	@Override
	public int compare(Photo lhs, Photo rhs) {
		int name1 = Integer.valueOf((lhs).getCreated());
		int name2 = Integer.valueOf((rhs).getCreated());
		if (name1 > name2)
			return -1;
		else if (name1 < name2)
			return 1;
		else
			return 0;
	}
}
