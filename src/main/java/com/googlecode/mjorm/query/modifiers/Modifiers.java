package com.googlecode.mjorm.query.modifiers;

import java.util.Collection;

import com.googlecode.mjorm.query.modifiers.BitwiseModifier.Operation;

/**
 * Utility class for easily creating {@link Modifier}s.
 */
public class Modifiers {

	/**
	 * {@see AddToSetEachModifier}
	 */
	public static <T> AddToSetEachModifier addToSetEach(T[] values) {
		return new AddToSetEachModifier(values);
	}

	/**
	 * {@see AddToSetEachModifier}
	 */
	public static <T> AddToSetEachModifier addToSetEach(Collection<T> values) {
		return new AddToSetEachModifier(values);
	}

	/**
	 * {@see AddToSetModifier}
	 */
	public static <T> AddToSetModifier addToSet(T value) {
		return new AddToSetModifier(value);
	}

	/**
	 * {@see BitwiseModifier}
	 */
	public static BitwiseModifier bitwise(Operation operation, Number value) {
		return new BitwiseModifier(operation, value);
	}

	/**
	 * {@see BitwiseModifier}
	 */
	public static BitwiseModifier bitwiseOr(Number value) {
		return bitwise(Operation.OR, value);
	}

	/**
	 * {@see BitwiseModifier}
	 */
	public static BitwiseModifier bitwiseAnd(Number value) {
		return bitwise(Operation.AND, value);
	}

	/**
	 * {@see IncrementModifier}
	 */
	public static IncrementModifier inc(Number value) {
		return new IncrementModifier(value);
	}

	/**
	 * {@see PopModifier}
	 */
	public static PopModifier pop() {
		return new PopModifier();
	}

	/**
	 * {@see PullAllModifier}
	 */
	public static <T> PullAllModifier pullAll(T[] values) {
		return new PullAllModifier(values);
	}

	/**
	 * {@see PullAllModifier}
	 */
	public static <T> PullAllModifier pullAll(Collection<T> values) {
		return new PullAllModifier(values);
	}

	/**
	 * {@see PullModifier}
	 */
	public static <T> PullModifier pull(T value) {
		return new PullModifier(value);
	}

	/**
	 * {@see PushAllModifier}
	 */
	public static <T> PushAllModifier pushAll(T[] values) {
		return new PushAllModifier(values);
	}

	/**
	 * {@see PushAllModifier}
	 */
	public static <T> PushAllModifier pushAll(Collection<T> values) {
		return new PushAllModifier(values);
	}

	/**
	 * {@see PushModifier}
	 */
	public static <T> PushModifier push(T value) {
		return new PushModifier(value);
	}

	/**
	 * {@see RenameModifier}
	 */
	public static RenameModifier rename(String value) {
		return new RenameModifier(value);
	}

	/**
	 * {@see SetModifier}
	 */
	public static <T> SetModifier set(T value) {
		return new SetModifier(value);
	}

	/**
	 * {@see ShiftModifier}
	 */
	public static ShiftModifier shift() {
		return new ShiftModifier();
	}

	/**
	 * {@see UnSetModifier}
	 */
	public static UnSetModifier unset() {
		return new UnSetModifier();
	}
}
