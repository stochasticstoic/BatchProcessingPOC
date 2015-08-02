package com.tecacet.movielens.easybatch;

import java.io.FileNotFoundException;
import java.util.Map;

import org.easybatch.core.api.Engine;
import org.easybatch.core.api.Report;
import org.easybatch.core.impl.EngineBuilder;
import org.easybatch.core.mapper.GenericRecordMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.stereotype.Component;

import com.tecacet.movielens.MongoConfig;
import com.tecacet.movielens.SpringConfig;
import com.tecacet.movielens.repository.UserRatingRepository;

@Component
public class ComputeMovieMetricsJob {

    private final UserRatingRepository ratingRepository;

    @Autowired
    public ComputeMovieMetricsJob(UserRatingRepository repository) {
        super();
        this.ratingRepository = repository;
    }

    @SuppressWarnings("unchecked")
    public Map<Long, MovieMetrics> computeMetrics() throws Exception {
        Engine engine = buildEngine();
        Report report = engine.call();
        return (Map<Long, MovieMetrics>) report.getBatchResult();
    }

    private Engine buildEngine() throws FileNotFoundException {

        MongoDBStreamReader reader = new MongoDBStreamReader(ratingRepository);

        Engine engine = new EngineBuilder().enableJMX(true).reader(reader).mapper(new GenericRecordMapper()).processor(new MovieRatingProcessor())
                .build();
        return engine;
    }

    public static void main(String[] args) throws Exception {
        ApplicationContext context = new AnnotationConfigApplicationContext(MongoConfig.class, SpringConfig.class);
        ComputeMovieMetricsJob job = context.getBean(ComputeMovieMetricsJob.class);
        MovieMetricsFileWriter fileWriter = context.getBean(MovieMetricsFileWriter.class);
        Map<Long, MovieMetrics> map =job.computeMetrics();
        fileWriter.writeMetrics(map);
    }

}