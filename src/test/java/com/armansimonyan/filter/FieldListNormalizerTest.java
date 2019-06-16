package com.armansimonyan.filter;

import org.junit.Before;
import org.junit.Test;

import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.*;

public class FieldListNormalizerTest {

	private FieldListNormalizer normalizer;

	@Before
	public void setUp() {
		normalizer = new FieldListNormalizer();
	}

	@Test
	public void normalize_all() {
		Set<String> fields = new HashSet<>();
		fields.add("id");
		fields.add("name");
		fields.add("child:id");
		fields.add("child:name");
		fields.add("child:grandChild:id");
		fields.add("child:grandChild:name");

		Set<String> result = normalizer.normalize(fields);

		Set<String> expected = new HashSet<>();
		expected.add("id");
		expected.add("name");
		expected.add("child");
		expected.add("child:id");
		expected.add("child:name");
		expected.add("child:grandChild");
		expected.add("child:grandChild:id");
		expected.add("child:grandChild:name");

		assertEquals(expected, result);
	}

	@Test
	public void normalize_partial() {
		Set<String> fields = new HashSet<>();
		fields.add("name");
		fields.add("child:id");
		fields.add("child:grandChild:name");

		Set<String> result = normalizer.normalize(fields);

		Set<String> expected = new HashSet<>();
		expected.add("name");
		expected.add("child");
		expected.add("child:id");
		expected.add("child:grandChild");
		expected.add("child:grandChild:name");

		assertEquals(expected, result);
	}

	@Test
	public void normalize_wildcard() {
		Set<String> fields = new HashSet<>();
		fields.add("id");
		fields.add("child:*");
		fields.add("child:grandChild:name");

		Set<String> result = normalizer.normalize(fields);

		Set<String> expected = new HashSet<>();
		expected.add("id");
		expected.add("child");
		expected.add("child:*");
		expected.add("child:grandChild");
		expected.add("child:grandChild:name");

		assertEquals(expected, result);
	}

}
