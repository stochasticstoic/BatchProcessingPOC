package com.tecacet.movielens.easybatch;

import com.tecacet.movielens.easybathc.converter.GenderTypeConverter;
import com.tecacet.movielens.easybathc.converter.OccupationTypeConverter;
import com.tecacet.movielens.model.User;

import org.easybatch.core.job.Job;
import org.easybatch.core.job.JobBuilder;
import org.easybatch.core.job.JobExecutor;
import org.easybatch.core.job.JobReport;
import org.easybatch.flatfile.DelimitedRecordMapper;
import org.easybatch.flatfile.FlatFileRecordReader;
import org.easybatch.validation.BeanValidationRecordValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;

@Component
public class UserLoadingJob {

    private static final String USER_FILENAME = "../ml-100k/u.user";

    private final UserLoadingProcessor userLoadingProcessor;
    private final JobExecutor jobExecutor = new JobExecutor();

    @Autowired
    public UserLoadingJob(UserLoadingProcessor userLoadingProcessor) {
        super();
        this.userLoadingProcessor = userLoadingProcessor;
    }

    public JobReport readUsers() throws IOException {
        Job job = buildJob();
        return jobExecutor.execute(job);
    }

    private Job buildJob() throws IOException {
        DelimitedRecordMapper recordMapper = new DelimitedRecordMapper(User.class, "id", "age", "gender", "occupation",
                "zipCode");
        recordMapper.setDelimiter("|");
        recordMapper.registerTypeConverter(new GenderTypeConverter());
        recordMapper.registerTypeConverter(new OccupationTypeConverter());
        return JobBuilder.aNewJob().enableJmx(true).reader(new FlatFileRecordReader(new File(USER_FILENAME)))
                .mapper(recordMapper).validator(new BeanValidationRecordValidator()).processor(userLoadingProcessor)
                .build();
    }

}
