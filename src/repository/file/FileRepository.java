package repository.file;

import domain.Identifiable;
import exceptions.FileException;
import repository.memory.MemoryRepository;

import java.util.Optional;

public abstract class FileRepository<ID, T extends Identifiable<ID>> extends MemoryRepository<ID, T> {
    protected String filename;

    public FileRepository(String filename) throws FileException{
        this.filename = filename;
    }

    protected abstract void readFromFile() throws FileException;
    protected abstract void writeToFile();

    @Override
    public void add(ID id, T entity){
        super.add(id, entity);
        writeToFile();
    }

    @Override
    public Optional<T> delete(ID id){
        Optional<T> deletedElement =  super.delete(id);
        writeToFile();
        return deletedElement;
    }

    @Override
    public void modify(ID id, T entity){
        super.modify(id, entity);
        writeToFile();
    }

}
