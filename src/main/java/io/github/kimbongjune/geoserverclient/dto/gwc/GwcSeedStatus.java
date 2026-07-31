package io.github.kimbongjune.geoserverclient.dto.gwc;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonAutoDetect;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * DTO for GWC seed task status. Maps {@code GET /gwc/rest/seed[.json|/{layer}.json]}.
 *
 * <p>Response format: JSON with key {@code "long-array-array"}, each entry is a
 * 5-element long array: {@code [tilesDone, tilesTotal, pendingCount, taskId, taskState]}.
 *
 * <p>taskState: -2=UNSET, -1=DEAD, 0=PENDING, 1=RUNNING, 2=DONE
 */
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
@JsonIgnoreProperties(ignoreUnknown = true)
public class GwcSeedStatus {

    @JsonProperty("long-array-array")
    private List<long[]> tasks = new ArrayList<long[]>();

    /** @return the raw task arrays (each entry: {@code [tilesDone, tilesTotal, pendingCount, taskId, taskState]}) */
    public List<long[]> getTasks() {
        return Collections.unmodifiableList(tasks);
    }

    /** @return {@code true} if there are no seed tasks */
    public boolean isEmpty() {
        return tasks.isEmpty();
    }

    /** Parsed view of a single seed task row (5-element long array). */
    public static class Task {
        /** Number of tiles completed. */
        public final long tilesDone;
        /** Total number of tiles to process. */
        public final long tilesTotal;
        /** Number of tasks pending. */
        public final long pendingCount;
        /** Unique task ID. */
        public final long taskId;
        /** Task state: -2=UNSET, -1=DEAD, 0=PENDING, 1=RUNNING, 2=DONE. */
        public final long taskState;

        /**
         * Constructs a {@code Task} from a raw 5-element array.
         * @param raw the raw 5-element array
         */
        public Task(long[] raw) {
            this.tilesDone = raw[0];
            this.tilesTotal = raw[1];
            this.pendingCount = raw[2];
            this.taskId = raw[3];
            this.taskState = raw[4];
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            Task that = (Task) o;
            return Objects.equals(tilesDone, that.tilesDone)
                    && Objects.equals(tilesTotal, that.tilesTotal)
                    && Objects.equals(pendingCount, that.pendingCount)
                    && Objects.equals(taskId, that.taskId)
                    && Objects.equals(taskState, that.taskState);
        }

        @Override
        public int hashCode() {
            return Objects.hash(tilesDone, tilesTotal, pendingCount, taskId, taskState);
        }

        @Override
        public String toString() {
            return "Task{" +
                    "tilesDone=" + tilesDone +
                    ", tilesTotal=" + tilesTotal +
                    ", pendingCount=" + pendingCount +
                    ", taskId=" + taskId +
                    ", taskState=" + taskState +
                    '}';
        }
    }

    /** @return the parsed task list */
    public List<Task> asTaskList() {
        List<Task> result = new ArrayList<Task>();
        for (long[] raw : tasks) {
            result.add(new Task(raw));
        }
        return result;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        GwcSeedStatus that = (GwcSeedStatus) o;
        if (tasks.size() != that.tasks.size()) {
            return false;
        }
        for (int i = 0; i < tasks.size(); i++) {
            if (!Arrays.equals(tasks.get(i), that.tasks.get(i))) {
                return false;
            }
        }
        return true;
    }

    @Override
    public int hashCode() {
        int result = 1;
        for (long[] task : tasks) {
            result = 31 * result + Arrays.hashCode(task);
        }
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("GwcSeedStatus{tasks=[");
        for (int i = 0; i < tasks.size(); i++) {
            if (i > 0) {
                sb.append(", ");
            }
            sb.append(Arrays.toString(tasks.get(i)));
        }
        return sb.append("]}").toString();
    }
}
