package com.googlecode.mjorm.query.criteria;

import java.util.regex.Pattern;

import com.googlecode.mjorm.ObjectMapper;

public class RegexCriterion
	extends AbstractCriterion {

	private Pattern pattern;

	public RegexCriterion(Pattern pattern) {
		this.pattern = pattern;
	}

	public RegexCriterion(String regex, int flags) {
		this(Pattern.compile(regex, flags));
	}

	public RegexCriterion(String regex) {
		this(Pattern.compile(regex));
	}

	@Override
	public Object toQueryObject(ObjectMapper mapper) {
		return pattern;
	}

}
