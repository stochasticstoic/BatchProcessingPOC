package com.tecacet.movielens.easybatch;

import java.util.Date;
import java.util.Iterator;
import java.util.stream.Stream;

import org.easybatch.core.record.Header;
import org.easybatch.core.record.Record;
import org.easybatch.core.reader.RecordReader;
import org.easybatch.core.reader.RecordReaderClosingException;
import org.easybatch.core.reader.RecordReaderOpeningException;
import org.easybatch.core.reader.RecordReadingException;
import org.easybatch.core.record.GenericRecord;
import org.springframework.beans.factory.annotation.Autowired;

import com.tecacet.movielens.model.UserRating;
import com.tecacet.movielens.repository.UserRatingRepository;

//TODO make generic
public class MongoDBStreamReader implements RecordReader {

    private final UserRatingRepository repository;
    
    @Autowired
    public MongoDBStreamReader(UserRatingRepository repository) {
        super();
        this.repository = repository;
    }

    private Stream<UserRating> stream;
    private Iterator<UserRating> iterator;
    private long count;
    
    private long current = 0;
    
    @Override
    public void open() throws RecordReaderOpeningException {
        stream = repository.findAllAndStream();
        count = repository.count();
        iterator = stream.iterator();
        
    }

    @Override
    public boolean hasNextRecord() {
        return iterator.hasNext();
    }

    @Override
    public Record<UserRating> readNextRecord() throws RecordReadingException {
       UserRating rating = iterator.next();
       return new GenericRecord<UserRating>(new Header(++current, getDataSourceName(), new Date()), rating);
    }

    @Override
    public Long getTotalRecords() {
        return count;
    }

    @Override
    public String getDataSourceName() {
        // TODO
        return "mongoDB collection";
    }

    @Override
    public void close() throws RecordReaderClosingException {
        stream.close();
    }

}
