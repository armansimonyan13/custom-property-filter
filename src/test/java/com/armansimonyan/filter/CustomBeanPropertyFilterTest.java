package com.armansimonyan.filter;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ser.FilterProvider;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import org.junit.Before;
import org.junit.Test;

import java.util.HashSet;
import java.util.Set;

import static com.jayway.jsonpath.matchers.JsonPathMatchers.*;
import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;

public class CustomBeanPropertyFilterTest {

	private Parent parent;

	@Before
	public void setUp() throws Exception {
		parent = new Parent();
		parent.id = "parent_id";
		parent.name = "parent_name";
		Child child = new Child();
		child.id = "child_id";
		child.name = "child_name";
		parent.child = child;
		GrandChild grandChild = new GrandChild();
		grandChild.id = "grand_child_id";
		grandChild.name = "grand_child_name";
		child.grandChild = grandChild;
	}

	private ObjectMapper createObjectMapper(Set<String> fields) {
		CustomBeanPropertyFilter customBeanPropertyFilter = new CustomBeanPropertyFilter(fields);

		ObjectMapper objectMapper = new ObjectMapper();
		FilterProvider filterProvider = new SimpleFilterProvider().addFilter("custom-property-customBeanPropertyFilter", customBeanPropertyFilter);
		objectMapper.setFilterProvider(filterProvider);

		@JsonFilter("custom-property-customBeanPropertyFilter")
		class CustomPropertyFilterMixIn { }

		objectMapper.addMixIn(Object.class, CustomPropertyFilterMixIn.class);

		return objectMapper;
	}

	@Test
	public void all_fields_are_serialized() throws Exception {
		Set<String> fieldList = new HashSet<>();
		fieldList.add("id");
		fieldList.add("name");
		fieldList.add("child");
		fieldList.add("child:id");
		fieldList.add("child:name");
		fieldList.add("child:grandChild");
		fieldList.add("child:grandChild:id");
		fieldList.add("child:grandChild:name");

		String result = createObjectMapper(fieldList).writeValueAsString(parent);

		assertThat(result, hasJsonPath("$.id", equalTo("parent_id")));
		assertThat(result, hasJsonPath("$.name", equalTo("parent_name")));
		assertThat(result, hasJsonPath("$.child.id", equalTo("child_id")));
		assertThat(result, hasJsonPath("$.child.name", equalTo("child_name")));
		assertThat(result, hasJsonPath("$.child.grandChild.id", equalTo("grand_child_id")));
		assertThat(result, hasJsonPath("$.child.grandChild.name", equalTo("grand_child_name")));
	}

	@Test
	public void partial_fields_are_serialized() throws Exception {
		Set<String> fieldList = new HashSet<>();
		fieldList.add("id");
		fieldList.add("child");
		fieldList.add("child:id");
		fieldList.add("child:grandChild");
		fieldList.add("child:grandChild:id");

		String result = createObjectMapper(fieldList).writeValueAsString(parent);

		assertThat(result, hasJsonPath("$.id", equalTo("parent_id")));
		assertThat(result, hasNoJsonPath("$.name"));
		assertThat(result, hasJsonPath("$.child.id", equalTo("child_id")));
		assertThat(result, hasNoJsonPath("$.child.name"));
		assertThat(result, hasJsonPath("$.child.grandChild.id", equalTo("grand_child_id")));
		assertThat(result, hasNoJsonPath("$.child.grandChild.name"));
	}

	@Test
	public void all_subfields_are_serialized_with_asterisk() throws Exception {
		Set<String> fieldList = new HashSet<>();
		fieldList.add("id");
		fieldList.add("child");
		fieldList.add("child:*");
		fieldList.add("child:grandChild");
		fieldList.add("child:grandChild:name");

		String result = createObjectMapper(fieldList).writeValueAsString(parent);

		assertThat(result, hasJsonPath("$.id", equalTo("parent_id")));
		assertThat(result, hasNoJsonPath("$.name"));
		assertThat(result, hasJsonPath("$.child.id", equalTo("child_id")));
		assertThat(result, hasJsonPath("$.child.name", equalTo("child_name")));
		assertThat(result, hasNoJsonPath("$.child.grandChild.id"));
		assertThat(result, hasJsonPath("$.child.grandChild.name", equalTo("grand_child_name")));
	}

	@Test
	public void all_fields_are_serialized_when_no_field_requested() throws Exception {
		Set<String> fieldList = new HashSet<>();

		String result = createObjectMapper(fieldList).writeValueAsString(parent);

		assertThat(result, hasJsonPath("$.id", equalTo("parent_id")));
		assertThat(result, hasJsonPath("$.name", equalTo("parent_name")));
		assertThat(result, hasJsonPath("$.child.id", equalTo("child_id")));
		assertThat(result, hasJsonPath("$.child.name", equalTo("child_name")));
		assertThat(result, hasJsonPath("$.child.grandChild.id", equalTo("grand_child_id")));
		assertThat(result, hasJsonPath("$.child.grandChild.name", equalTo("grand_child_name")));
	}

}
