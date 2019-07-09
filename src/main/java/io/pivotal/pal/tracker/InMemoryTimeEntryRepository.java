package io.pivotal.pal.tracker;

import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.stream.Collectors;

public class InMemoryTimeEntryRepository implements TimeEntryRepository {
    Map<String, TimeEntry> repository = new HashMap<String, TimeEntry>();
    int index = 0;

    @Override
    public TimeEntry find(long timeEntryId) {
        String key = Long.toString(timeEntryId);
        return repository.containsKey(key) ? repository.get(key) : null;
    }

    @Override
    public TimeEntry create(TimeEntry timeEntry) {
        if (timeEntry.getId() <= 0) {
            index++;
            timeEntry.setId(index);
        }
        repository.put(Long.toString(timeEntry.getId()), timeEntry);

        return timeEntry;
    }

    @Override
    public List<TimeEntry> list() {
        return repository.values().stream().collect(Collectors.toList());
    }

    @Override
    public TimeEntry update(long id, TimeEntry timeEntry) {
        timeEntry.setId(id);
        TimeEntry result = repository.replace(Long.toString(id), timeEntry);
        return null == result ? null : timeEntry;
    }

    @Override
    public TimeEntry delete(long id) {
        return repository.remove(Long.toString(id));
    }
}
