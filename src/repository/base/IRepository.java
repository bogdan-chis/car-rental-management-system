package repository.base;

import domain.Identifiable;

import java.util.Iterator;
import java.util.Optional;

public interface IRepository<ID, T extends Identifiable<ID>> {
    void add(ID id, T object);
    Optional<T> delete(ID id);
    void modify(ID id, T object);
    Optional<T> findById(ID id);
    Iterable<T> getAll();
    Iterator<T> getIterator();
    int size();
}
