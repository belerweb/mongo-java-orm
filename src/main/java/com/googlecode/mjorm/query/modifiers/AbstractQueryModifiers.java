package com.googlecode.mjorm.query.modifiers;

import java.util.Collection;

import com.googlecode.mjorm.query.modifiers.AbstractModifierBuilder;
import com.googlecode.mjorm.query.modifiers.BitwiseModifier.Operation;

public abstract class AbstractQueryModifiers<T extends AbstractQueryModifiers<T>>
	extends AbstractModifierBuilder<T>{

	/**
	 * {@see AddToSetEachModifier}
	 */
	public <V> T addToSetEach(String property, V[] values) {
		return add(property, Modifiers.addToSetEach(values));
	}

	/**
	 * {@see AddToSetEachModifier}
	 */
	public <V> T addToSetEach(String property, Collection<V>values) {
		return add(property, Modifiers.addToSetEach(values));
	}

	/**
	 * {@see AddToSetModifier}
	 */
	public <V> T addToSet(String property, V value) {
		return add(property, Modifiers.addToSet(value));
	}

	/**
	 * {@see BitwiseModifier}
	 */
	public T bitwise(String property, Operation operation, Number value) {
		return add(property, Modifiers.bitwise(operation, value));
	}

	/**
	 * {@see BitwiseModifier}
	 */
	public T bitwiseOr(String property, Number value) {
		return add(property, Modifiers.bitwise(Operation.OR, value));
	}

	/**
	 * {@see BitwiseModifier}
	 */
	public T bitwiseAnd(String property, Number value) {
		return add(property, Modifiers.bitwise(Operation.AND, value));
	}

	/**
	 * {@see IncrementModifier}
	 */
	public T inc(String property, Number value) {
		return add(property, Modifiers.inc(value));
	}

	/**
	 * {@see PopModifier}
	 */
	public T pop(String property) {
		return add(property, Modifiers.pop());
	}

	/**
	 * {@see PullAllModifier}
	 */
	public <V> T pullAll(String property, V[] values) {
		return add(property, Modifiers.pullAll(values));
	}

	/**
	 * {@see PullAllModifier}
	 */
	public <V> T pullAll(String property, Collection<V>values) {
		return add(property, Modifiers.pullAll(values));
	}

	/**
	 * {@see PullModifier}
	 */
	public <V> T pull(String property, V value) {
		return add(property, Modifiers.pull(value));
	}

	/**
	 * {@see PushAllModifier}
	 */
	public <V> T pushAll(String property, V[] values) {
		return add(property, Modifiers.pushAll(values));
	}

	/**
	 * {@see PushAllModifier}
	 */
	public <V> T pushAll(String property, Collection<V>values) {
		return add(property, Modifiers.pushAll(values));
	}

	/**
	 * {@see PushModifier}
	 */
	public <V> T push(String property, V value) {
		return add(property, Modifiers.push(value));
	}

	/**
	 * {@see RenameModifier}
	 */
	public T rename(String property, String value) {
		return add(property, Modifiers.rename(value));
	}

	/**
	 * {@see SetModifier}
	 */
	public <V> T set(String property, V value) {
		return add(property, Modifiers.set(value));
	}

	/**
	 * {@see ShiftModifier}
	 */
	public T shift(String property) {
		return add(property, Modifiers.shift());
	}

	/**
	 * {@see UnSetModifier}
	 */
	public <V> T unset(String property) {
		return add(property, Modifiers.unset());
	}

}
