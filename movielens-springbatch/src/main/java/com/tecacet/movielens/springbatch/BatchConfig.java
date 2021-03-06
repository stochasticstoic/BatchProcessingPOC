package com.tecacet.movielens.springbatch;

import com.tecacet.movielens.model.Movie;
import com.tecacet.movielens.model.User;
import com.tecacet.movielens.model.UserRating;
import com.tecacet.movielens.repository.MovieRepository;
import com.tecacet.movielens.repository.UserRatingRepository;
import com.tecacet.movielens.repository.UserRepository;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.launch.support.SimpleJobLauncher;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.repository.support.MapJobRepositoryFactoryBean;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.data.RepositoryItemWriter;
import org.springframework.batch.item.support.PassThroughItemProcessor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan
@EnableBatchProcessing
public class BatchConfig {

    @Bean
    public JobRepository jobRepository() throws Exception {
        return new MapJobRepositoryFactoryBean().getObject();
    }

    @Bean
    public JobLauncher jobLauncher(JobRepository jobRepository) {
        SimpleJobLauncher jobLauncher = new SimpleJobLauncher();
        jobLauncher.setJobRepository(jobRepository);
        return jobLauncher;
    }

    @Bean
    public ItemProcessor<?, ?> processor() {
        return new PassThroughItemProcessor<>();
    }

    @Bean
    public ItemWriter<User> userWritter(UserRepository userRepository) {
        RepositoryItemWriter<User> itemWriter = new RepositoryItemWriter<>();
        itemWriter.setRepository(userRepository);
        itemWriter.setMethodName("save");
        return itemWriter;
    }

    @Bean
    public ItemWriter<Movie> movieWritter(MovieRepository repository) {
        RepositoryItemWriter<Movie> itemWriter = new RepositoryItemWriter<>();
        itemWriter.setRepository(repository);
        itemWriter.setMethodName("insert");
        return itemWriter;
    }

    @Bean
    public ItemWriter<UserRating> ratingWritter(UserRatingRepository repository) {
        RepositoryItemWriter<UserRating> itemWriter = new RepositoryItemWriter<>();
        itemWriter.setRepository(repository);
        itemWriter.setMethodName("save");
        return itemWriter;
    }

    @Bean(name = "importUserStep")
    public Step importUserStep(StepBuilderFactory stepBuilderFactory, UserItemReader reader, ItemWriter<User> writer,
            ItemProcessor<User, User> processor) {
        return stepBuilderFactory.get("importUserStep").<User, User>chunk(10).reader(reader).processor(processor)
                .writer(writer).build();
    }

    @Bean(name = "importMovieStep")
    public Step importMovieStep(StepBuilderFactory stepBuilderFactory, MovieItemReader reader,
            ItemWriter<Movie> writer,
            ItemProcessor<Movie, Movie> processor) {
        return stepBuilderFactory.get("importMovieStep").<Movie, Movie>chunk(10).reader(reader).processor(processor)
                .writer(writer).build();
    }

    @Bean(name = "importRatingStep")
    public Step importRatingStep(StepBuilderFactory stepBuilderFactory, RatingItemReader reader,
            ItemWriter<UserRating> writer, ItemProcessor<UserRating, UserRating> processor) {
        return stepBuilderFactory.get("importRatingStep").<UserRating, UserRating>chunk(1000).reader(reader)
                .processor(processor).writer(writer).build();
    }

    @Bean
    public Job importUserJob(JobBuilderFactory jobs, Step importUserStep) {
        return jobs.get("importUserJob").incrementer(new RunIdIncrementer())
                // .listener(listener)
                .flow(importUserStep).end().build();
    }

    @Bean
    public Job importMovieJob(JobBuilderFactory jobs, Step importMovieStep) {
        return jobs.get("importMovieJob").incrementer(new RunIdIncrementer())
                // .listener(listener)
                .flow(importMovieStep).end().build();
    }

    @Bean
    public Job importRatingsJob(JobBuilderFactory jobs, Step importRatingStep) {
        return jobs.get("importRatingsJob").incrementer(new RunIdIncrementer())
                // .listener(listener)
                .flow(importRatingStep).end().build();
    }

}
