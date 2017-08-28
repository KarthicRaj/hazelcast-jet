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

import com.hazelcast.jet.Traverser;
import com.hazelcast.jet.aggregate.AggregateOperation1;
import com.hazelcast.jet.aggregate.AggregateOperation2;
import com.hazelcast.jet.aggregate.AggregateOperation3;
import com.hazelcast.jet.function.DistributedFunction;
import com.hazelcast.jet.function.DistributedPredicate;
import com.hazelcast.jet.pipeline.impl.transform.CoGroupTransform;
import com.hazelcast.jet.pipeline.impl.transform.JoinTransform;
import com.hazelcast.jet.pipeline.tuple.Tuple2;
import com.hazelcast.jet.pipeline.tuple.Tuple3;

import java.util.Collection;
import java.util.List;
import java.util.Map.Entry;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;

public interface ComputeStage<E> extends Stage {
    <R> ComputeStage<R> attach(UnaryTransform<? super E, R> unaryTransform);

    <R> ComputeStage<R> attach(MultiTransform<R> multiTransform, List<ComputeStage> moreInputs);

    EndStage drainTo(Sink sink);

    default <R> ComputeStage<R> map(DistributedFunction<? super E, ? extends R> mapF) {
        return attach(Transforms.map(mapF));
    }

    default ComputeStage<E> filter(DistributedPredicate<E> filterF) {
        return attach(Transforms.filter(filterF));
    }
    default <R> ComputeStage<R> flatMap(DistributedFunction<? super E, Traverser<? extends R>> flatMapF) {
        return attach(Transforms.flatMap(flatMapF));
    }

    default <K, R> ComputeStage<Entry<K, R>> groupBy(
            DistributedFunction<? super E, ? extends K> keyF, AggregateOperation1<E, ?, R> aggrOp
    ) {
        return attach(Transforms.groupBy(keyF, aggrOp));
    }

    @SuppressWarnings("unchecked")
    default <K, E1> ComputeStage<Tuple2<E, Collection<E1>>> join(
            ComputeStage<E1> s1, JoinOn<K, E, E1> joinOn
    ) {
        return attach(new JoinTransform(singletonList(joinOn), emptyList()), singletonList(s1));
    }

    @SuppressWarnings("unchecked")
    default <K1, E1, K2, E2> ComputeStage<Tuple3<E, Collection<E1>, Collection<E2>>> join(
            ComputeStage<E1> s1, JoinOn<K1, E, E1> joinOn1,
            ComputeStage<E2> s2, JoinOn<K2, E, E2> joinOn2
    ) {
        return attach(new JoinTransform(asList(joinOn1, joinOn2), emptyList()), asList(s1, s2));
    }

    default JoinBuilder<E> joinBuilder() {
        return new JoinBuilder<>(this);
    }

    @SuppressWarnings("unchecked")
    default <K, A, E1, R> ComputeStage<Tuple2<K, R>> coGroup(
            DistributedFunction<? super E, ? extends K> thisKeyF,
            ComputeStage<E1> s1, DistributedFunction<? super E1, ? extends K> key1F,
            AggregateOperation2<E, E1, A, R> aggrOp
    ) {
        return attach(new CoGroupTransform<>(asList(thisKeyF, key1F), aggrOp), singletonList(s1));
    }

    @SuppressWarnings("unchecked")
    default <K, A, E1, E2, R> ComputeStage<Tuple2<K, R>> coGroup(
            DistributedFunction<? super E, ? extends K> thisKeyF,
            ComputeStage<E1> s1, DistributedFunction<? super E1, ? extends K> key1F,
            ComputeStage<E2> s2, DistributedFunction<? super E2, ? extends K> key2F,
            AggregateOperation3<E, E1, E2, A, R> aggrOp
    ) {
        return attach(new CoGroupTransform<>(asList(thisKeyF, key1F, key2F), aggrOp), asList(s1, s2));
    }

    default <K> CoGroupBuilder<K, E> coGroupBuilder(DistributedFunction<? super E, K> thisKeyF) {
        return new CoGroupBuilder<>(this, thisKeyF);
    }
}