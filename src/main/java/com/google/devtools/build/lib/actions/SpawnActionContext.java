// Copyright 2014 The Bazel Authors. All rights reserved.
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//    http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
package com.google.devtools.build.lib.actions;

import com.google.common.collect.ImmutableList;

/**
 * A context that allows execution of {@link Spawn} instances.
 */
@ActionContextMarker(name = "spawn")
public interface SpawnActionContext extends ActionContext {

  /**
   * Executes the given spawn and returns metadata about the execution. Implementations must
   * guarantee that the first list entry represents the successful execution of the given spawn (if
   * no execution was successful, the method must throw an exception instead). The list may contain
   * further entries for (unsuccessful) retries as well as tree artifact management (which may
   * require additional spawn executions).
   */
  ImmutableList<SpawnResult> exec(Spawn spawn, ActionExecutionContext actionExecutionContext)
      throws ExecException, InterruptedException;

  /**
   * Executes the given spawn, possibly asynchronously, and returns a SpawnContinuation to represent
   * the execution. Otherwise all requirements from {@link #exec} apply.
   */
  default SpawnContinuation beginExecution(
      Spawn spawn, ActionExecutionContext actionExecutionContext) throws InterruptedException {
    try {
      return SpawnContinuation.immediate(exec(spawn, actionExecutionContext));
    } catch (ExecException e) {
      return SpawnContinuation.failedWithExecException(e);
    }
  }

  /** Returns whether this SpawnActionContext supports executing the given Spawn. */
  boolean canExec(Spawn spawn, ActionExecutionContext actionExecutionContext);
}
