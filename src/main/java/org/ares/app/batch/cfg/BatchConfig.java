package org.ares.app.batch.cfg;

import javax.annotation.Resource;
import javax.sql.DataSource;

import org.ares.app.batch.listener.BizJobListener;
import org.ares.app.batch.model.BizModel;
import org.ares.app.batch.processor.BizItemProcessor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.launch.support.SimpleJobLauncher;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.repository.support.JobRepositoryFactoryBean;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.BeanPropertyItemSqlParameterSourceProvider;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.JdbcCursorItemReader;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@EnableBatchProcessing
public class BatchConfig {
	
    @Bean
    public JobRepository jobRepository(DataSource dataSource, PlatformTransactionManager transactionManager)
    throws Exception {
        JobRepositoryFactoryBean jobRepositoryFactoryBean = new JobRepositoryFactoryBean();
        jobRepositoryFactoryBean.setDataSource(dataSource);
        jobRepositoryFactoryBean.setTransactionManager(transactionManager);
        jobRepositoryFactoryBean.setDatabaseType("mysql");
        return jobRepositoryFactoryBean.getObject();
    }
    
    @Bean
    public SimpleJobLauncher jobLauncher(DataSource dataSource, PlatformTransactionManager transactionManager)
    throws Exception {
        SimpleJobLauncher jobLauncher = new SimpleJobLauncher();
        jobLauncher.setJobRepository(jobRepository(dataSource, transactionManager));
        return jobLauncher;
    }
    
    @Bean
    public Job importJob(JobBuilderFactory jobs, Step s1) {
        return jobs.get("importJob")
        .incrementer(new RunIdIncrementer())
        .flow(s1)
        .end()
        .listener(bizJobLisener)
        .build();
    }
    
    @Bean
    public Step step1(StepBuilderFactory stepBuilderFactory, ItemReader<BizModel> reader, ItemWriter<BizModel> writer,
    ItemProcessor<BizModel,BizModel> processor) {
        return stepBuilderFactory
        .get("step1")
        .<BizModel, BizModel>chunk(65000)
        .reader(reader)
        .processor(processor)
        .writer(writer)
        .build();
    }
    
    @Bean
    public ItemReader<BizModel> reader(DataSource dataSource) throws Exception {
    	JdbcCursorItemReader<BizModel> itemReader = new JdbcCursorItemReader<BizModel>();
    	itemReader.setDataSource(dataSource);
    	itemReader.setSql("select * from  b_msg");
    	itemReader.setRowMapper(new  BeanPropertyRowMapper<BizModel>(BizModel.class));//要转换成的bean
    	ExecutionContext executionContext = new ExecutionContext();
    	itemReader.open(executionContext);
    	Object customerCredit = new Object();
    	while(customerCredit != null){
    	    customerCredit = itemReader.read();
    	}
    	itemReader.close();
    	return itemReader;
    }
    
    @Bean
    public ItemProcessor<BizModel, BizModel> processor() {
        return new BizItemProcessor();
    }
    
    @Bean
    public ItemWriter<BizModel> writer(DataSource dataSource) {
    	JdbcBatchItemWriter<BizModel> writer = new JdbcBatchItemWriter<BizModel>();
        writer.setItemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<BizModel>());
        String sql = "insert into d_msg(name, msg) values (:name, :msg)";
        writer.setSql(sql);
        writer.setDataSource(dataSource);
        return writer;
    }
    
    @Bean
    public JobExecutionListener bizJobListener() {
        return new BizJobListener();
    }
    
    @Resource JobExecutionListener bizJobLisener;
}
