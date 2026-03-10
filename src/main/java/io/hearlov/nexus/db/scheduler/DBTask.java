package io.hearlov.nexus.db.scheduler;

import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public record DBTask(
        String query,
        Object[] params,
        Consumer<List<Map<String, Object>>> callback,
        Consumer<List<Map<String, Object>>> maincallback
) {}