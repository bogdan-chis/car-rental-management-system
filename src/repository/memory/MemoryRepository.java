package repository.memory;

import domain.Identifiable;
import repository.base.IRepository;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Optional;

public class MemoryRepository<ID, T extends Identifiable<ID>> implements IRepository<ID,T> {
    protected HashMap<ID, T> elements;

    public MemoryRepository(){
        elements = new HashMap<>();
    }

    @Override
    public void add(ID id, T element) {
        elements.put(id, element);
    }

    @Override
    public Optional<T> delete(ID id) {
        return Optional.ofNullable(elements.remove(id));
    }

    @Override
    public void modify(ID id, T element) {
        elements.put(id, element);
    }

    @Override
    public Optional<T> findById(ID id) {
        return Optional.ofNullable(elements.get(id));
    }

    @Override
    public Iterable<T> getAll(){
        return elements.values();
    }

    @Override
    public Iterator<T> getIterator() {
        return elements.values().iterator();
    }

    @Override
    public int size(){return elements.size();}
}
