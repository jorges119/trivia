package com.onesockpirates.quad.assignment.trivia.managers;

public interface IStorageManager<T> {
	public void intialize(String name, Class<T> type);
    public T save(T in);
    public T query(String queryFilter);
    public void update(String queryFilter, T updated);
}
