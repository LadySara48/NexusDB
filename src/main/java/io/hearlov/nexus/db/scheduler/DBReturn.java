package io.hearlov.nexus.db.scheduler;

import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public record DBReturn(Consumer<List<Map<String, Object>>> callback, List<Map<String, Object>> object){}
