package com.armansimonyan.filter;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonStreamContext;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.PropertyWriter;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;

import java.util.HashSet;
import java.util.Set;

public class CustomBeanPropertyFilter extends SimpleBeanPropertyFilter {

	private final Set<String> fields;

	public CustomBeanPropertyFilter(Set<String> fields) {
		this.fields = new HashSet<>(fields);
	}

	@Override
	public void serializeAsField(Object pojo, JsonGenerator jgen, SerializerProvider provider, PropertyWriter writer) throws Exception {
		if (fields.isEmpty()) {
			super.serializeAsField(pojo, jgen, provider, writer);
		}

		String path = createPath(writer, jgen);
		String pathWithWildcard = cratePathWithWildcard(path);

		if (this.fields.contains(path) || this.fields.contains(pathWithWildcard)) {
			writer.serializeAsField(pojo, jgen, provider);
		} else if (!jgen.canOmitFields()) {
			writer.serializeAsOmittedField(pojo, jgen, provider);
		}
	}

	private String createPath(PropertyWriter writer, JsonGenerator jsonGenerator) {
		String SEPARATOR = ":";

		StringBuilder path = new StringBuilder();
		path.append(writer.getName());

		JsonStreamContext sc = jsonGenerator.getOutputContext();
		if (sc != null) {
			sc = sc.getParent();
		}
		while (sc != null) {
			if (sc.getCurrentName() != null) {
				if (path.length() > 0) {
					path.insert(0, SEPARATOR);
				}
				path.insert(0, sc.getCurrentName());
			}
			sc = sc.getParent();
		}

		return path.toString();
	}

	private String cratePathWithWildcard(String path) {
		String pathWildcard = "";
		int index = path.lastIndexOf(":");
		if (index > -1) {
			pathWildcard = path.substring(0, index + 1) + "*";
		}
		return pathWildcard;
	}

}
