package com.tecacet.movielens.springbatch;

import com.tecacet.movielens.model.Movie;
import com.tecacet.movielens.springbatch.converter.LocalDatePropertyEditor;

import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.core.io.FileSystemResource;
import org.springframework.stereotype.Component;

import java.beans.PropertyEditor;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

@Component
@StepScope
public class MovieItemReader extends FlatFileItemReader<Movie> {

    private static final String MOVIE_FILENAME = "../ml-100k/u.item";

    private final String[] properties = new String[] {"id", "title", "releaseDate", "videoReleaseDate", "IMDBurl"};

    public MovieItemReader() {
        Map<Class<?>, PropertyEditor> customEditors = new HashMap<>();
        customEditors.put(LocalDate.class, new LocalDatePropertyEditor());

        DelimitedLineTokenizer lineTokenizer = new DelimitedLineTokenizer("|");
        lineTokenizer.setNames(properties);
        lineTokenizer.setStrict(false);
        setResource(new FileSystemResource(MOVIE_FILENAME));
        DefaultLineMapper<Movie> lineMapper = new DefaultLineMapper<>();
        lineMapper.setLineTokenizer(lineTokenizer);
        BeanWrapperFieldSetMapper<Movie> fieldSetMapper = new BeanWrapperFieldSetMapper<>();
        fieldSetMapper.setTargetType(Movie.class);
        fieldSetMapper.setCustomEditors(customEditors);
        lineMapper.setFieldSetMapper(fieldSetMapper);
        setLineMapper(lineMapper);
    }
}
