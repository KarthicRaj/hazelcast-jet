/*
 * Copyright (c) 2008-2017, Hazelcast, Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.hazelcast.jet.pipeline;

import com.hazelcast.jet.pipeline.impl.transform.JoinTransform;
import com.hazelcast.jet.pipeline.impl.PipelineImpl;
import com.hazelcast.jet.pipeline.bag.BagsByTag;
import com.hazelcast.jet.pipeline.bag.Tag;
import com.hazelcast.jet.pipeline.tuple.Tuple2;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import static com.hazelcast.jet.pipeline.bag.Tag.tag;
import static com.hazelcast.jet.pipeline.bag.Tag.tag0;
import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.toList;

/**
 * Javadoc pending.
 */
public class JoinBuilder<E0> {
    private final Map<Tag<?>, JoinClause<?, E0, ?>> clauses = new HashMap<>();

    JoinBuilder(ComputeStage<E0> leftStream) {
        add(leftStream, null);
    }

    public <K, E_RIGHT> Tag<E_RIGHT> add(ComputeStage<E_RIGHT> s, JoinOn<K, E0, E_RIGHT> joinOn) {
        Tag<E_RIGHT> tag = tag(clauses.size());
        clauses.put(tag, new JoinClause<>(s, joinOn));
        return tag;
    }

    @SuppressWarnings("unchecked")
    public ComputeStage<Tuple2<E0, BagsByTag>> build() {
        List<Entry<Tag<?>, JoinClause<?, E0, ?>>> orderedClauses = orderedClauses();
        List<ComputeStage> upstream = orderedClauses.stream()
                                                    .map(e -> e.getValue().pstream())
                                                    .collect(toList());
        JoinTransform transform = new JoinTransform(
                orderedClauses.stream()
                              .skip(1)
                              .map(e -> e.getValue().joinOn())
                              .collect(toList()),
                orderedClauses.stream()
                              .skip(1)
                              .map(Entry::getKey)
                              .collect(toList())
        );
        PipelineImpl pipeline = (PipelineImpl) clauses.get(tag0()).pstream().getPipeline();
        return pipeline.attach(upstream, transform);
    }

    private List<Entry<Tag<?>, JoinClause<?, E0, ?>>> orderedClauses() {
        return clauses.entrySet().stream()
                      .sorted(comparing(Entry::getKey))
                      .collect(toList());
    }

    private static class JoinClause<K, E0, E_RIGHT> {
        private final ComputeStage<E_RIGHT> pstream;
        private final JoinOn<K, E0, E_RIGHT> joinOn;

        JoinClause(ComputeStage<E_RIGHT> pstream, JoinOn<K, E0, E_RIGHT> joinOn) {
            this.pstream = pstream;
            this.joinOn = joinOn;
        }

        ComputeStage<E_RIGHT> pstream() {
            return pstream;
        }

        JoinOn<K, E0, E_RIGHT> joinOn() {
            return joinOn;
        }
    }
}