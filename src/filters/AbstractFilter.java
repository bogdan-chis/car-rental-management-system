package filters;

public interface AbstractFilter<T> {
    public boolean accept(T entity);
}
