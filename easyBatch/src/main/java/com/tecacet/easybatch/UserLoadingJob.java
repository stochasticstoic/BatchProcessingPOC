package com.tecacet.easybatch;

import java.io.File;

import org.easybatch.core.api.Engine;
import org.easybatch.core.api.Report;
import org.easybatch.core.impl.EngineBuilder;
import org.easybatch.flatfile.DelimitedRecordMapper;
import org.easybatch.flatfile.FlatFileRecordReader;
import org.easybatch.validation.BeanValidationRecordValidator;
import org.elasticsearch.client.Client;
import org.elasticsearch.node.Node;

import com.tecacet.elastic.ElasticServer;
import com.tecacet.movielens.model.User;

public class UserLoadingJob {

    private static final String USER_FILENAME = "../ml-100k/u.user";
    
    private final ElasticServer elasticServer = new ElasticServer();
    
    public void indexUsers() throws Exception {
        //start embedded elastic search node
        Node node = elasticServer.startEmbeddedNode();
        Client client = node.client();
        
        DelimitedRecordMapper<User> recordMapper = new DelimitedRecordMapper<User>(User.class, new String[]{"id","age","gender","occupation","zipCode"});
        recordMapper.setDelimiter("|");
        Engine engine = new EngineBuilder()
        .enableJMX(true)
        .reader(new FlatFileRecordReader(new File(USER_FILENAME)))
                .mapper(recordMapper)
                .validator(new BeanValidationRecordValidator<User>())
                 .processor(new UserClassifyingProcessor())
                 .processor(new JsonTransformingProcessor())
                 .processor(new UserIndexer(client))
                 .recordProcessorEventListener(new LoggingEventListener())
                .build();
        Report report = engine.call();
        
        //shutdown elastic search node
        elasticServer.stopEmbeddedNode(node);
    }
    
    public static void main(String[] args) throws Exception {
        new UserLoadingJob().indexUsers();
    
    }
}
