package com.tecacet.movielens.easybatch;

import static org.junit.Assert.assertEquals;

import javax.annotation.Resource;

import org.easybatch.core.job.JobReport;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.tecacet.movielens.MongoConfig;
import com.tecacet.movielens.SpringConfig;
import com.tecacet.movielens.repository.MovieRepository;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { SpringConfig.class, MongoConfig.class })
public class MovieLoadingJobTest {

	@Resource
	private MovieLoadingJob movieLoadingJob;

	@Resource
	private MovieRepository movieRepository;

	@Test
	public void testReadMovies() throws Exception {
		JobReport jobReport = movieLoadingJob.readMovies();
		System.out.println(jobReport);
		//TODO assertEquals(540, movieRepository.count());
		movieRepository.deleteAll();
	}

}
