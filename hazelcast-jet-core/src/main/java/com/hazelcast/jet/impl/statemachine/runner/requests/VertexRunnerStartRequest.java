/*
 * Copyright (c) 2008-2016, Hazelcast, Inc. All Rights Reserved.
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

package com.hazelcast.jet.impl.statemachine.runner.requests;

import com.hazelcast.jet.impl.runtime.VertexRunnerEvent;
import com.hazelcast.jet.impl.statemachine.StateMachineRequest;

/**
 * State-machine request which start vertex runners;
 */
public class VertexRunnerStartRequest implements StateMachineRequest<VertexRunnerEvent, Void> {
    @Override
    public VertexRunnerEvent getEvent() {
        return VertexRunnerEvent.START;
    }

    @Override
    public Void getPayload() {
        return null;
    }
}