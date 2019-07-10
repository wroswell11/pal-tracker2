package io.pivotal.pal.tracker;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import javax.sql.DataSource;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.util.List;

import static java.sql.Statement.RETURN_GENERATED_KEYS;


public class JdbcTimeEntryRepository implements TimeEntryRepository {
    private JdbcTemplate template;

    private final RowMapper<TimeEntry> mapper = (rs, rowNum) -> new TimeEntry(
            rs.getLong("id"),
            rs.getLong("project_id"),
            rs.getLong("user_id"),
            rs.getDate("date").toLocalDate(),
            rs.getInt("hours")
    );

    private final ResultSetExtractor<TimeEntry> extractor =
            (rs) -> rs.next() ? mapper.mapRow(rs, 1) : null;


    public JdbcTimeEntryRepository(DataSource datasource) {
        template = new JdbcTemplate();
        template.setDataSource(datasource);
    }

    @Override
    public TimeEntry find(long id) {
        return template.query("select * from time_entries where id = ?",
                new Object[]{id},
                extractor);

    }

    @Override
    public TimeEntry create(TimeEntry timeEntry) {
        KeyHolder keyHolder = new GeneratedKeyHolder();

        template.update(
                connection -> {
                    PreparedStatement insertStatement = connection.prepareStatement(
                            "insert into time_entries " +
                                    "(project_id,user_id,date,hours) " +
                                    "values (?,?,?,?)",
                            RETURN_GENERATED_KEYS);

                    insertStatement.setLong(1, timeEntry.getProjectId());
                    insertStatement.setLong(2, timeEntry.getUserId());
                    insertStatement.setDate(3, Date.valueOf(timeEntry.getDate()));
                    insertStatement.setLong(4, timeEntry.getHours());

                    return insertStatement;
                },keyHolder);
        return find(keyHolder.getKey().longValue());
    }

    @Override
    public List<TimeEntry> list() {
        return template.query("select * from time_entries", mapper);
    }

    @Override
    public TimeEntry update(long id, TimeEntry timeEntry) {
        template.update("update time_entries set project_id = ?, user_id=?, date=?, hours=? where id=?",
                timeEntry.getProjectId(),
                timeEntry.getUserId(),
                Date.valueOf(timeEntry.getDate()),
                timeEntry.getHours(),id);

        return find(id);
    }

    @Override
    public void delete(long id) {
        template.update("delete from time_entries where id = ?", id);
    }
}
