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

package com.hazelcast.jet.pipeline.bag;

/**
 * Javadoc pending.
 */
public class ThreeBags<E1, E2, E3> {
    private final Iterable<E1> bag1;
    private final Iterable<E2> bag2;
    private final Iterable<E3> bag3;

    public ThreeBags(Iterable<E1> bag1, Iterable<E2> bag2, Iterable<E3> bag3) {
        this.bag1 = bag1;
        this.bag2 = bag2;
        this.bag3 = bag3;
    }

    public Iterable<E1> bag1() {
        return bag1;
    }

    public Iterable<E2> bag2() {
        return bag2;
    }

    public Iterable<E3> bag3() {
        return bag3;
    }
}