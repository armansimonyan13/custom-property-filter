package com.armansimonyan.filter;

import java.util.HashSet;
import java.util.Set;

public class FieldListNormalizer {

	public Set<String> normalize(Set<String> fieldList) {
		Set<String> result = new HashSet<>();
		for (String s : fieldList) {
			int index = s.lastIndexOf(":");
			if (index > -1) {
				result.add(s.substring(0, index));
			}
			result.add(s);
		}
		return result;
	}

}
