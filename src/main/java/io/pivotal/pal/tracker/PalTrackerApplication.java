package io.pivotal.pal.tracker;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;

import javax.sql.DataSource;

@SpringBootApplication
public class PalTrackerApplication {


    @Bean
    @ConditionalOnClass(DataSource.class)
    @ConditionalOnProperty()
    public TimeEntryRepository jdbcRepository(DataSource datasource){
        return new JdbcTimeEntryRepository(datasource);
    }


    @Bean
    @ConditionalOnMissingClass("javax.sql.DataSource")
    public TimeEntryRepository timeEntryRepository() {
        return new InMemoryTimeEntryRepository();
    }


    public static void main(String[] args) {
        SpringApplication.run(PalTrackerApplication.class, args);

    }


}
