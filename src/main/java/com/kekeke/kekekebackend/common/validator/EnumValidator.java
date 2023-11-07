package com.kekeke.kekekebackend.common.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class EnumValidator implements ConstraintValidator<Enum, String> {

	private Enum annotation;

	@Override
	public void initialize(Enum constraintAnnotation) {
		this.annotation = constraintAnnotation;
	}

	@Override
	public boolean isValid(String value, ConstraintValidatorContext context) {
		java.lang.Enum[] enumValues = this.annotation.target().getEnumConstants();

		if (enumValues == null) {
			return false;
		}
		for (java.lang.Enum enumValue : enumValues) {
			if (value.equals(enumValue.toString())
					|| (this.annotation.ignoreCase() && value.equalsIgnoreCase(enumValue.toString()))) {
				return true;
			}
		}
		return false;
	}
}

